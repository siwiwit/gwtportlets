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

import com.google.gwt.user.client.ui.HTML;
import org.gwtportlets.portlet.client.AbstractWidgetFactory;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.WidgetInfo;

/**
 * Widget used as a placeholder to make editing layouts easier.
 */
public class PlaceholderPortlet extends Portlet {

    private HTML html = new HTML();
    private int counter;

    public PlaceholderPortlet() {
        initWidget(html);
        setStyleName("portlet-placeholder");
    }

    public void boundsUpdated() {
        html.setHTML("Placeholder(" + ++counter + ")");
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    @WidgetInfo(description = "Dummy widget used to make editing layouts easier")
    public static class Factory extends AbstractWidgetFactory<PlaceholderPortlet> {

        public Factory() {
        }

        public Factory(PlaceholderPortlet w) {
            super(w);
        }

        public PlaceholderPortlet createWidget() {
            return new PlaceholderPortlet();
        }
    }
}