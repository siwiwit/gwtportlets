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

package org.gwtportlets.portlet.client.layout;

import com.google.gwt.event.shared.GwtEvent;

/**
 * A {@link org.gwtportlets.portlet.client.layout.Container} has redone its
 * layout.
 */
public class LayoutEvent extends GwtEvent<LayoutHandler> {

    private static Type<LayoutHandler> TYPE;

    /**
     * Fires a layout event on all registered handlers in the handler
     * manager.If no such handlers exist, this method will do nothing.
     *
     * @param source the source of the handlers
     */
    public static void fire(HasLayoutHandlers source) {
        if (TYPE != null) {
            source.fireEvent(new LayoutEvent());
        }
    }

    /**
     * Gets the type associated with this event.
     */
    public static Type<LayoutHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<LayoutHandler>();
        }
        return TYPE;
    }

    public Type<LayoutHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(LayoutHandler handler) {
        handler.onLayoutUpdated(this);
    }
}
