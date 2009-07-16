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

import org.gwtportlets.portlet.client.AbstractWidgetFactory;
import org.gwtportlets.portlet.client.layout.LayoutUtil;

/**                                                                                 
 * Base factory for {@link org.gwtportlets.portlet.client.ui.Portlet}'s.
 */
public abstract class PortletFactory<T extends Portlet>
        extends AbstractWidgetFactory<T> {

    private transient boolean fromPortlet;

    protected PortletFactory() {
    }

    protected PortletFactory(Portlet p) {
        super(p);
        fromPortlet = true;
    }

    /**
     * Was this factory created directly from a Portlet i.e. on the client
     * side? This is useful if data retrieved from the server must be updated
     * 'once only' on the client side before being used. One example is
     * adjusting java.util.Date fields for timezone differences between the
     * client and the server.
     */
    public boolean isFromPortlet() {
        return fromPortlet;
    }

    /**
     * Get a nice name for this factory for use on menu items and so on.
     */
    public String getName() {
        String s = getClass().getName();
        s = s.substring(s.lastIndexOf('.') + 1);
        if (s.endsWith("Portlet$Factory")) {
            s = s.substring(0, s.length() - 15);
        } else if (s.endsWith("$Factory")) {
            s = s.substring(0, s.length() - 8);
        }
        return LayoutUtil.insertSpaces(s);
    }

    /**
     * Get a category for this factory.
     */
    public String getCategory() {
        String s = getClass().getName();
        int j = s.lastIndexOf('.');
        if (j < 0) {
            return "(none)";
        }
        int i = s.lastIndexOf('.', j - 1);
        return Character.toUpperCase(s.charAt(i + 1)) + s.substring(i + 2, j);
    }

    /**
     * Get a description for this factory.
     */
    public String getDescription() {
        return "";
    }

}
