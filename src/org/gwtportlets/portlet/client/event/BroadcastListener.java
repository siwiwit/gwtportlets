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

package org.gwtportlets.portlet.client.event;

/**
 * Widgets that implement this will receive objects broadcast to the widget
 * tree. Typically these will be events.
 *
 * @see BroadcastManager#broadcast(Object)
 * @see BroadcastManager#broadcastUp(com.google.gwt.user.client.ui.Widget, Object)
 */
public interface BroadcastListener {

    /**
     * A broadcast object has been received.
     * 
     * @see BroadcastManager#broadcast(Object)
     * @see BroadcastManager#broadcastUp(com.google.gwt.user.client.ui.Widget, Object)
     */
    public void onBroadcast(Object ev);

    /**
     * Returns true if the listener is still listening. Generally the listener will be some sort of widget and this method will just return isAttached()
     *
     * @return  True if the listener is still listening for broadcasted events at this point
     */
    public boolean isStillListeningForBroadcasts();
}
