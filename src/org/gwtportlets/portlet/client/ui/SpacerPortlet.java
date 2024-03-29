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

import com.google.gwt.user.client.ui.SimplePanel;
import org.gwtportlets.portlet.client.AbstractWidgetFactory;
import org.gwtportlets.portlet.client.WidgetFactory;

/**
 * Empty Portlet used create blank space in layouts.
 */
public class SpacerPortlet extends Portlet {

    public SpacerPortlet() {
        initWidget(new SimplePanel());
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    public static class Factory extends AbstractWidgetFactory<SpacerPortlet> {

        public Factory() {
        }

        public Factory(SpacerPortlet w) {
            super(w);
        }

        public SpacerPortlet createWidget() {
            return new SpacerPortlet();
        }
    }
}