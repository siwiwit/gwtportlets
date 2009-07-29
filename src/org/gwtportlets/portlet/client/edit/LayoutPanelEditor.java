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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.MenuBar;
import org.gwtportlets.portlet.client.ui.LayoutPanel;

/**
 * Editor for a LayoutPanel. Adds option to open dialog to edit panel
 * settings.
 */
public class LayoutPanelEditor extends DefaultWidgetEditor {

    public LayoutPanelEditor() {
    }

    public void createMenuItems(final PageEditor manager,
            MenuBar bar, final Widget widget) {
        super.createMenuItems(manager, bar, widget);

        bar.addItem("Edit Container...", new Command() {
            public void execute() {
                manager.displayEditWidgetDialog(widget,
                        new LayoutPanelDialog((LayoutPanel)widget));
            }
        }).setTitle("Edit container settings");
    }

}