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
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.WidgetFactoryMetaData;
import org.gwtportlets.portlet.client.WidgetFactoryVisitor;
import org.gwtportlets.portlet.client.impl.WidgetFactoryHelper;
import org.gwtportlets.portlet.client.ui.BadWidgetPlaceholder;
import org.gwtportlets.portlet.client.ui.Portlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static utility methods for working with layout heirachies and
 * WidgetFactory's.
 */
public class LayoutUtil {

    private static WidgetFactoryHelper widgetFactoryHelper;

    static {
        widgetFactoryHelper = GWT.create(WidgetFactoryHelper.class);
    }

    /**
     * Find the topmost {@link org.gwtportlets.portlet.client.layout.Container}
     * in the heirachy that w belongs to or null if none found.
     */
    public static Container getTopmostContainer(Widget w) {
        Container ans = null;
        for (; w != null; ) {
            if (w instanceof Container) {
                ans = (Container)w;
            }
            w = getLogicalParent(w);
        }
        return ans;
    }

    /**
     * Get the logical parent of w. This will normally be its parent widget
     * but might be a widget in a different heirachy (see
     * {@link org.gwtportlets.portlet.client.layout.Container#getLogicalParent()}).
     */
    public static Widget getLogicalParent(Widget w) {
        return w instanceof Container
                ? ((Container)w).getLogicalParent()
                : w.getParent();
    }

    /**
     * Find the next stop point for maximizing windows in the tree by searching
     * upwards from w. May return null if none found.
     */
    public static Container getNextMaximizeStop(Widget w) {
        for (;;) {
            w = w.getParent();
            if (w instanceof Container) {
                Container c = (Container)w;
                if (c.isLimitMaximize() || w.getParent() == null) {
                    return c;
                }
            } else if (w == null) {
                return null;
            }
            if (w.getParent() == null) {
                return null;
            }
        }
    }

    /**
     * Is w a stop point for maximizing widgets?
     */
    public static boolean isMaximizeStop(Widget w) {
        return w instanceof Container && ((Container)w).isLimitMaximize();
    }

    /**
     * Clear all fields marked with
     * {@link org.gwtportlets.portlet.client.DoNotSendToServer} for each
     * factory in the tree.
     */
    public static void clearDoNotSendToServerFields(WidgetFactory root) {
        root.accept(new WidgetFactoryVisitor() {
            public boolean visit(WidgetFactory wf) {
                widgetFactoryHelper.clearDoNotSendToServerFields(wf);
                return true;
            }
        });
    }

    /**
     * Search for a child widget that is a Portlet and return
     * it or null if none. Depth first in child widget index order.
     */
    public static Portlet findPortletChild(Container c) {
        int n = c.getWidgetCount();
        for (int i = 0; i < n; i++) {
            Widget w = c.getWidget(i);
            if (w instanceof Portlet) {
                return (Portlet)w;
            }
            if (w instanceof Container) {
                Portlet a = findPortletChild((Container)w);
                if (a != null) {
                    return a;
                }
            }
        }
        return null;
    }

    /**
     * Insert a space before each new capital letter in s.
     */
    public static String insertSpaces(String s) {
        int n = s.length();
        StringBuffer b = new StringBuffer(n + 3);
        boolean caps = true;
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                if (!caps) {
                    b.append(' ');
                    caps = true;
                }
            } else {
                caps =false;
            }
            b.append(c);
        }
        return b.toString();
    }

    /**
     * Create a widget using wf and return a placeholder error widget if
     * there are errors.
     */
    public static Widget createWidget(WidgetFactory wf) {
        Widget w;
        try {
            w = wf.createWidget();
            wf.refresh(w);
        } catch (Exception e) {
            GWT.log(e.toString(), e);
            w = new BadWidgetPlaceholder(wf, e);
        }
        return w;
    }

    /**
     * Create a list of all classes that implement WidgetFactory. A new list
     * is created with each call.
     */
    public static List<WidgetFactoryMetaData> getWidgetFactoryList() {
        return new ArrayList<WidgetFactoryMetaData>(
                Arrays.asList(widgetFactoryHelper.getWidgetFactoryList()));
    }

    /**
     * Create a WidgetFactory instance.
     */
    public static WidgetFactory createWidgetFactory(int index) {
        return widgetFactoryHelper.createWidgetFactory(index);
    }

}
