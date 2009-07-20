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

package org.gwtportlets.portlet.client.ui;

import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.*;
import org.gwtportlets.portlet.client.layout.LayoutUtil;
import org.gwtportlets.portlet.client.layout.*;
import org.gwtportlets.portlet.client.event.WidgetChangeEvent;
import org.gwtportlets.portlet.client.event.BroadcastListener;

/**
 * Displays a refresh spinner when its child is refreshing. Delegates to
 * a LayoutPanel to hold its children.
 */
public class RefreshPanel extends DelegatingContainer
        implements BroadcastListener {

    private int lastFlags = -1;
    private boolean limitMaximize;

    private final LayoutPanel outer = new LayoutPanel(new DeckLayout());
    private final LayoutPanel body;

    public RefreshPanel() {
        this(new RowLayout());
    }

    public RefreshPanel(Layout layout) {
        body = new LayoutPanel(layout);
        initWidget(outer);
        outer.add(body);
        setStyleName("portlet-refresh");
    }

    public RefreshPanel(Widget child) {
        this();
        add(child);
    }

    protected void onLoad() {
        lastFlags = -1;
        update();
    }

    public void setStyleName(String style) {
        super.setStyleName(style);
        body.setStyleName(style + "-body");
    }

    protected Container getDelegate() {
        return body;
    }

    public String getDescription() {
        return body.getDescription();
    }

    public boolean isLimitMaximize() {
        return limitMaximize;
    }

    public void setLimitMaximize(boolean limitMaximize) {
        this.limitMaximize = limitMaximize;
    }

    public void layout() {
        update();
        super.layout();
    }

    public void onBroadcast(Object ev) {
        if (ev instanceof WidgetChangeEvent) {
            update();
        }
    }

    public void update() {
        Portlet child = LayoutUtil.findPortletChild(body);
        int flags = child != null ? child.getFlags() : 0;
        if (flags != lastFlags) {
            lastFlags = flags;
            setLoading((flags & Portlet.REFRESH_BUSY) != 0);
        }
    }

    /**
     * Show/hide our loading spinner.
     */
    public void setLoading(boolean on) {
        if (on != isLoading()) {
            if (on) {
                outer.add(new LoadingWidget());
            } else {
                for (int i = outer.getWidgetCount() - 1; i > 0; i--) {
                    outer.remove(i);
                }
            }
            outer.layout();
        }
    }

    public boolean isLoading() {
        return outer.getWidgetCount() > 1;
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    @WidgetInfo(description = "Displays a spinner when contained widget is refreshed")
    public static class Factory extends ContainerFactory<RefreshPanel> {

        public Factory() {
        }

        public Factory(RefreshPanel cp) {
            super(cp);
        }

        public RefreshPanel createWidget() {
            return new RefreshPanel();
        }
    }
}