/*
 * GWT Portlets Framework (http://www.gwtportlets.org/)
 * Copyright 2009 Business Systems Group (Africa)
 *
 * This file is part of GWT Portlets.
 *
 * GWT Portlets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GWT Portlets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GWT Portlets.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gwtportlets.portlet.client.ui;

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import org.gwtportlets.portlet.client.*;
import org.gwtportlets.portlet.client.edit.SelectWidgetFactoryDialog;
import org.gwtportlets.portlet.client.event.AppEventListener;
import org.gwtportlets.portlet.client.event.EventManager;
import org.gwtportlets.portlet.client.event.WidgetChangeEvent;
import org.gwtportlets.portlet.client.event.WidgetConfigChangeEvent;
import org.gwtportlets.portlet.client.layout.*;
import org.gwtportlets.portlet.client.util.MoveAnimation;

import java.util.EventObject;
import java.util.List;

/**
 * Container with a title bar with buttons to refresh, maximize and so on.
 * Delegates to a LayoutPanel to hold its children.
 */
public class TitlePanel extends ContainerPortlet implements AppEventListener  {

    private String titleText = "Title";
    private boolean titleTextAuto = true;
    private boolean refreshEnabled = true;
    private boolean configureEnabled = true;
    private boolean maximizeEnabled = true;
    private boolean limitMaximize = true;
    private boolean editEnabled;

    private Portlet portlet;
    private int lastFlags = -1;

    private final LayoutPanel outer = new LayoutPanel(new DeckLayout());
    private final LayoutPanel main = new LayoutPanel(new RowLayout(true, 0));
    private final LayoutPanel deck = new LayoutPanel(new DeckLayout());
    private EdgeRow titleBar = new EdgeRow(new LayoutPanel(new RowLayout(false, 0)));
    private final LayoutPanel body = createBody();
    private Label titleLabel = new Label(titleText);

    private Container currentMaximizeStop;
    private OverlayPanel maxOverlay;

    public TitlePanel() {
        initWidget(outer);

        deck.add(body);
        main.add(titleBar);
        main.add(deck, new RowLayout.Constraints(RowLayout.Constraints.HIDDEN));
        outer.add(main);

        setStyleName("portlet-title");
    }

    public TitlePanel(Widget child) {
        this();
        add(child);
    }

    protected void onLoad() {
        lastFlags = -1;
        update();
    }

    public void setStyleName(String style) {
        super.setStyleName(style);
        main.setStyleName(style + "-main");
        titleLabel.setStyleName(style + "-text");
        deck.setStyleName(style + "-body");
        titleBar.setStyleName(style + "-header");
        Theme.get().updateTitleBar(titleBar);
        main.setLayoutConstraints(titleBar, new RowLayout.Constraints(
                titleBar.getHeight()));
    }

    private LayoutPanel createBody() {
        return new LayoutPanel(new RowLayout(false));
    }

    public void onAppEvent(EventObject ev) {
        if (ev instanceof WidgetChangeEvent
                && (refreshHelper != null || ev.getSource() == portlet)) {
            update();
        }
    }

    private void updateTitle(String s) {
        if (!titleLabel.getText().equals(s)) {
            titleLabel.setText(s);
            EventManager.get().broadcastUp(this, new WidgetChangeEvent(this));
        }
    }

    /**
     * Update our title and buttons etc.
     */
    public void update() {
        portlet = LayoutUtil.findPortletChild(body);
        if (titleTextAuto && portlet != null) {
            updateTitle(portlet.getWidgetTitle());
        } else {
            updateTitle(titleText);
        }

        int flags = getEnhancedFlags();
        flags = setBit(flags, Portlet.REFRESH,
                refreshEnabled && WidgetRefreshHook.App.get() != null);
        if (refreshHelper != null && refreshHelper.isRefreshBusy()) {
            flags |= Portlet.REFRESH_BUSY;
        }
        if (!configureEnabled) {
            flags = setBit(flags, Portlet.CONFIGURE, false);
        }
        flags = setBit(flags, Portlet.MAXIMIZE, maximizeEnabled);

        if (flags != lastFlags) {
            lastFlags = flags;
            titleBar.clear();
            addLeftButtons(flags);
            titleBar.add(titleLabel, LayoutConstraints.HIDDEN);
            addRightButtons(flags);
            titleBar.layout();

            if ((flags & Portlet.REFRESH_BUSY) != 0) {
                if (deck.getWidgetCount() == 1) {
                    deck.add(new LoadingWidget());
                    deck.layout();
                }
            } else if (deck.getWidgetCount() > 1) {
                for (int i = deck.getWidgetCount() - 1; i > 0; i--) {
                    deck.remove(i);
                }
                deck.layout();                
            }
        }
    }

    /**
     * Get the child portlet used for our title, flags and so on.
     */
    protected Portlet getPortlet() {
        return portlet;
    }

    /**
     * Get flags from our enhanced widget.
     */
    protected int getEnhancedFlags() {
        return (editEnabled ? CONFIGURE : 0)
                | (portlet != null ? portlet.getFlags() : 0);
    }

