/*
 * GWT Portlets Framework (http://code.google.com/p/gwtportlets/)
 * Copyright 2010 Business Systems Group (Africa)
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

/**
 * Provides a method to check if a widget factory is enabled or not. This
 * interface must be implemented by both the PageProvider and
 * WidgetRefreshHook classes to use this functionality.
 */
public interface HasWidgetFactoryEnabled {
    /**
     * Returns true if a widget factory type is completely disabled.
     */
    public boolean isWidgetFactoryEnabled(Class<? extends WidgetFactory> wfClass);

    /**
     * Returns true if a specific widget factory is disabled. The implementation
     * should probably also call the Class version of this method as well.
     */
    public boolean isWidgetFactoryEnabled(WidgetFactory wf);

    /**
     * Returns the message why the given widget factory is disabled. This must
     * return null if the factory is enabled. The implementation should probably
     * also call the isWidgetFactoryEnabled(WidgetFactory) method.
     */
    public String getWidgetFactoryDisabledMessage(WidgetFactory wf);
}
