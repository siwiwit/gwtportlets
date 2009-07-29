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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.layout.PositionAware;

/**
 * Composite that passes boundsUpdated calls on to its widget if that widget
 * implements PositionAware. Use this when building a Composite that wraps
 * a {@link org.gwtportlets.portlet.client.layout.Container}.
 */
public abstract class PositionAwareComposite extends Composite
        implements PositionAware {

    public void boundsUpdated() {
        Widget w = getWidget();
        if (w instanceof PositionAware) {
            ((PositionAware)w).boundsUpdated();
        }
    }
}
