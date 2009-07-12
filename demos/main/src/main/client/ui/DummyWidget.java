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

package main.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.AbstractWidgetFactory;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.WidgetFactoryProvider;
import org.gwtportlets.portlet.client.layout.Container;
import org.gwtportlets.portlet.client.layout.PositionAware;

/**
 * Dummy widget that just displays its size and the toString of its layout
 * constraints.
 */
public class DummyWidget extends HTML implements PositionAware,
        WidgetFactoryProvider {

    private int counter;

    public DummyWidget(String name) {
        super(name);
        setStylePrimaryName(name);
    }

    public DummyWidget() {
    }

    public void boundsUpdated() {
        Object c = ((Container)getParent()).getLayoutConstraints(this);
        setHTML(getStylePrimaryName() + " " + getAbsoluteLeft() + "," + getAbsoluteTop() +
                "<br>" + getOffsetWidth() + "x" + getOffsetHeight() + " "
                + (++counter) + "<br>" + c);
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    public static class Factory extends AbstractWidgetFactory {

        public Factory() {
        }

        public Factory(DummyWidget w) {
            super(w);
        }

        public Widget createWidget() {
            return new DummyWidget();
        }
    }
}