    private int setBit(int flags, int bit, boolean on) {
        return on ? flags | bit : flags & ~bit;
    }

    /**
     * Add buttons on the left side of the title.
     */
    protected void addLeftButtons(int flags) {
    }

    /**
     * Add a button to the title bar.
     */
    protected void addTitleButton(ToolButton b) {
        titleBar.add(b, new RowLayout.Constraints(b.getMaxWidth()));
    }

    /**
     * Add buttons on the right side of the title.
     */
    protected void addRightButtons(int flags) {
        if ((flags & Portlet.REFRESH) != 0) {
            addTitleButton(createRefresh());
        }
        if ((flags & Portlet.CONFIGURE) != 0) {
            addTitleButton(createConfigure());
        }
        if ((flags & Portlet.MAXIMIZE) != 0) {
            if (maxOverlay != null) {
                addTitleButton(createRestore());
            }
            if (getNextMaximizeStop() != null) {
                addTitleButton(createMaximize());
            }
        }
    }

    protected ToolButton createRefresh() {
        return new ToolButton(ToolButton.REFRESH, "Refresh",
                new ClickHandler() {
            public void onClick(ClickEvent event) {
                refresh();
            }
        });
    }

    protected ToolButton createConfigure() {
        return new ToolButton(ToolButton.CONFIGURE, "Configure",
                new ClickHandler() {
            public void onClick(ClickEvent event) {
                onConfigureClick();
            }
        });
    }

    protected ToolButton createMaximize() {
        return new ToolButton(ToolButton.MAXIMIZE, "Maximize", new ClickHandler() {
            public void onClick(ClickEvent event) {
                maximize();
            }
        });
    }

    protected ToolButton createRestore() {
        return new ToolButton(ToolButton.RESTORE, "Restore", new ClickHandler() {
            public void onClick(ClickEvent event) {
                restore();
            }
        });
    }

    /**
     * Our configure button has been clicked.
     */
    protected void onConfigureClick() {
        if (editEnabled) {
            if (portlet != null && portlet.isConfigureSupported()) {
                showChooseActionDialog();
            } else {
                showChooseWidgetDialog();
            }
        } else if (portlet != null) {
            portlet.configure();
        }
    }

    public void refresh() {
        int wc = body.getWidgetCount();
        if (wc == 0) {
            return;
        }

        // if we contain only one widget then refresh that on its own so the
        // same widget instance is maintained otherwise refresh body
        final Widget w = wc == 1 ? body.getWidget(0) : body;
        if (!(w instanceof WidgetFactoryProvider)) {
            return;
        }

        if (w instanceof Portlet) {
            lastFlags = -1;            
            ((Portlet)w).refresh(); // widget can refresh itself
        } else {
            if (refreshHelper == null) {
                refreshHelper = new RefreshHelper();
            }
            refreshHelper.refresh(w, new AsyncCallback<WidgetFactory>() {
                public void onFailure(Throwable caught) {
                }
                public void onSuccess(WidgetFactory result) {
                    outer.layout();
                    body.layout();
                }
            });
        }
    }

    /**
     * Get the container we will expand to cover if maximized or null if none
     * (already full size or no maximize stops found).
     */
    protected Container getNextMaximizeStop() {
        return LayoutUtil.getNextMaximizeStop(
                currentMaximizeStop == null ? this : (Widget)currentMaximizeStop);
    }

    public void maximize() {
        final Container next = getNextMaximizeStop();
        if (next == null) {
            return;
        }
        currentMaximizeStop = next;

        if (maxOverlay == null) {
            maxOverlay = new OverlayPanel();
            maxOverlay.setStyleName(getStylePrimaryName() + "-maximized");
            maxOverlay.setLogicalParent(this);            
        }

        lastFlags = -1;
        new MoveAnimation(main, (Widget)currentMaximizeStop,
                new AsyncCallback<Void>(){
            public void onFailure(Throwable caught) {
            }
            public void onSuccess(Void result) {
                if (maxOverlay.getWidgetCount() == 0) {
                    outer.remove(main);
                    maxOverlay.add(main);
                }
                maxOverlay.setTarget(currentMaximizeStop);
            }
        }).start();
        update();
    }

    public void restore() {
        if (maxOverlay == null) {
            return;
        }
        maxOverlay.remove(main);
        outer.add(main);
        new MoveAnimation(maxOverlay, outer, null).start();
        maxOverlay.setTarget(null);
        currentMaximizeStop = null;
        maxOverlay = null;
        lastFlags = -1;
        layout();
    }

    protected void onUnload() {
        super.onUnload();
        restore();
    }

    public String getWidgetTitle() {
        return titleLabel.getText();
    }

    public String getWidgetName() {
        return "Title";
    }

    public int getFlags() {
        return 0;
    }

    public void configure() {
    }

