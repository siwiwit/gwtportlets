/*
 * GWT Portlets Framework (http://code.google.com/p/gwtportlets/)
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.event.BroadcastListener;
import org.gwtportlets.portlet.client.layout.DeckLayout;
import org.gwtportlets.portlet.client.layout.LDOM;
import org.gwtportlets.portlet.client.layout.LayoutConstraints;
import org.gwtportlets.portlet.client.layout.RowLayout;
import org.gwtportlets.portlet.client.util.DragHandle;
import org.gwtportlets.portlet.client.util.Rectangle;
import org.gwtportlets.portlet.client.util.SyncToClientArea;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Nicely styled replacement for standard GWT dialog box.
 */
public class Dialog extends PopupPanel implements AsyncCallback<WidgetFactory> {

    private final LayoutPanel main = new LayoutPanel(new RowLayout(true, 0));
    private final EdgeRow header = new EdgeRow(new LayoutPanel(new RowLayout(false, 0)));
    private final EdgeRow sidesContent = new EdgeRow(new LayoutPanel(new DeckLayout()));
    private final EdgeRow sidesButtonBar = new EdgeRow(new LayoutPanel(new DeckLayout()));
    private final FlowPanel buttonBar = new FlowPanel();
    private final EdgeRow footer = new EdgeRow(new LayoutPanel(new RowLayout(false, 0)));

    private final RefreshPanel content = new RefreshPanel() {
        public void onBroadcast(Object ev) {
            super.onBroadcast(ev);
            Dialog.this.onBroadcast(ev);
        }
    };

    private boolean modal;
    private int buttonBarHeight;
    private int maxHeight;
    private Rectangle originalBounds;
    private String wrapperStyle;
    private boolean widthSet;
    private BroadcastListener[] broadcastListeners;

    private final HTML titleHTML = new HTML("Dialog");
    private Widget blocker;
    private SyncToClientArea blockerSync;
    private SyncToClientArea maxSync;

    private boolean hidden = false;
    // Indicates whether the dialog will collapse downwards when the hide button is clicked. If this is false then it
    // will collapse upwards
    private boolean collapseDownwards = false;

    private final ToolButton hideButton = new ToolButton(ToolButton.HIDE, "Hide",
            new ClickHandler() {
        public void onClick(ClickEvent event) {
            onHideClick();
        }
    });

    private final ToolButton closeButton = new ToolButton(ToolButton.CLOSE, "Close",
            new ClickHandler() {
        public void onClick(ClickEvent event) {
            onCloseClick();
        }
    });

    private final ToolButton maxButton = new ToolButton(ToolButton.MAXIMIZE, "Maximize",
            new ClickHandler() {
        public void onClick(ClickEvent event) {
            onMaximizeClick();
        }
    });

    public Dialog() {
        this(false);
    }

    public Dialog(boolean autohide) {
        this(autohide, true);
    }

    public Dialog(boolean autoHide, boolean modal) {
        super(autoHide, modal);
        this.modal = modal;

        header.add(titleHTML, LayoutConstraints.HIDDEN);
        sidesContent.add(content);
        sidesButtonBar.add(buttonBar);

        main.add(header);
        main.add(sidesContent);

        main.add(sidesButtonBar);
        main.add(footer);

        super.setWidget(main);

        setHideVisible(false);
        setMaximizeVisible(true);
        setCloseVisible(true);
        setStyleName("portlet-dialog");

        new DragHandle(titleHTML, this){
            protected void onDrag(int clientX, int clientY) {
                setPopupPosition(clientX - getOffsetX(), clientY - getOffsetY());
            }
        };
    }

    protected void onAttach() {
        super.onAttach();
        checkHeight();
        main.layout();
    }

    /**
     * Make sure the dialog is not too big.
     */
    protected void checkHeight() {
        int h = LDOM.getHeight(getElement());
        int max = maxHeight == 0 ? Window.getClientHeight() : maxHeight;
        if (h > max) {
            setHeight(max + "px");
        }
    }

    public void setStyleName(String style) {
        super.setStyleName(style);
        // this gets called from PopupPanel's constructor so these may be null
        if (header != null) {
            header.setStyleName(style + "-header");
            sidesContent.setStyleName(style + "-sides");
            sidesButtonBar.setStyleName(style + "-sides");
            content.setStyleName(style + "-content");
            wrapperStyle = style + "-content-wrapper";
            buttonBar.setStyleName(style + "-buttonbar");
            footer.setStyleName(style + "-footer");
            titleHTML.setStyleName(style + "-title");
            Theme.get().updateDialog(this);
            main.setLayoutConstraints(header, new RowLayout.Constraints(
                    header.getHeight()));
            main.setLayoutConstraints(footer, new RowLayout.Constraints(
                    footer.getHeight()));
        }
    }

