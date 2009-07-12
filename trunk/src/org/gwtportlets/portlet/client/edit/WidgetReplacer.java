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

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.layout.Container;
import org.gwtportlets.portlet.client.layout.LayoutConstraints;

/**
 * Manages exchanging one widget with another in the widget heirachy.
 * Preserves constraints etc.
 */
public class WidgetReplacer {

    public WidgetReplacer() {
    }

    /**
     * Replace old with nw. Throws UnsupportedOperationException if this is
     * not possible i.e. the parent of old is not one we know how to deal
     * with.
     */
    public void replace(Widget old, Widget nw) {
        Widget parent = old.getParent();
        if (parent instanceof Container) {
            Container c = (Container)parent;
            LayoutConstraints lc = c.getLayoutConstraints(old);
            int i = c.getWidgetIndex(old);
            c.remove(i);
            c.insert(nw, i, lc);
            c.layout();
        } else if (parent instanceof HasWidgets) {
            HasWidgets hw = (HasWidgets)parent;
            hw.remove(old);
            hw.add(nw);
        } else {
            throw new UnsupportedOperationException("Unsupported parent " +
                    parent == null ? "null" : parent.getClass().getName());
        }
    }
}
