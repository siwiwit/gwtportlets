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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.event.BroadcastManager;
import org.gwtportlets.portlet.client.event.WidgetChangeEvent;
import org.gwtportlets.portlet.client.layout.LayoutUtil;
import org.gwtportlets.portlet.client.WidgetFactoryProvider;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.WidgetRefreshHook;

/**
 * Support class to refresh widgets.
 */
public class RefreshHelper {

    private int refreshBusy;

    public RefreshHelper() {
    }

    /**
     * Refresh the widget. NOP if no WidgetRefreshHandler has been set.
     *
     * @param callback Invoked after completion or failure if not null
     * 
     * @see org.gwtportlets.portlet.client.WidgetRefreshHook.App#get()
     */
    public void refresh(final Widget w, AsyncCallback<WidgetFactory> callback) {
        refresh(w, ((WidgetFactoryProvider)w).createWidgetFactory(), callback, false);
    }

    /**
     * Refresh the widget using the WidgetFactory. NOP if no
     * WidgetRefreshHandler has been set.
     *
     * @param callback Invoked after completion or failure if not null
     * @param quiet If true then {@link #isRefreshBusy()} will return false
     *              for this refresh i.e. spinners etc will not deploy
     * @see org.gwtportlets.portlet.client.WidgetRefreshHook.App#get()
     */
    public void refresh(final Widget w, WidgetFactory wf,
            final AsyncCallback<WidgetFactory> callback, final boolean quiet) {
        WidgetRefreshHook rh = WidgetRefreshHook.App.get();
        if (rh == null) {
            return;
        }
        LayoutUtil.clearDoNotSendToServerFields(wf);
        if (!quiet) {
            ++refreshBusy;
        }
        fireChangeEvent(w);
        rh.refresh(w, wf, new AsyncCallback<WidgetFactory>() {
            public void onFailure(Throwable caught) {
                if (!quiet) {
                    --refreshBusy;
                }
                RefreshHelper.this.onRefreshCallFailure(w, caught);
                if (callback != null) {
                    callback.onFailure(caught);
                }
            }
            public void onSuccess(WidgetFactory wf) {
                if (!quiet) {
                    --refreshBusy;
                }
                RefreshHelper.this.onRefreshCallSuccess(w, wf);
                if (callback != null) {
                    callback.onSuccess(wf);
                }
            }
        });
    }

    protected void fireChangeEvent(Widget w) {
        BroadcastManager.get().broadcastUp(w, new WidgetChangeEvent(w));
    }

    public void onRefreshCallFailure(Widget w, Throwable caught) {
        fireChangeEvent(w);
        GWT.log("Refresh failed: " + caught, caught);
        WidgetRefreshHook.App.get().onRefreshCallFailure(w, caught);
    }

    public void onRefreshCallSuccess(Widget w, WidgetFactory wf) {
        try {
            wf.refresh(w);
        } catch (Exception e) {
            onRefreshFailed(w, wf, e);
        }
        fireChangeEvent(w);
    }

    protected void onRefreshFailed(Widget w, WidgetFactory wf, Exception e) {
        GWT.log(e.toString(), e);
    }

    public boolean isRefreshBusy() {
        return refreshBusy > 0;
    }

    public int getRefreshBusy() {
        return refreshBusy;
    }
}
