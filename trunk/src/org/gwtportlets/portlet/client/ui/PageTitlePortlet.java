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
import org.gwtportlets.portlet.client.event.BroadcastListener;
import org.gwtportlets.portlet.client.event.PageTitleChangeEvent;

/**
 * Displays title of the current page and updates it whenever a
 * PageTitleChangeEvent is received.
 */
public class PageTitlePortlet extends Portlet implements BroadcastListener {

    private Label label = new Label("Page Title");

    public PageTitlePortlet() {
        initWidget(label);
        setStyleName("portlet-page-title");
    }

    public void onBroadcast(Object ev) {
        if (ev instanceof PageTitleChangeEvent) {
            label.setText(((PageTitleChangeEvent)ev).getPageTitle());
        }
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory();
    }

    public static class Factory extends PortletFactory<PageTitlePortlet> {

        public Factory() {
        }

        public PageTitlePortlet createWidget() {
            return new PageTitlePortlet();
        }
    }
}