    public int getButtonBarHeight() {
        return buttonBarHeight;
    }

    public void setButtonBarHeight(int buttonBarHeight) {
        this.buttonBarHeight = buttonBarHeight;
        updateButtonBarConstraints();
    }

    private void updateButtonBarConstraints() {
        if (isButtonBarVisible()) {
            main.setLayoutConstraints(sidesButtonBar, new RowLayout.Constraints(
                    buttonBarHeight));
        }
    }

    /**
     * Replace our contents with w. If w is a TABLE then it is wrapped in a
     * DIV styled portlet-content-wrapper which gives it 4px padding by
     * default. This is useful for forms.
     */
    public void setWidget(Widget w) {
        // wrap TABLE elements in a DIV or they do not layout properly
        if (DOM.getElementProperty(w.getElement(), "tagName")
                .equalsIgnoreCase("table")) {
            SimplePanel p = new SimplePanel();
            p.setStyleName(wrapperStyle);
            p.add(w);
            w = p;
        }
        content.clear();
        content.add(w);
    }

    public void show() {
        if (modal && blocker == null) {
            blocker = LDOM.createInputBlocker();
            blocker.setStyleName(getStylePrimaryName() + "-blocker");
            blockerSync = new SyncToClientArea(blocker);
            blockerSync.resizeWidget();
            blockerSync.startListening();
            RootPanel.get().add(blocker);
        }
        super.show();
        ensureWidth();
    }

    /**
     * Give us a reasonable best guess width if none has been set otherwise
     * {@link #getOffsetWidth()} sometimes returns 0 messing up positioning
     * and so on.
     */
    private void ensureWidth() {
        if (!widthSet) {
            // add a fudge factor to avoid scrollbars that appear otherwise
            setWidth((header.getOffsetWidth() + LDOM.getDialogWidthFudgePx()) +
                    "px");
        }
    }

    public void setWidth(String width) {
        widthSet = true;
        super.setWidth(width);
        // setting the width on all of our inner widgets seems to make
        // dragging much smoother for some reason
        header.setWidth(width);
        sidesContent.setWidth(width);
        sidesButtonBar.setWidth(width);
        footer.setWidth(width);
        header.layout();
        sidesContent.layout();
        sidesButtonBar.layout();
        footer.layout();
    }

    public void center() {
        // default center creates dialogs with content area with zero height
        // for some reason .. this one works .. not sure why
        setVisible(false);
        show();
        super.center();
        setVisible(true);
    }

    /**
     * Position the dialog next to another target widget and show it.
     */
    public void showNextTo(Widget target) {
        setVisible(false);
        show();
        Rectangle r = LDOM.getNextToPosition(this, target.getElement(), 4);
        setPopupPosition(r.x, r.y);
        setVisible(true);
    }    

    public int getOffsetHeight() {
        // the default implementation always returns zero for some reason
        // so calculate the height from our components
        return header.getOffsetHeight() + sidesContent.getOffsetHeight() +
                sidesButtonBar.getOffsetHeight() + footer.getOffsetHeight();
    }

    protected void onDetach() {
        super.onDetach();
        if (blocker != null) {
            blockerSync.stopListening();
            // If the root panel is detaching all its children, the blocker
            // could be detached before the dialog. 
            if (blocker.isAttached()) {
                RootPanel.get().remove(blocker);
            }
            blocker = null;
            blockerSync = null;
        }
        if (maxSync != null) {
            maxSync.stopListening();
            maxSync = null;
        }
    }

    public boolean isModal() {
        return modal;
    }

    public EdgeRow getHeader() {
        return header;
    }

    public EdgeRow getSidesContent() {
        return sidesContent;
    }

    public RefreshPanel getContent() {
        return content;
    }

    public EdgeRow getSidesButtonBar() {
        return sidesButtonBar;
    }

    public FlowPanel getButtonBar() {
        return buttonBar;
    }

    public EdgeRow getFooter() {
        return footer;
    }

    public String getText() {
        return titleHTML.getText();
    }

