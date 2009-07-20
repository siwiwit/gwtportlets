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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.event.*;
import org.gwtportlets.portlet.client.layout.*;
import org.gwtportlets.portlet.client.util.Rectangle;
import org.gwtportlets.portlet.client.util.FormBuilder;

/**
 * Displays the current page by listening for {@link PageChangeEvent}'s
 * and swapping out a new tree of widgets in a content area on page changes.
 * If the page is editable then an edit button is displayed that launches
 * the online layout editor on click.
 */
public class PagePortlet extends ContainerPortlet implements
        BroadcastListener {

    private String appTitle = "App Title";
    private String prefix = appTitle + ": ";

    private RefreshPanel panel = new RefreshPanel();
    private ToolButton edit;

    private PageChangeEvent pageChange;
    private Portlet titlePortlet;
    private String currentTitle;

    public PagePortlet() {
        initWidget(panel);
        panel.setLimitMaximize(true);
        setStyleName("portlet-page");
    }

    protected Container getDelegate() {
        return panel;
    }

    private void restore(Factory f) {
        this.appTitle = f.appTitle;
        this.prefix = f.prefix;
        updateTitle();
    }

    public void onBroadcast(Object ev) {
        if (ev instanceof PageChangeEvent) {
            onPageChange((PageChangeEvent)ev);
        } else if (ev instanceof WidgetChangeEvent
                && ((WidgetChangeEvent)ev).getSource() == titlePortlet) {
            updateTitle();
        }
    }

    protected void onPageChange(PageChangeEvent pageChange) {
        if (pageChange.isHandled()) {
            return;
        }
        pageChange.setHandled(true);
        this.pageChange = pageChange;
        
        // replace the contents of our panel with a tree of widgets created
        // from the factory in the page
        panel.clear();
        WidgetFactory wf = pageChange.getWidgetFactory();
        if (wf != null) {
            panel.add(LayoutUtil.createWidget(wf));
        }
        panel.layout();

        // use the first portlet we find on panel to supply our title
        titlePortlet = LayoutUtil.findPortletChild(panel);
        updateTitle();

        // if the page is editable then make sure the edit button is visible
        if (pageChange.isEditable()) {
            if (edit == null) {
                edit = new ToolButton(ToolButton.CONFIGURE, "Edit page",
                        new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        onEditPage();
                    }
                });
            }
            edit.setTitle("Edit page '" + pageChange.getPageName() + "'");
            if (!edit.isAttached()) {
                RootPanel.get().add(edit);
                updateEditButtonPosition();
            }
        } else if (edit != null && edit.isAttached()) {
            RootPanel.get().remove(edit);
        }
    }

    private void onEditPage() {
        pageChange.editPage((Container)panel.getWidget(0));
    }

    private void updateTitle() {
        String title = null, windowTitle;
        if (titlePortlet != null) {
            title = titlePortlet.getWidgetTitle();
        }
        if (title != null && !title.equals(appTitle)) {
            windowTitle = prefix + title;
        } else {
            title = windowTitle = appTitle;
        }
        if (!title.equals(currentTitle)) {
            Window.setTitle(windowTitle);
            currentTitle = title;
        }
        BroadcastManager.get().broadcast(new PageTitleChangeEvent(this, title));
    }

    public void boundsUpdated() {
        super.boundsUpdated();
        updateEditButtonPosition();
    }

    protected void updateEditButtonPosition() {
        if (edit != null && edit.isAttached()) {
            Rectangle r = new Rectangle();
            r.width = edit.getMaxWidth();
            r.height = edit.getMaxHeight();
            r.x = Window.getClientWidth() - r.width;
            r.y = Window.getClientHeight() - r.height;
            LDOM.setBounds(edit, r);
        }
    }

    public boolean isConfigureSupported() {
        return true;
    }

    public void configure() {
        final TextBox appTitle = new TextBox();
        appTitle.setVisibleLength(30);
        appTitle.setTitle("Title used when no other title is available");
        appTitle.setText(this.appTitle);

        final TextBox prefix = new TextBox();
        prefix.setVisibleLength(30);
        prefix.setTitle("Prefix for title from first portlet on page for " +
                "browser window");
        prefix.setText(this.prefix);

        FormBuilder b = new FormBuilder();
        b.label("Application title").field(appTitle).endRow();
        b.label("Browser window title prefix").field(prefix).endRow();

        final Dialog dlg = new Dialog();
        dlg.setText("Configure " + getWidgetName());
        dlg.setWidget(b.getForm());

        dlg.addButton(new CssButton("OK", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dlg.hide();
                PagePortlet.this.appTitle = appTitle.getText();
                PagePortlet.this.prefix = prefix.getText();
                updateTitle();
            }
        }));
        dlg.addCloseButton("Cancel");
        dlg.showNextTo(this);
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    public static class Factory extends PortletFactory<PagePortlet> {

        public String appTitle;
        public String prefix;

        public Factory() {
        }

        public Factory(PagePortlet p) {
            super(p);
            appTitle = p.appTitle;
            prefix = p.prefix;
        }

        public void refresh(PagePortlet p) {
            super.refresh(p);
            p.restore(this);
        }

        public PagePortlet createWidget() {
            return new PagePortlet();
        }
    }

}


