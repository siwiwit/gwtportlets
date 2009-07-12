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

import com.google.gwt.user.client.ui.Label;
import org.gwtportlets.portlet.client.WidgetFactory;

/**
 * Placeholder widget used when a WidgetFactory createWidget or refresh call
 * fails. Stores the original factory and uses that as needed.
 */
public class BadWidgetPlaceholder extends Portlet {

    private WidgetFactory badFactory;

    public BadWidgetPlaceholder(WidgetFactory badFactory, Exception e) {
        this.badFactory = badFactory;

        StringBuffer s = new StringBuffer();
        s.append(badFactory.getClass().getName()).append('\n');
        s.append(tos(badFactory)).append('\n');
        s.append(e.getClass().getName()).append(":\n");
        s.append(e.getMessage());
        
        initWidget(new Label(s.toString()));
        setStyleName("portlet-badwidget");
    }

    private String tos(Object o) {
        try {
            return o.toString();
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    public WidgetFactory createWidgetFactory() {
        return badFactory;
    }

    public String getWidgetTitle() {
        return "Bad Widget";
    }

    public void refresh() {
        // do nothing
    }
}
