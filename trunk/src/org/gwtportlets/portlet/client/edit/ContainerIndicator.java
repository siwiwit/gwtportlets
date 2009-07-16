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

package org.gwtportlets.portlet.client.edit;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.layout.LDOM;
import org.gwtportlets.portlet.client.layout.RowLayout;
import org.gwtportlets.portlet.client.ui.LayoutPanel;
import org.gwtportlets.portlet.client.util.Rectangle;

/**
 * Used to outline containers when they are empty.
 */
public class ContainerIndicator extends Composite {

    private final PageEditor manager;
    private final LayoutEditor editor;

    private final LayoutPanel panel = new LayoutPanel(new RowLayout(false, 0));
    private final FlowPanel infoPanel = new FlowPanel();

    private Widget info;

    public ContainerIndicator(PageEditor manager,
            LayoutEditor editor) {
        this.manager = manager;
        this.editor = editor;

        panel.add(infoPanel, new RowLayout.Constraints(RowLayout.Constraints.HIDDEN));
        initWidget(panel);

        setStyleName("portlet-ed-empty-indicator");

        panel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ContainerIndicator.this.manager.toggleMenuForContainer(
                        ContainerIndicator.this);
            }
        });
    }

    public LayoutEditor getEditor() {
        return editor;
    }

    public void update() {
        Widget newInfo = editor.getContainerInfo(info);
        if (newInfo != info) {
            if (info != null) {
                infoPanel.remove(info);
            }
            info = newInfo;
            if (info != null) {
                infoPanel.add(info);
            }
        }

        Widget w = (Widget)editor.getContainer();
        Rectangle r = LDOM.getBounds(w);
        panel.setTitle(manager.getWidgetDescription(w) +
                ", click for menu (" + r + ")");
        if (!isAttached()) {
            RootPanel.get().add(this);
        }
        LDOM.setBounds(this, r);
    }

    public void dispose() {
        if (isAttached()) {
            RootPanel.get().remove(this);            
        }
    }
    
}