    public void setText(String text) {
        titleHTML.setText(text);
    }

    public void setHTML(String html) {
        titleHTML.setHTML(html);
    }

    public void setHTML(SafeHtml html) {
        titleHTML.setHTML(html);
    }

    public void setCollapseDownwards(boolean on) {
        collapseDownwards = on;
    }

    public boolean isHideVisible() {
        return hideButton.getParent() != null;
    }

    public void setHideVisible(boolean on) {
        if (on != isHideVisible()) {
            if (on) {
                RowLayout.Constraints c = new RowLayout.Constraints(
                        hideButton.getMaxWidth());
                if (isMaximizeVisible()) {
                    header.insert(hideButton, header.getWidgetIndex(maxButton), c);
                } else if (isCloseVisible()) {
                    header.insert(hideButton, header.getWidgetIndex(closeButton), c);
                } else {
                    header.add(hideButton, c);
                }
            } else {
                header.remove(hideButton);
            }
        }
    }

    public boolean isCloseVisible() {
        return closeButton.getParent() != null;
    }

    public void setCloseVisible(boolean on) {
        if (on != isCloseVisible()) {
            if (on) {
                header.add(closeButton, new RowLayout.Constraints(
                        closeButton.getMaxWidth()));
            } else {
                header.remove(closeButton);
            }
        }
    }

    public boolean isMaximizeVisible() {
        return maxButton.getParent() != null;
    }

    public void setMaximizeVisible(boolean on) {
        if (on != isMaximizeVisible()) {
            if (on) {
                RowLayout.Constraints c = new RowLayout.Constraints(
                        maxButton.getMaxWidth());
                if (isCloseVisible()) {
                    header.insert(maxButton, header.getWidgetIndex(closeButton), c);
                } else {
                    header.add(maxButton, c);
                }
            } else {
                header.remove(maxButton);
            }
        }
    }

    public boolean isButtonBarVisible() {
        return sidesButtonBar.getParent() !=  null;
    }

    public void setButtonBarVisible(boolean on) {
        if (on != isButtonBarVisible()) {
            if (on) {
                main.insert(sidesButtonBar, main.getWidgetIndex(footer), null);
                updateButtonBarConstraints();
            } else {
                main.remove(sidesButtonBar);
            }
        }
        main.layout();
    }

    /**
     * Hides / collapses the dialog (if it is not already hidden)
     */
    public void collapse() {
        if (!isHidden()) {
            toggleHide();
        }
    }

    /**
     * Un-hides / expands the dialog (if it is hidden)
     */
    public void uncollapse() {
        if (isHidden()) {
            toggleHide();
        }
    }

    /**
     * The hide / show button has been clicked
     */
    public void onHideClick() {
        toggleHide();
    }

    /**
     * Hides / shows the dialog
     */
    private void toggleHide() {
        if (isHidden()) {
            updateHideButton(true);

            Rectangle headerBounds = LDOM.getBounds(header);

            Rectangle showBounds = new Rectangle(originalBounds);
            showBounds.x = headerBounds.x;
            showBounds.y = collapseDownwards ?
                    headerBounds.y - originalBounds.height + headerBounds.height :
                    headerBounds.y;
            
            setBounds(showBounds);
            originalBounds = null;

            hidden = false;
        } else {
            updateHideButton(false);

            // LDOM.getBounds(this) and LDOM.getBounds(main) return 0 height
            // so add up the sections manually
            originalBounds = LDOM.getBounds(header);
            originalBounds.height = getHeight();

            Rectangle hiddenBounds = new Rectangle(LDOM.getBounds(header));
            hiddenBounds.height = LDOM.getHeight(header.getElement());

            if (collapseDownwards) {
                hiddenBounds.y += (LDOM.getHeight(sidesContent.getElement())
                     + LDOM.getHeight(sidesButtonBar.getElement())
                     + LDOM.getHeight(footer.getElement()));
            }
            setBounds(hiddenBounds);

            hidden = true;
        }
        setButtonBarVisible(!hidden);
    }

    private int getHeight() {
        return LDOM.getHeight(header.getElement())
                + LDOM.getHeight(sidesContent.getElement())
                + LDOM.getHeight(sidesButtonBar.getElement())
                + LDOM.getHeight(footer.getElement());
    }

    public boolean isHidden() {
        return hidden;
    }

