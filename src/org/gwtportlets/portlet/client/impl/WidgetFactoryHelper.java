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

package org.gwtportlets.portlet.client.impl;

import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.WidgetFactoryMetaData;

/**
 * Generated operations to help work with
 * {@link org.gwtportlets.portlet.client.WidgetFactory}'s.
 */
public interface WidgetFactoryHelper {

    /**
     * Clears fields on a WidgetFactory object that have been annotated with
     * {@link org.gwtportlets.portlet.client.DoNotSendToServer} and invokes
     * 'clearDoNotSendToServerFields' on WidgetFactory's that have a
     * method with that name.
     */
    public void clearDoNotSendToServerFields(WidgetFactory wf);

    /**
     * Get a list of all classes that implement WidgetFactory.
     */
    public WidgetFactoryMetaData[] getWidgetFactoryList();

    /**
     * Create a WidgetFactory instance by index.
     */
    public WidgetFactory createWidgetFactory(int index);

}
