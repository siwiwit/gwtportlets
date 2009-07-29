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

package org.gwtportlets.portlet.client.edit;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.WidgetFactoryProvider;
import org.gwtportlets.portlet.client.layout.Container;
import org.gwtportlets.portlet.client.ui.Portlet;

/**
 * Simple widget editor that supports selection of a style from a menu.
 * If no styleNames are set then the layout editor manager is asked for
 * style names for each widget. Create 'Configure' option for configurable
 * Portlet's.
 * 
 * @see org.gwtportlets.portlet.client.ui.Portlet#isConfigureSupported()
 */
public class DefaultWidgetEditor implements WidgetEditor {

    private String[] styleNames;

    public DefaultWidgetEditor() {
    }

    public String[] getStyleNames() {
        return styleNames;
    }

    /**
     * Set the available style names. Include a null in the array for a
     * an option to enter a style name and use that. If styleNames is null
     * then the manager is asked for style names for each widget.
     *
     * @see PageEditor#getStyleNamesFor(Widget)
     */
    public void setStyleNames(String[] styleNames) {
        this.styleNames = styleNames;
    }

    public void createMenuItems(final PageEditor manager, MenuBar bar,
            final Widget widget) {
        if (!(widget instanceof WidgetFactoryProvider)) {
            return; // don't edit style if it won't be preserved by a factory
        }

        MenuBar sm = manager.createMenuBar(true);

        MenuItem mi = createRemoveStyleMenuItem(manager, widget);
        if (mi != null) {
            sm.addItem(mi);
        }

        String[] a = styleNames == null
                ? manager.getStyleNamesFor(widget)
                : styleNames;
        if (a != null) {
            for (int i = 0; i < a.length; i++) {
                String s = a[i];
                mi = s == null
                        ? createEnterStyleMenuItem(manager, widget)
                        : createChangeStyleMenuItem(manager, widget, s);
                if (mi != null) {
                    sm.addItem(mi);
                }
            }
        } else {
            mi = createEnterStyleMenuItem(manager, widget);
            if (mi != null) {
                sm.addItem(mi);
            }
        }

        bar.addItem("Style", sm);

        if (widget instanceof Portlet) {
            Portlet p = (Portlet)widget;
            if (p.isConfigureSupported()) {
                bar.addItem(createConfigurePortletMenuItem(p));
            }
        }
    }

    /**
     * Create a menu item to change the style of widget.
     */
    protected MenuItem createChangeStyleMenuItem(
            final PageEditor manager, final Widget widget,
            final String styleName) {
        MenuItem mi = new MenuItem(styleName, new Command() {
            public void execute() {
                changeStyleName(manager, widget, styleName);
            }
        });
        mi.setTitle("Set style to '" + styleName + "'");
        return mi;
    }

    /**
     * Create a menu item to prompt for a style for a widget and change it.
     */
    protected MenuItem createEnterStyleMenuItem(
            final PageEditor manager, final Widget widget) {
        MenuItem mi = new MenuItem("Enter Style Name", new Command() {
            public void execute() {
                String s = Window.prompt("Enter style name:",
                        widget.getStylePrimaryName());
                if (s != null) {
                    changeStyleName(manager, widget, s);
                }
            }
        });
        mi.setTitle("Capture new style name");
        return mi;
    }

    /**
     * Create a menu item to remove the primary style from the widget or
     * return null for no such option.
     */
    protected MenuItem createRemoveStyleMenuItem(
            final PageEditor manager, final Widget widget) {
        String c = widget.getStylePrimaryName();
        if (c == null || c.length() == 0) {
            return null;
        }
        MenuItem mi = new MenuItem("Remove Style " + c, new Command() {
            public void execute() {
                changeStyleName(manager, widget, "");
            }
        });
        mi.setTitle("Remove style '" + c + "'");
        return mi;
    }

    /**
     * Change the style of the widget. Includes undo support.
     */
    protected void changeStyleName(PageEditor manager, Widget widget,
            String styleName) {
        String c = widget.getStyleName();
        if (styleName.equals(c)) {
            return;
        }
        manager.beginUndo(
            styleName.length() == 0 ? "Remove Style " + c : "Set Style " + styleName);
        widget.setStyleName(styleName);
        Widget p = widget.getParent();
        if (p instanceof Container) {
            ((Container)p).layout(); // border or padding of widget may have changed
        }
    }

    /**
     * Create a menu item to configure the Portlet.
     */
    protected MenuItem createConfigurePortletMenuItem(final Portlet portlet) {
        MenuItem mi = new MenuItem("Configure...", new Command() {
            public void execute() {
                portlet.configure();
            }
        });
        mi.setTitle("Configure " + portlet.getWidgetName());
        return mi;
    }
}
