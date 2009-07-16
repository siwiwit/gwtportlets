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

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * Create one of these for a widget to provide drag support.
 * Converts all drag coordinates into client area coordinates.
 */
public class DragAdapter implements MouseDownHandler, MouseUpHandler,
        MouseMoveHandler {

    private final Widget target;
    private int state;
    private int startClientX, startClientY;
    private int startAbsoluteLeft, startAbsoluteTop;

    private int NOT_DRAGGING = 0;
    private int ABOUT_TO_DRAG = 1;
    private int DRAGGING = 2;

    /**
     * Target must implement HasMouseDownHandlers, HasMouseUpHandlers and
     * HasMouseMoveHandlers.
     */
    public DragAdapter(Widget target) {
        this.target = target;
        ((HasMouseDownHandlers)target).addMouseDownHandler(this);
        ((HasMouseUpHandlers)target).addMouseUpHandler(this);
        ((HasMouseMoveHandlers)target).addMouseMoveHandler(this);
    }

    public Widget getTarget() {
        return target;
    }

    public boolean isDragging() {
        return state == DRAGGING;
    }

    public int getStartClientX() {
        return startClientX;
    }

    public int getStartClientY() {
        return startClientY;
    }

    /**
     * Get what target.getAbsoluteLeft() returned when dragging started.
     * @see com.google.gwt.user.client.ui.Widget#getAbsoluteLeft()
     */
    public int getStartAbsoluteLeft() {
        return startAbsoluteLeft;
    }

    /**
     * Get what target.getAbsoluteTop() returned when dragging started.
     * @see com.google.gwt.user.client.ui.Widget#getAbsoluteTop()
     */
    public int getStartAbsoluteTop() {
        return startAbsoluteTop;
    }

    /**
     * Dragging has started (mouse down).
     */
    protected void onDragStart(int clientX, int clientY) {
    }

    /**
     * Dragging is in progress (mouse moving).
     */
    protected void onDrag(int clientX, int clientY) {
    }

    /**
     * Dragging has finished (mouse up).
     */
    protected void onDrop(int clientX, int clientY) {
    }

    public void onMouseDown(MouseDownEvent event) {
        if (isDragging()) {
            return;
        }
        NativeEvent ev = event.getNativeEvent();
        ev.preventDefault();
        ev.stopPropagation();
        state = ABOUT_TO_DRAG;
        startClientX = ev.getClientX();
        startClientY = ev.getClientY();
        startAbsoluteLeft = target.getAbsoluteLeft();
        startAbsoluteTop = target.getAbsoluteTop();
        DOM.setCapture(target.getElement());
    }

    public void onMouseMove(MouseMoveEvent event) {
        if (state == NOT_DRAGGING) {
            return;
        }
        NativeEvent ev = event.getNativeEvent();
        ev.preventDefault();
        ev.stopPropagation();
        int x = ev.getClientX();
        int y = ev.getClientY();
        if (state == ABOUT_TO_DRAG) {
            if (x != startClientX || y != startClientY) {
                state = DRAGGING;
                onDragStart(startClientX, startClientY);
            } else {
                return;
            }
        }
        onDrag(x, y);
    }

    public void onMouseUp(MouseUpEvent event) {
        if (state == NOT_DRAGGING) {
            return;
        }
        DOM.releaseCapture(target.getElement());
        NativeEvent ev = event.getNativeEvent();
        ev.preventDefault();
        ev.stopPropagation();
        try {
            if (state == DRAGGING) {
                onDrop(ev.getClientX(), ev.getClientY());
            }
        } finally {
            state = NOT_DRAGGING;
        }
    }

}