    protected void updateHideButton(boolean hide) {
        hideButton.setImageIndex(hide ? ToolButton.HIDE : ToolButton.SHOW);
        hideButton.setTitle(hide ? "Hide" : "Show");
    }

    public ToolButton getCloseButton() {
        return closeButton;
    }

    /**
     * The close button has been clicked. Call this to simulate a close click.
     */
    public void onCloseClick() {
        hide();
    }

    public void setPixelSize(int width, int height) {
        main.setPixelSize(width, height);
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    /**
     * If w is not null add it to the button bar.
     */
    public void addButton(Widget w) {
        if (w != null) {
            buttonBar.add(w);
        }
    }

    /**
     * Add each non-null widget in a to the button bar.
     */
    public void addButtons(Widget[] a) {
        for (int i = 0; i < a.length; i++) {
            Widget w = a[i];
            if (w != null) {
                buttonBar.add(w);
            }
        }
    }

    /**
     * Add a button that hides the dialog when clicked.
     */
    public CssButton addCloseButton(String html) {
        CssButton b = new CssButton(html, new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        addButton(b);
        return b;
    }

    public CssButton addCloseButton(SafeHtml html) {
        CssButton b = new CssButton(html, new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        addButton(b);
        return b;
    }

    /**
     * Add a button that hides the dialog when clicked.
     */
    public CssButton addCloseButton() {
        return addCloseButton("Close");
    }

    public boolean isMaximized() {
        return maxSync != null;
    }

    /**
     * The maximize/restore button has been clicked.
     */
    protected void onMaximizeClick() {
        toggleMaximized();
    }

    /**
     * Toggle between maximized/restored state.
     */
    protected void toggleMaximized() {
        if (isMaximized()) {
            updateMaximizeButton(true);
            maxSync.stopListening();
            maxSync = null;
            setBounds(originalBounds);
            originalBounds = null;
        } else {
            updateMaximizeButton(false);
            // LDOM.getBounds(this) and LDOM.getBounds(main) return 0 height
            // so add up the sections manually
            originalBounds = LDOM.getBounds(header);
            originalBounds.height = getHeight();
            maxSync = new SyncToClientArea(Dialog.this) {
                public void resizeWidget() {
                    setBounds(getMaximizeBounds());
                }
            };
            maxSync.startListening();
            maxSync.resizeWidget();
        }
    }

    protected void setBounds(Rectangle r) {
        LDOM.setBounds(main, 0, 0, r.width, r.height);
        setPopupPosition(r.x,  r.y);
    }

    /**
     * Get the bounds we expand to fill when maximized.
     */
    protected Rectangle getMaximizeBounds() {
        Rectangle r = new Rectangle();
        r.width = Window.getClientWidth();
        r.height = Window.getClientHeight();
        return r;
    }

    protected void updateMaximizeButton(boolean maximize) {
        maxButton.setImageIndex(maximize ? ToolButton.MAXIMIZE : ToolButton.RESTORE);
        maxButton.setTitle(maximize ? "Maximize" : "Restore");
    }

    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        super.onPreviewNativeEvent(event);
        if (event.getNativeEvent().getKeyCode() == 27
                && closeButton.isAttached() && closeButton.isEnabled()) {
            onCloseClick();
            event.cancel();
        }
    }

    /**
     * An broadcast object has been received, probably from a child widget.
     * Forward it to our listeners.
     */
    protected void onBroadcast(Object ev) {
        if (broadcastListeners != null) {
            for (int i = 0; i < broadcastListeners.length; i++) {
                broadcastListeners[i].onBroadcast(ev);
            }
        }
    }

    public void addBroadcastListener(BroadcastListener h) {
        if (broadcastListeners == null) {
            broadcastListeners = new BroadcastListener[]{h};
        } else {
            ArrayList a = new ArrayList(Arrays.asList(broadcastListeners));
            a.add(h);
            broadcastListeners = (BroadcastListener[])a.toArray(new BroadcastListener[a.size()]);
        }
    }

    public void removeBroadcastListener(BroadcastListener h) {
        if (broadcastListeners != null) {
            ArrayList a = new ArrayList(Arrays.asList(broadcastListeners));
            a.remove(h);
            broadcastListeners = (BroadcastListener[])a.toArray(new BroadcastListener[a.size()]);
        }
    }

    public void onFailure(Throwable caught) {
        if (!isShowing()) {
            show();
        }
    }

    public void onSuccess(WidgetFactory result) {
        hide();
    }

}
