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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import org.gwtportlets.portlet.client.layout.Layout;
import org.gwtportlets.portlet.client.layout.RowLayout;
import org.gwtportlets.portlet.client.util.SyncToClientArea;

/**
 * This panel takes up the whole browser client area and redoes its layout
 * when the browser is resized. It adds itself to the root panel and disables
 * scrolling for the browser window when constructed.
 */
public class ClientAreaPanel extends RefreshPanel {

    private final SyncToClientArea sync = new SyncToClientArea(this);

    public ClientAreaPanel() {
        this(new RowLayout());
    }

    public ClientAreaPanel(Layout layout) {
        super(layout);
        Window.enableScrolling(false);
        RootPanel.get().add(this);
    }

    protected void onLoad() {
        sync.resizeWidget();
        sync.startListening();
    }

    protected void onUnload() {
        sync.stopListening();
    }
    
}