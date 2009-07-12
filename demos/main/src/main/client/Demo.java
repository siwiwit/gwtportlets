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

package main.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.AbstractWidgetFactory;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.WidgetRefreshHandler;
import org.gwtportlets.portlet.client.event.EventManager;
import org.gwtportlets.portlet.client.event.PageChangeEvent;
import org.gwtportlets.portlet.client.layout.Container;
import org.gwtportlets.portlet.client.layout.LayoutUtil;
import org.gwtportlets.portlet.client.ui.ClientAreaPanel;
import org.gwtportlets.portlet.client.ui.LayoutPanel;

/**
 * EntryPoint (main) class for the demo application. Fetches the root page
 * layout and uses this to create the GUI. Loads additional pages when the
 * history token changes.
 */
public class Demo implements EntryPoint, HistoryListener {

    // The ClientAreaPanel always fills the whole of the browsers client area.
    // It forms the topmost container for the application and adds itself to
    // the normal GWT RootPanel in its constructor. It uses a LayoutPanel
    // internally
    private ClientAreaPanel clientAreaPanel = new ClientAreaPanel();
    // The pageEditor is responsible for editing and saving pages
    private DemoPageEditor pageEditor = new DemoPageEditor();

    public void onModuleLoad() {
        // The framework calls the WidgetRefreshHandler instance when it
        // needs to refresh a Portlet or Widget with new data from the server.
        // This handler just calls our refresh service method.
        WidgetRefreshHandler.App.set(new WidgetRefreshHandler() {
            public void refresh(Widget w, WidgetFactory wf,
                    AsyncCallback<WidgetFactory> cb) {
                DemoService.App.get().refresh(History.getToken(), wf, cb);
            }
            public void onRefreshCallFailure(Widget w, Throwable caught) {
                Window.alert("Refresh failed: " + caught);
            }
        });
        History.addHistoryListener(this);
        fetchRootPage();
    }

    /**
     * Create the main application GUI by fetching the 'root page' from the
     * server. The root page contains a {@link org.gwtportlets.portlet.client.ui.PagePortlet}
     * which forms a 'content area' to display the selected page. 
     */
    private void fetchRootPage() {
        DemoService.App.get().getRootPage(History.getToken(),
                new AsyncCallback<DemoPage>() {
            public void onFailure(Throwable caught) {
                Window.alert("Oops " + caught);
            }
            public void onSuccess(DemoPage p) {
                // Hide the initial loading spinner from Demo.html
                Element e = DOM.getElementById("loading");
                if (e != null) {
                    DOM.setStyleAttribute(e, "display", "none");
                }
                clientAreaPanel.add(LayoutUtil.createWidget(p.rootWidgetFactory));
                clientAreaPanel.layout();
                onPageChange(p);
            }
        });
    }

    /**
     * Notify all portlets that the page has changed. The
     * {@link org.gwtportlets.portlet.client.ui.PagePortlet} uses this
     * notification to display the new page.
     */
    private void onPageChange(final DemoPage p) {
        final boolean notFound = p.widgetFactory == null;
        if (notFound) {
            // Put in a 'not found' message
            p.widgetFactory = new AbstractWidgetFactory() {
                public Widget createWidget() {
                    LayoutPanel lp = new LayoutPanel();
                    lp.add(new Label("Page not found: '" + p.pageName +
                            "', click the edit page button to create it"));
                    return lp;
                }
            };
        }

        // The page change event knows how to edit the current page
        PageChangeEvent pce = new PageChangeEvent(this) {
            public void editPage(Container container) {
                if (notFound) {
                    // get rid of the 'not found' message
                    container.clear();    
                }
                pageEditor.startEditing(getPageName(), container);
            }
        };
        pce.setPageName(p.pageName);
        pce.setEditable(p.canEditPage);
        pce.setWidgetFactory(p.widgetFactory);

        // Send the event to every AppEventListener in the container tree.
        // The PagePortlet uses this event to change the widget tree in the
        // 'content area' of the application and to display the gear icon
        // for editable pages
        EventManager.get().broadcast(pce);
    }

    /**
     * Load a new page of portlets and widgets when the history token changes.
     */
    public void onHistoryChanged(String historyToken) {
        // display AJAX pizza over whole app disabling input while the page loads
        clientAreaPanel.setLoading(true);
        DemoService.App.get().getPage(historyToken, new AsyncCallback<DemoPage>() {
            public void onFailure(Throwable caught) {
                clientAreaPanel.setLoading(false);
                Window.alert("Oops " + caught);
            }
            public void onSuccess(DemoPage p) {
                clientAreaPanel.setLoading(false);
                onPageChange(p);
            }
        });
    }

}