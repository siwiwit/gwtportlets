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

package org.gwtportlets.portlet.client.util;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.layout.LDOM;

/**
 * Positions a widget to fill the browsers client area. Call
 * {@link #addResizeHandler()} to start automatically resizing the widget when
 * the browser client area size changes.
 */
public class SyncToClientArea implements ResizeHandler {

    private final Widget widget;
    private Timer timer;
    private HandlerRegistration handlerRegistration;

    public SyncToClientArea(Widget widget) {
        this.widget = widget;
    }

    public void onResize(ResizeEvent event) {
        if (timer != null) {
            timer.cancel();
        } else {
            timer = new Timer() {
                public void run() {
                    if (widget.isAttached()) {
                        resizeWidget();
                    }
                }
            };
        }
        timer.schedule(200);
    }

    /**
     * Resize our widget to match the client area size.
     */
    public void resizeWidget() {
        int w = Window.getClientWidth();
        int h = Window.getClientHeight();
        if (LDOM.isScrollbarWorkaroundRequired()) {
            LDOM.setBounds(widget, 0, 0, w - 32, h - 32);
        }
        LDOM.setBounds(widget, 0, 0, w, h);
    }

    /**
     * Start listening for window resize events.
     */
    public void addResizeHandler() {
        removeResizeHandler();
        handlerRegistration = Window.addResizeHandler(this);
    }

    /**
     * Stop listening for window resize events.
     */
    public void removeResizeHandler() {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            handlerRegistration = null;
        }
    }
}