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

package main.server;

import main.client.ui.FreeMemoryPortlet;
import org.gwtportlets.portlet.server.PageRequest;
import org.gwtportlets.portlet.server.WidgetDataProvider;

/**
 * Displays free and total memory on the server.
 */
public class FreeMemoryDataProvider
        implements WidgetDataProvider<FreeMemoryPortlet.Factory> {

    public Class getWidgetFactoryClass() {
        return FreeMemoryPortlet.Factory.class;
    }

    public void refresh(FreeMemoryPortlet.Factory f, PageRequest req) {
        f.freeK = (int)(Runtime.getRuntime().freeMemory() >> 10);
        f.totalK = (int)(Runtime.getRuntime().totalMemory() >> 10);
    }
}
