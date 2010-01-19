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

package smartgwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.gwtportlets.portlet.client.WidgetFactory;

/**
 * Async version of DemoService.
 */
public interface DemoServiceAsync {

    /**
     * Load the root layout for our application and the page for the
     * history token.
     */
    void getRootPage(String historyToken,
            AsyncCallback<DemoPage> async);

    /**
     * Load a page for the history token.
     */
    void getPage(String historyToken, AsyncCallback<DemoPage> async);

    /**
     * Update the page layout.
     */
    void savePage(String pageName, WidgetFactory wf, AsyncCallback async);

    /**
     * Refresh the factory with new data from the server.
     */
    void refresh(String historyToken, WidgetFactory wf,
            AsyncCallback<WidgetFactory> async);
}
