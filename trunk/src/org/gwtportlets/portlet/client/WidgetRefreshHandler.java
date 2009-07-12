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

package org.gwtportlets.portlet.client;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Handles refreshing of widgets. Set a WidgetRefreshHandler for your
 * application in onModuleLoad to enable widget refresh.
 */
public interface WidgetRefreshHandler {

    public static class App {

        private static WidgetRefreshHandler instance;

        public static WidgetRefreshHandler get() {
            return instance;
        }

        public static void set(WidgetRefreshHandler instance) {
            if (App.instance != null) {
                throw new IllegalStateException("WidgetRefreshHandler already set");
            }
            App.instance = instance;
        }

        /**
         * If a WidgetRefreshHandler has been set then do refresh and return
         * true otherwise do nothing and return false.
         */
        public static boolean refresh(Widget w, WidgetFactory wf,
                AsyncCallback<WidgetFactory> cb) {
            if (instance != null) {
                instance.refresh(w, wf, cb);
                return true;
            }
            return false;
        }
    }

    /**
     * Refresh wf with new data from the server and invoke cb with the new
     * instance.
     */
    public void refresh(Widget w, WidgetFactory wf,
            AsyncCallback<WidgetFactory> cb);

    /**
     * A refresh has failed. The framework will log the failure using GWT.log
     * before calling this method. A common response would be to popup an
     * alert or a dialog with the message from caught.
     */
    public void onRefreshCallFailure(Widget w, Throwable caught);
}

