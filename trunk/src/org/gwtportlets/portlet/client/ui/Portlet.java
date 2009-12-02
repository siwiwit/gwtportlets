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

package org.gwtportlets.portlet.client.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.WidgetFactoryProvider;
import org.gwtportlets.portlet.client.event.BroadcastManager;
import org.gwtportlets.portlet.client.event.WidgetChangeEvent;
import org.gwtportlets.portlet.client.event.WidgetConfigChangeEvent;
import org.gwtportlets.portlet.client.layout.LayoutUtil;

/**
 * Portlet like widget for GWT.
 */
public abstract class Portlet extends PositionAwareComposite
        implements WidgetFactoryProvider {

    public static final int REFRESH = 1;
    public static final int CONFIGURE = 2;
    public static final int MAXIMIZE = 4;

    public static final int REFRESH_BUSY = 1024;

    protected RefreshHelper refreshHelper;

    public Portlet() {
    }

    public int getFlags() {
        return (isConfigureSupported() ? CONFIGURE : 0)
            | (isRefreshBusy() ? REFRESH_BUSY : 0);
    }

    public boolean isConfigureSupported() {
        return false;
    }

    public void configure() {
        Window.alert("configure " + getWidgetName() + " not implemented");
    }

    public String getWidgetName() {
        String s = getClass().getName();
        s = s.substring(s.lastIndexOf('.') + 1);
        if (s.endsWith("Widget")) {
            s = s.substring(0, s.length() - 6);
        } else if (s.endsWith("Portlet")) {
            s = s.substring(0, s.length() - 7);
        }
        return LayoutUtil.insertSpaces(s);
    }

    public String getWidgetTitle() {
        return getWidgetName();
    }

    /**
     * Call this when our title or flags may have changed.
     */
    protected void fireChangeEvent() {
        BroadcastManager.get().broadcastUp(this, new WidgetChangeEvent(this));
    }

    /**
     * Call this when our configuration may have changed in response to user
     * action (e.g. a call to {@link #configure()}). Listeners may save the
     * page we are on or perform other actions.
     */
    protected void fireConfigChangeEvent() {
        BroadcastManager.get().broadcastUp(this, new WidgetConfigChangeEvent(this));
    }

    /**
     * Broadcast ev to our children. This is a NOP unless our wrapped widget
     * (i.e. the one set by {@link #initWidget(com.google.gwt.user.client.ui.Widget)})
     * implements HasWidgets and/or BroadcastListener.
     */
    protected void broadcastDown(Object ev) {
        BroadcastManager.get().broadcast(getWidget(), ev);
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        String n = getClass().getName();
        s.append(n.substring(n.lastIndexOf('.') + 1));
        s.append('@').append(Integer.toHexString(hashCode()));
        return s.toString();
    }

    /**
     * Is a refresh currently underway?
     */
    public boolean isRefreshBusy() {
        return refreshHelper != null && refreshHelper.isRefreshBusy();
    }

    public void refresh() {
        refresh(createWidgetFactory(), null);
    }

    /**
     * Refresh using the supplied WidgetFactory.
     */
    public void refresh(WidgetFactory wf) {
        refresh(wf, null);
    }

    /**
     * Refresh using the supplied WidgetFactory.
     * 
     * @param callback Invoked after completion or failure if not null
     */
    public void refresh(WidgetFactory wf, AsyncCallback<WidgetFactory> callback) {
        refresh(wf, callback, false);
    }


    /**
     * Refresh using the supplied WidgetFactory.
     *
     * @param callback Invoked after completion or failure if not null
     * @param quiet If true then spinners and so on will not deploy for this
 *              refresh
     */
    public void refresh(WidgetFactory wf, AsyncCallback<WidgetFactory> callback,
            boolean quiet) {
        ensureRefreshHelper();
        refreshHelper.refresh(this, wf, callback, quiet);
    }

    /**
     * If we do not have a RefreshHelper then create one.
     */
    protected void ensureRefreshHelper() {
        if (refreshHelper == null) {
            refreshHelper = new RefreshHelper();
        }
    }

}
