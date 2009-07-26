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

/**
 * Singleton to broadcast objects to widget trees.
 */
public class BroadcastManager {

    private static BroadcastManager instance;

    /**
     * Get the instance, creating it if not already set.
     */
    public static BroadcastManager get() {
        if (instance == null) {
            instance = new BroadcastManager();
        }
        return instance;
    }

    /**
     * Call this from onModuleLoad if you have your own manager class for
     * your application.
     */
    public static void set(BroadcastManager instance) {
        if (BroadcastManager.instance != null) {
            throw new IllegalStateException("BroadcastManager already set");
        }
        BroadcastManager.instance = instance;
    }

    private BroadcastListener[] listeners = new BroadcastListener[0];

    public BroadcastManager() {
    }

    /**
     * Add handler to be notified on calls to {@link #broadcast} before the
     * object is dispatched to the widget tree.
     */
    public void addObjectBroadcastListener(BroadcastListener l) {
        ArrayList a = new ArrayList(Arrays.asList(listeners));
        a.add(l);
        listeners = (BroadcastListener[])a.toArray(new BroadcastListener[a.size()]);
    }

    public void removeObjectBroadcastListener(BroadcastListener l) {
        ArrayList a = new ArrayList(Arrays.asList(listeners));
        a.remove(l);
        listeners = (BroadcastListener[])a.toArray(new BroadcastListener[a.size()]);
    }

    /**
     * Send the object to all widgets from the RootPanel down.
     *
     * @see #broadcast(com.google.gwt.user.client.ui.Widget, Object) 
     */
    public void broadcast(Object ev) {
        for (int i = listeners.length - 1; i >= 0; i--) {
            listeners[i].onBroadcast(ev);
        }
        broadcast(RootPanel.get(), ev);
    }

    /**
     * Send the object up to the logical parent of w (and its logical parent
     * and so on) until a widget with no logical parent is reached.
     *
     * @see BroadcastListener#onBroadcast
     * @see org.gwtportlets.portlet.client.layout.LayoutUtil#getLogicalParent
     */
    public void broadcastUp(Widget w, Object ev) {
        for (;;) {
            w = LayoutUtil.getLogicalParent(w);
            if (w == null) {
                break;
            }
            if (w instanceof BroadcastListener) {
                ((BroadcastListener)w).onBroadcast(ev);
            }
        }
    }

    /**
     * Send the object to all widgets implementing EventBroadcastHandler
     * starting at root in depth first order. Note that only children
     * of Widgets that implement {@link com.google.gwt.user.client.ui.HasWidgets}
     * are visited. This means that if a Portlet has children that need to
     * receive events it must implement HasWidgets (the iterator method)
     * or BroadcastListener and forward events received.
     *
     * @see BroadcastListener#onBroadcast
     * @see org.gwtportlets.portlet.client.layout.LayoutUtil#getTopmostContainer
     * @see org.gwtportlets.portlet.client.ui.Portlet#broadcastDown(Object) 
     */
    public void broadcast(Widget root, Object ev) {
        if (root instanceof BroadcastListener) {
            ((BroadcastListener)root).onBroadcast(ev);
        }
        if (root instanceof HasWidgets) {
            for (Widget w : ((HasWidgets)root)) {
                broadcast(w, ev);
            }
        }
    }

}
