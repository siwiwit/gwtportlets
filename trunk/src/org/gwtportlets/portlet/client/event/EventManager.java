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

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.layout.LayoutUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.Iterator;

/**
 * Singleton to boardcast events to widget trees.
 */
public class EventManager {

    private static EventManager instance;

    /**
     * Get the instance, creating it if not already set.
     */
    public static EventManager get() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    /**
     * Call this from onModuleLoad if you have your own manager class for
     * your application.
     */
    public static void set(EventManager instance) {
        if (EventManager.instance != null) {
            throw new IllegalStateException("EventManager already set");
        }
        EventManager.instance = instance;
    }

    private AppEventListener[] listeners = new AppEventListener[0];

    public EventManager() {
    }

    /**
     * Add listener to be notified on calls to {@link #broadcast} before the
     * event is dispatched to the widget tree.
     */
    public void addAppEventListener(AppEventListener l) {
        ArrayList a = new ArrayList(Arrays.asList(listeners));
        a.add(l);
        listeners = (AppEventListener[])a.toArray(new AppEventListener[a.size()]);
    }

    public void removeAppEventListener(AppEventListener l) {
        ArrayList a = new ArrayList(Arrays.asList(listeners));
        a.remove(l);
        listeners = (AppEventListener[])a.toArray(new AppEventListener[a.size()]);
    }

    /**
     * Send the event to all widgets implementing AppEventListener
     * starting at the RootPanel in depth first order. Does not send the event
     * to its source.
     *
     * @see AppEventListener#onAppEvent
     * @see org.gwtportlets.portlet.client.layout.LayoutUtil#getTopmostContainer
     */
    public void broadcast(EventObject ev) {
        for (int i = listeners.length - 1; i >= 0; i--) {
            listeners[i].onAppEvent(ev);
        }
        broadcastDown(RootPanel.get(), ev);
    }

    /**
     * Send the event up to the logical parent of w (and its logical parent
     * and so on) until a widget with no logical parent is reached.
     *
     * @see AppEventListener#onAppEvent
     * @see org.gwtportlets.portlet.client.layout.LayoutUtil#getLogicalParent
     */
    public void broadcastUp(Widget w, EventObject ev) {
        for (;;) {
            w = LayoutUtil.getLogicalParent(w);
            if (w == null) {
                break;
            }
            if (w instanceof AppEventListener) {
                ((AppEventListener)w).onAppEvent(ev);
            }
        }
    }

    /**
     * Send the event to root and all its children. Does not send the event
     * to its source.
     */
    protected void broadcastDown(Widget root, EventObject ev) {
        if (root != ev.getSource() && root instanceof AppEventListener) {
            ((AppEventListener)root).onAppEvent(ev);
        }
        if (root instanceof HasWidgets) {
            Iterator<Widget> i = ((HasWidgets)root).iterator();
            while (i.hasNext()) {
                broadcastDown(i.next(), ev);
            }
        }
    }

}
