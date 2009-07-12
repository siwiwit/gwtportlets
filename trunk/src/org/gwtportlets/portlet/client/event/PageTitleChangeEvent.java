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

import java.util.EventObject;

/**
 * Fired when the title of the current page changes. Portlets on the page
 * can react to this event to display the title.
 *
 * @see org.gwtportlets.portlet.client.ui.PageTitlePortlet
 */
public class PageTitleChangeEvent extends EventObject {

    private String pageTitle;

    public PageTitleChangeEvent(Object source, String pageTitle) {
        super(source);
        this.pageTitle = pageTitle;
    }

    public String getPageTitle() {
        return pageTitle;
    }
}