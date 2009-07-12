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

package org.gwtportlets.portlet.server;

import org.gwtportlets.portlet.client.WidgetFactory;

/**
 * Makes changes to a WidgetFactory before it is sent to the client.
 * Typically this will involve populating it with data from some source.
 */
public interface WidgetDataProvider<T extends WidgetFactory> {

    /**
     * If this provider provides data for a single WidgetFactory class
     * then return it, else return null.
     */
    public Class getWidgetFactoryClass();

    /**
     * Make changes to wf e.g. populate it with data from a database using
     * information in f and req as input to the query.
     */
    public void refresh(T wf, PageRequest req);

}
