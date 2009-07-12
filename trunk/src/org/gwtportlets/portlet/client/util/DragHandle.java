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

import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.layout.LDOM;

/**
 * Add this as a mouse listener to a widget that acts as a drag handle for
 * another widget (the draggee) i.e. the draggee moves when the handle is
 * dragged.<p>
 *
 * Example: <code>handle.addMouseListener(new DragHandle(handle, draggee));</code>
 */
public class DragHandle extends DragAdapter {

    private final Widget draggee;

    private int offsetX;
    private int offsetY;

    public DragHandle(Widget handle, Widget draggee) {
        super(handle);
        this.draggee = draggee;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    protected void onDragStart(int clientX, int clientY) {
        offsetX = clientX - draggee.getAbsoluteLeft();
        offsetY = clientY - draggee.getAbsoluteTop();
    }

    protected void onDrag(int clientX, int clientY) {
        int left = clientX - offsetX;
        int top = clientY - offsetY;
        LDOM.setPosition(draggee.getElement(), left < 0 ? 0 : left,
                top < 0 ? 0 : top);
    }

}
