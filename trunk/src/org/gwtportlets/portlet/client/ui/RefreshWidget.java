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

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.RootPanel;
import org.gwtportlets.portlet.client.event.BroadcastListener;
import org.gwtportlets.portlet.client.event.WidgetChangeEvent;
import org.gwtportlets.portlet.client.layout.LDOM;

/**
 * Extends SimplePanel to displays a refresh spinner when its child is
 * refreshing. Use this instead of
 * {@link org.gwtportlets.portlet.client.ui.RefreshPanel} to put an
 * automatic spinner over a portlet not positioned absolutely.
 */
public class RefreshWidget extends SimplePanel implements BroadcastListener {

    private int lastFlags = -1;
    private LoadingWidget loading;

    public RefreshWidget() {
    }

    public RefreshWidget(Widget child) {
        this();
        add(child);
    }

    protected void onLoad() {
        lastFlags = -1;
        update();
    }

    public void onBroadcast(Object ev) {
        if (ev instanceof WidgetChangeEvent) {
            update();
        }
    }

    public void update() {
        Widget c = getWidget();
        int flags = c instanceof Portlet ? ((Portlet)c).getFlags() : 0;
        if (flags != lastFlags) {
            lastFlags = flags;
            if ((flags & Portlet.REFRESH_BUSY) != 0) {
                if (loading == null) {
                    loading = new LoadingWidget();
                }
                if (!loading.isAttached()) {
                    RootPanel.get().add(loading);
                }
                updateLoadingPosition();
            } else if (loading != null && loading.isAttached()) {
                RootPanel.get().remove(loading);
            }
        }
    }

    private void updateLoadingPosition() {
        LDOM.setBounds(loading, LDOM.getBounds(getWidget()));
    }

}
