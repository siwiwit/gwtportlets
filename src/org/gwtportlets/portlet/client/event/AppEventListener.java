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

package org.gwtportlets.portlet.client.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Widgets that implement this will receive broadcast application events.
 *
 * @see EventManager#broadcast(com.google.gwt.user.client.ui.Widget, java.util.EventObject)
 * @see EventManager#broadcastUp(com.google.gwt.user.client.ui.Widget, java.util.EventObject)  
 */
public interface AppEventListener extends EventListener {

    /**
     * Do something with the event.
     */
    public void onAppEvent(EventObject ev);

}
