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

package org.gwtportlets.portlet.client.layout;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.WidgetFactoryProvider;

/**
 * Widget's that are also containers (i.e. use layout managers and so on)
 * implement this.
 */
public interface Container extends HasWidgets, IndexedPanel,
        WidgetFactoryProvider, PositionAware, HasLayoutHandlers {

    /**
     * Get a short type description for this container (e.g. 'Row',
     * 'Column' etc.). This may depend on the layout or other container settings.
     */
    public String getDescription();
    
    /**
     * Get the layout of this container.
     */
    public Layout getLayout();

    /**
     * Set the layout strategy of this container. This will not redo the layout.
     */
    public void setLayout(Layout layout);

    /**
     * Get the constraints for the widget.
     */
    public LayoutConstraints getLayoutConstraints(Widget widget);

    /**
     * Set the constraints for the widget. This does not redo the layout.
     */
    public void setLayoutConstraints(Widget widget, LayoutConstraints constraints);

    /**
     * Redo this containers layout. Note that containers do not automatically
     * call layout() when widgets are added/removed etc. Only resizing the
     * container triggers automatic layout.
     */
    public void layout();

    /**
     * Get the parent of this container for event propagation purposes.
     * This might be a Widget that is not an ancestor of this container
     * in the DOM tree e.g. when a portlet is maximized by a TitlePanel.
     * Most containers can just return getParent().
     */
    public Widget getLogicalParent();

    /**
     * Add a widget with constraints. This does not redo the layout.
     */
    public void add(Widget w, LayoutConstraints constraints);

    /**
     * Add a widget with FloatLayoutConstraints. This does not redo the layout.
     */
    public void add(Widget widget, float constraints);

    /**
     * Add a widget with StringLayoutConstraints. This does not redo the layout.
     */
    public void add(Widget widget, String constraints);
    
    /**
     * Inserts a widget before the specified index. If the Widget is already
     * attached to the container, it will be moved to the specified index.
     * This does not redo the layout.
     */
    public void insert(Widget w, int beforeIndex, LayoutConstraints constraints);

    /**
     * Will maximizing widgets stop at this container?
     */
    public boolean isLimitMaximize();

    /**
     * Clears all of the object's style names and sets it to the given style.
     * 
     * @see com.google.gwt.user.client.ui.UIObject#setStyleName(String)
     */
    public void setStyleName(String style);

    /**
     * Gets all of the object's style names, as a space-separated list.
     *
     * @see com.google.gwt.user.client.ui.UIObject#getStyleName() 
     */
    public String getStyleName();
    
    /**
     * Gets a handle to the object's underlying DOM element.
     *
     * @see com.google.gwt.user.client.ui.UIObject#getElement()  
     */
    public Element getElement();

}
