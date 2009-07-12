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

package org.gwtportlets.portlet.client.impl;

import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.layout.LDOM;

/**
 * Methods for peformance monitoring and layout debugging.
 */
public class LayoutPerfImplEnabled extends LayoutPerfImpl {

    private StringBuffer log = new StringBuffer(1024);
    private long lastTime;
    private String indent = " ";

    public String getLog() {
        return log.toString();
    }

    public void clear() {
        log = new StringBuffer(1024);
        lastTime = System.currentTimeMillis();
    }

    private void preLog() {
        int ms = (int)(System.currentTimeMillis() - lastTime);
        if (ms < 10) {
            log.append("   ");
        } else if (ms < 100) {
            log.append("  ");
        } else if (ms < 1000) {
            log.append(" ");
        }
        log.append(ms).append(indent);
    }

    public void log(String s) {
        preLog();
        log.append(s).append('\n');
    }

    public void enter(Widget w) {
        preLog();
        String t = w.getClass().getName();
        t = t.substring(t.lastIndexOf('.') + 1);
        log.append(t).append('@').append(w.hashCode()).append(": ")
                .append(LDOM.getBounds(w))
                .append('\n');
        indent = indent + "  ";
    }

    public void leave() {
        indent = indent.substring(0, indent.length() - 2);
    }

}