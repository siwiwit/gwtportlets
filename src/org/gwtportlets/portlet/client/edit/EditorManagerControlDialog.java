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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.layout.LDOM;
import org.gwtportlets.portlet.client.util.Rectangle;
import org.gwtportlets.portlet.client.ui.CssButton;
import org.gwtportlets.portlet.client.ui.Dialog;

/**
 * Displays the current edit depth etc for the editor manager.
 */
public class EditorManagerControlDialog extends Dialog {

    private final PageEditor manager;

    private FlowPanel levelPanel = new FlowPanel();
    private FlowPanel leftPanel = new FlowPanel();
    private HTML message = new HTML();
    private FlowPanel rightPanel = new FlowPanel();

    private String defaultMessage = "Drag widgets to move or resize";
    private String defaultTooltip = "Drag the thick edge of each widget " +
        "to resize, click for menus";

    private ClickListener levelClickListener= new ClickListener() {
        public void onClick(Widget sender) {
            Button b = (Button)sender;
            b.setFocus(false);
            manager.setEditDepth(Integer.parseInt(b.getText()) - 1);
        }
    };
    
    private static final int WIDTH = 350;
    private static final int HEIGHT = 62;

    public EditorManagerControlDialog(PageEditor manager) {
        super(false, false);
        this.manager = manager;

        setText("Layout Editor");
        setButtonBarVisible(false);
        setMaximizeVisible(false);

        addStyleName("portlet-ed-control");
        levelPanel.setStyleName("portlet-ed-control-level");
        leftPanel.setStyleName("portlet-ed-control-left");
        message.setStyleName("portlet-ed-control-message");
        rightPanel.setStyleName("portlet-ed-control-right");

        Grid g = new Grid(1, 1);
        g.setCellPadding(0);
        g.setCellSpacing(0);
        g.setBorderWidth(0);
        g.setHeight("100%");
        g.setWidget(0, 0, message);
        g.getRowFormatter().setVerticalAlign(0,
                HasVerticalAlignment.ALIGN_MIDDLE);
        g.getCellFormatter().setHorizontalAlignment(0, 0,
                HasHorizontalAlignment.ALIGN_CENTER);

        restoreMessage();

        FlowPanel p = new FlowPanel();
        p.add(levelPanel);
        p.add(leftPanel);
        p.add(rightPanel);
        p.add(g);
        setWidget(p);

        setPixelSize(WIDTH, HEIGHT);
    }

    public boolean onEventPreview(Event event) {
        return manager.onEventPreview(event) && super.onEventPreview(event);
    }

    public void onCloseClick() {
        manager.stopEditing();
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public void setDefaultTooltip(String defaultTooltip) {
        this.defaultTooltip = defaultTooltip;
    }

    private void restoreMessage() {
        message.setHTML(defaultMessage);
        message.setTitle(defaultTooltip);
    }

    /**
     * Sync to our manager and display if not already visible.
     */
    public void update() {
        levelPanel.clear();
        int sel = manager.getEditDepth();
        int max = manager.getMaxEditDepth();
        for (int i = 0; i <= max; i++) {
            String s = Integer.toString(i + 1);
            Button b = new CssButton(s, levelClickListener);
            b.setTitle("Edit layout level " + s + " (Wheel up/down)");
            levelPanel.add(b);
            if (i == sel) {
                b.addStyleDependentName("selected");
            }
        }
        show();
    }

    public void show() {
        // try to center above or below the root widget
        Rectangle r = LDOM.getBounds(manager.getRoot().getElement());
        int y = r.y - HEIGHT - 4;
        if (y < 0) { // not enough space above so try below
            y = r.y + r.height + 4;
            if (y + HEIGHT > Window.getClientHeight()) {
                y = 0; // give up
            }
        }
        int x = r.x + (r.width - WIDTH) / 2;
        if (x < 0) {
            x = 0;
        }
        setPopupPosition(x, y);
        super.show();
    }

    /**
     * Display msg and enable the cancel button. If cancel is clicked then
     * onCancel is invoked.
     *
     * @see #endOperation() 
     */
    public void beginOperation(String htmlMsg, String tooltip,
            boolean hideLevel, ClickListener onCancel) {
        message.setHTML(htmlMsg);
        message.setTitle(tooltip);

        Button cancel = new CssButton("Cancel", onCancel);
        cancel.setTitle("Cancel operation (Esc)");
        rightPanel.add(cancel);

        if (hideLevel) {
            levelPanel.setVisible(false);
        }
    }

    public void endOperation() {
        levelPanel.setVisible(true);
        restoreMessage();
        rightPanel.clear();
    }

    /**
     * This is the panel with the stop button. Add extra stuff here if you
     * need to.
     */
    public FlowPanel getLeftPanel() {
        return leftPanel;
    }

    /**
     * Add extra buttons and so on required by the active operation to this
     * panel. It is cleared by {@link #endOperation()}. 
     */
    public FlowPanel getRightPanel() {
        return rightPanel;
    }

    /**
     * Is the level indicator visible?
     */
    public boolean isLevelVisible() {
        return levelPanel.isVisible();    
    }

}
