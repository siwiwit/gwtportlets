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

/**
 * Layout strategy or manager.
 */
public interface Layout {

    /**
     * Get a short description for this layout (e.g. 'Row', 'Column' etc.).
     * This may depend on the layout settings.
     */
    public String getDescription();

    /**
     * Layout the widgets. The constraints of each widget may be set or updated
     * as part of this process. Any repositioned widgets that implement
     * PositionAware must be notified. The layout must handle invalid
     * constraints and constraints from other Layout's gracefully (typically
     * by fixing or replacing them). In particular LayoutPanel has some
     * convenience add methods that add widgets with
     * {@link FloatLayoutConstraints} and {@link StringLayoutConstraints} and
     * these should be handled in a reasonable way.
     *
     * @param contentLeft Left edge of content area in the container
     * @param contentTop Top edge of content area in the container
     * @param width Width of content area
     * @param height Height of content area
     */
    public void layoutWidgets(Container container,
            int contentLeft, int contentTop, int width, int height);

    /**
     * If possible convert the layoutConstraints into something suitable for
     * this layout. If this is not possible then return null. Note that
     * layoutConstraint's may be null.
     */
    public LayoutConstraints convertConstraints(LayoutConstraints constraints);

    /**
     * Create an object that can be used to recreate this layout.
     */
    public LayoutFactory createLayoutFactory();

}
