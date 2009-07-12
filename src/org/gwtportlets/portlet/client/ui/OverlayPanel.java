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

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel;
import org.gwtportlets.portlet.client.util.Rectangle;
import org.gwtportlets.portlet.client.layout.*;

/**
 * LayoutPanel that maintains its position over another container.
 */
public class OverlayPanel extends LayoutPanel implements ContainerListener {

    private Container target;
    private Widget logicalParent;
    private PopupPanel popup;

    /**
     * Create with a {@link org.gwtportlets.portlet.client.layout.DeckLayout}.
     */
    public OverlayPanel() {
        this(new DeckLayout());
    }

    public OverlayPanel(Layout layout) {
        super(layout);
        popup = LDOM.createOverlayPopup();
        // the popup prevents controls from showing through on IE
        // null otherwise
        // todo use something lighter like an IFRAME on its own
    }

    public Widget getLogicalParent() {
        return logicalParent == null ? getParent() : logicalParent;
    }

    public void setLogicalParent(Widget logicalParent) {
        this.logicalParent = logicalParent;
    }

    /**
     * Position us over target and display. Set a target of null to hide.
     */
    public void setTarget(Container target) {
        if (this.target != null) {
            this.target.removeContainerListener(this);
        }
        this.target = target;
        if (target != null) {
            target.addContainerListener(this);
        }
        update();
    }

    public Container getTarget() {
        return target;
    }

    private void update() {
        if (target == null || !((Widget)target).isAttached()) {
            if (isAttached()) {
                if (popup != null) {
                    popup.hide();
                }
                RootPanel.get().remove(this);
            }
        } else {
            Rectangle r = LDOM.getBounds((Widget)target);
            if (!isAttached()) {
                if (popup != null) {
                    popup.setPopupPosition(r.x, r.y);
                    popup.show();
                }
                RootPanel.get().add(this);
            }
            if (popup != null) {
                LDOM.setBounds(popup, r);
            }
            LDOM.setBounds(this, r);
        }
    }

    public void layoutUpdated(Container container) {
        update();            
    }

    public boolean isLimitMaximize() {
        return true;
    }
}
