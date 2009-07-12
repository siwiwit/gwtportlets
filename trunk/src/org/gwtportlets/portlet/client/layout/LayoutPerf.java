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

package org.gwtportlets.portlet.client.layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.impl.LayoutPerfImpl;

/**
 * Methods for peformance monitoring and layout debugging.
 */
public class LayoutPerf {

    private static LayoutPerfImpl impl = (LayoutPerfImpl)GWT.create(LayoutPerfImpl.class);

    public static String getLog() {
        return impl.getLog();
    }

    public static void clear() {
        impl.clear();
    }

    public static void log(String s) {
        impl.log(s);
    }

    public static void enter(Widget w) {
        impl.enter(w);
    }

    public static void leave() {
        impl.leave();
    }

}
