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

package org.gwtportlets.portlet.client.edit;

import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.layout.Container;
import org.gwtportlets.portlet.client.layout.LayoutConstraints;
import org.gwtportlets.portlet.client.util.Rectangle;

/**
 * Classes that edit a particular implementation of
 * {@link org.gwtportlets.portlet.client.layout.Layout} implement this.
 */
public interface LayoutEditor {

    /**
     * Set the manager in control of the editing operation and the container
     * we are editing.
     */
    public void init(PageEditor manager, Container container);

    /**
     * Stop editing (NOP if not busy editing).
     */
    public void dispose();

    /**
     * Get the container being edited or null if none.
     */
    public Container getContainer();

    /**
     * Can the layout itself be edited ?
     *
     * @see #editLayout(com.google.gwt.event.logical.shared.CloseHandler)
     */
    public boolean isEditLayout();

    /**
     * Edit layout settings and invoke closeHandler.onClose when
     * done.
     */
    public void editLayout(CloseHandler closeHandler);

    /**
     * Can constraints be edited for widget?
     *
     * @see #editConstraints(com.google.gwt.user.client.ui.Widget,ValueChangeHandler,com.google.gwt.event.logical.shared.CloseHandler)
     */
    public boolean isEditConstraints(Widget widget);

    /**
     * Edit constraints for widget and invoke closeHandler.onClose when
     * done. If changeListener is not null then call it whenever the
     * constraints are modified.
     */
    public void editConstraints(Widget widget,
            ValueChangeHandler<Integer> changeHandler, CloseHandler closeHandler);

    /**
     * Add menu options for the widget to bar. Note that if widget is null
     * then general menu options applicable to our container and layout
     * should be added. The added options may be reused accross calls to this
     * method. 
     */
    public void createMenuItems(Widget widget, MenuBar bar);

    /**
     * If the widget can be dropped into our container then return a rectangle
     * that can be used to indicate approximately where it will go. The
     * width and height of the rectangle can be zero if this is not known.
     * The x and y coordinates of the rectangle must be client area coordinates.
     * Return null if we cannot accept the widget for some reason.
     *
     * @param constraints The widgets current layout contraints (if any)
     */
    public Rectangle getDropArea(Widget widget, LayoutConstraints constraints,
            int clientX, int clientY);

    /**
     * Drop the widget into our container if possible and return true else
     * return false. It must be removed from its current parent (if any).
     *
     * @param constraints The widgets current layout contraints (if any)
     * @param overflow The overflow style attribute to use if new constraints
     *                 are created
     */
    public boolean dropWidget(Widget widget, LayoutConstraints constraints,
            String overflow, int clientX, int clientY);

    /**
     * Should the bottom (true) or right (false) edge of the target widget
     * be draggable? 
     */
    public boolean isDragBottomEdge(Widget target);

    /**
     * Return a widget to show information about the container or null if none
     * needed. The info parameter is the widget previously
     * returned by this method or null otherwise.
     * It should be reused if possible.
     */
    public Widget getContainerInfo(Widget info);

    /**
     * Return a widget to show information about the constraints of target
     * or null if none needed. The info parameter is the widget previously
     * returned by this method for the same target or null otherwise.
     * It should be reused if possible.
     */
    public Widget getConstraintInfo(Widget target, Widget info);

    /**
     * The right or bottom edge of the widget has been dragged so resize it
     * if this make sense.
     */
    public void resizeWidget(Widget target, int delta);
}