    /**
     * Ask the user if they want to configure our widget or select a
     * replacement.
     */
    protected void showChooseActionDialog() {
        final Portlet p = getPortlet();
        final Dialog dlg = new Dialog();
        dlg.setText("Configure");
        dlg.setWidget(new HTML("<div style=\"padding:4px\">Would you like to configure " +
                p.getWidgetName() + " or replace it with another widget?</div>"));
        dlg.addButton(new CssButton("Configure", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dlg.hide();
                p.configure();
            }
        }, "Configure " + p.getWidgetName()));
        dlg.addButton(new CssButton("Replace", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dlg.hide();
                showChooseWidgetDialog();
            }
        }, "Replace with another widget"));
        dlg.addButton(new CssButton("Cancel", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dlg.hide();
            }
        }));
        dlg.setPixelSize(400, 120);
        dlg.center();
    }

    /**
     * Ask the user to select a new widget to replace whatever we currently
     * have.
     */
    protected void showChooseWidgetDialog() {
        final SelectWidgetFactoryDialog dlg = new SelectWidgetFactoryDialog(
                getWidgetFactoryList());
        dlg.setText("Choose Widget");
        dlg.addPopupListener(new PopupListener() {
            public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
                WidgetFactoryMetaData md = dlg.getSelectedWidgetFactory();
                if (md != null) {
                    Widget w = md.createWidgetFactory().createWidget();
                    replaceWidget(w);
                }
            }
        });
        dlg.center();
    }

    /**
     * Get the list of widgets the user has to choose from when the choose
     * widget dialog is displayed. Override this to restrict selection.
     */
    protected List<WidgetFactoryMetaData> getWidgetFactoryList() {
        return LayoutUtil.getWidgetFactoryList();
    }

    /**
     * Replace our contents with w.
     */
    protected void replaceWidget(Widget w) {
        LayoutConstraints c = null;
        if (getWidgetCount() > 0) {
            c = getLayoutConstraints(getWidget(0));
        }
        if (c == null) {
            c = new RowLayout.Constraints();
        }
        clear();
        add(w, c);
        layout();
        onChanged();
        refresh();
        if (portlet != null
                && (portlet.getFlags() & CONFIGURE) != 0) {
            portlet.configure();
        }
    }

    /**
     * Our layout as changed in some way (widgets added or removed etc.).
     * Dispatch an event.
     */
    protected void onChanged() {
        EventManager.get().broadcast(new WidgetConfigChangeEvent(this));
    }    

    protected boolean isMaximized() {
        return maxOverlay != null;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public boolean isTitleTextAuto() {
        return titleTextAuto;
    }

    public void setTitleTextAuto(boolean titleTextAuto) {
        this.titleTextAuto = titleTextAuto;
    }

    public boolean isRefreshEnabled() {
        return refreshEnabled;
    }

    public void setRefreshEnabled(boolean refreshEnabled) {
        this.refreshEnabled = refreshEnabled;
    }

    public boolean isConfigureEnabled() {
        return configureEnabled;
    }

    public void setConfigureEnabled(boolean configureEnabled) {
        this.configureEnabled = configureEnabled;
    }

    public boolean isMaximizeEnabled() {
        return maximizeEnabled;
    }

    public void setMaximizeEnabled(boolean maximizeEnabled) {
        this.maximizeEnabled = maximizeEnabled;
    }

    public boolean isLimitMaximize() {
        return limitMaximize;
    }

    public void setLimitMaximize(boolean limitMaximize) {
        this.limitMaximize = limitMaximize;
    }

    public boolean isEditEnabled() {
        return editEnabled;
    }

    public void setEditEnabled(boolean editEnabled) {
        this.editEnabled = editEnabled;
    }

    protected Container getDelegate() {
        return body;
    }

    public void layout() {
        update();
        super.layout();
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    @WidgetInfo(description = "Panel with caption and buttons to refresh, " +
            "maximize and so on")
    public static class Factory extends ContainerFactory<TitlePanel> {

        private String title;
        private boolean titleAuto;
        private boolean refresh;
        private boolean configure;
        private boolean edit;
        private boolean maximize;
        private boolean limitMaximize;

        public Factory() {
        }

        public Factory(TitlePanel cp) {
            super(cp);
            title = cp.titleText;
            titleAuto = cp.titleTextAuto;
            refresh = cp.refreshEnabled;
            configure = cp.configureEnabled;
            edit = cp.editEnabled;
            maximize = cp.maximizeEnabled;
            limitMaximize = cp.limitMaximize;
        }

        public void refresh(TitlePanel p) {
            refreshSettings(p);
            super.refresh(p);
        }

        /**
         * Refresh only the configurable settings of p.
         */
        public void refreshSettings(TitlePanel p) {
            p.titleText = title;
            p.titleTextAuto = titleAuto;
            p.refreshEnabled = refresh;
            p.configureEnabled = configure;
            p.editEnabled = edit;
            p.maximizeEnabled = maximize;
            p.limitMaximize = limitMaximize;
        }

        public TitlePanel createWidget() {
            return new TitlePanel();
        }
    }
}
