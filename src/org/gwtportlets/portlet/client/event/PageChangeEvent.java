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

package org.gwtportlets.portlet.client.event;

import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.layout.Container;

import java.util.EventObject;

/**
 * Fired when the user navigates to a new page. Subclass this and override
 * editPage to support page editing.
 */
public class PageChangeEvent extends EventObject {

    private String pageName;
    private WidgetFactory widgetFactory;
    private boolean editable;
    private boolean handled;

    public PageChangeEvent(Object source) {
        super(source);
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public WidgetFactory getWidgetFactory() {
        return widgetFactory;
    }

    public void setWidgetFactory(WidgetFactory widgetFactory) {
        this.widgetFactory = widgetFactory;
    }

    /**
     * Is the page editable?
     *
     * @see #editPage(org.gwtportlets.portlet.client.layout.Container) 
     */
    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    /**
     * Start editing the page. Override this to support page editing in
     * your application.
     */
    public void editPage(Container container) {
    }
}
