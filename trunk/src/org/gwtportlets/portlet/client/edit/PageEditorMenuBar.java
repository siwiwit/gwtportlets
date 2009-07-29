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
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.layout.LDOM;
import org.gwtportlets.portlet.client.util.Rectangle;

/**
 * Hides the active menu in the PageEditor when any menu item is activated.
 * Prevents menu from going off screen.
 */
class PageEditorMenuBar extends MenuBar {

    private final PageEditor pageEditor;

    public PageEditorMenuBar(PageEditor pageEditor, boolean vertical) {
        super(vertical);
        this.pageEditor = pageEditor;
        addStyleName("portlet-ed-menu");
        setAutoOpen(true);
    }

    public MenuItem addItem(MenuItem item) {
        MenuItem ans = super.addItem(item);
        final Command cmd = ans.getCommand();
        if (cmd != null) {
            ans.setCommand(new Command() {
                public void execute() {
                    pageEditor.hideMenu();
                    cmd.execute();
                }
            });
        }
        return ans;
    }

    protected void onLoad() {
        super.onLoad();

        final Widget p = getParent();
        if (!(p instanceof PopupPanel)) {
            return;
        }

        // reposition the popup so it does not go off screen etc
        p.setVisible(false);
        final int left = p.getAbsoluteLeft();
        final int top = p.getAbsoluteTop();
        LDOM.setPosition(p.getElement(), 0, 0); // put here so size is not constrained

        DeferredCommand.addCommand(new Command() {
            public void execute() {
                Rectangle nextTo = new Rectangle();
                nextTo.x = left;
                nextTo.y = top;
                nextTo.width = 1;
                nextTo.height = 1;
                Rectangle r = LDOM.getNextToPosition(
                        p.getOffsetWidth(), p.getOffsetHeight(), nextTo, true, 0);
                LDOM.setPosition(p.getElement(), r.x, r.y);
                p.setVisible(true);
            }
        });
    }
}
