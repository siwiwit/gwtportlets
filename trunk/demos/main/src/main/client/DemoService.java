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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import org.gwtportlets.portlet.client.WidgetFactory;

/**
 * Our service interface.
 */
public interface DemoService extends RemoteService {

    public static class App {
        private static DemoServiceAsync ourInstance = null;

        public static synchronized DemoServiceAsync get() {
            if (ourInstance == null) {
                ourInstance = (DemoServiceAsync)GWT.create(DemoService.class);
                ((ServiceDefTarget)ourInstance).setServiceEntryPoint(
                        GWT.getModuleBaseURL() + "rpc");
            }
            return ourInstance;
        }
    }
    
    /**
     * Load the root layout for our application and the page for the
     * history token.
     */
    public DemoPage getRootPage(String historyToken);

    /**
     * Load a page for the history token.
     */
    public DemoPage getPage(String historyToken);

    /**
     * Update the page layout.
     */
    public void savePage(String pageName, WidgetFactory wf);

    /**
     * Refresh the factory with new data from the server.
     */
    public WidgetFactory refresh(String historyToken, WidgetFactory wf);

}
