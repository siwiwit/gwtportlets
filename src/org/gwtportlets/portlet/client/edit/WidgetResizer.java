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

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.ui.LayoutPanel;
import org.gwtportlets.portlet.client.util.DragAdapter;
import org.gwtportlets.portlet.client.util.Rectangle;
import org.gwtportlets.portlet.client.layout.RowLayout;
import org.gwtportlets.portlet.client.layout.LDOM;
import org.gwtportlets.portlet.client.layout.PositionAware;

/**
 * Widget used to outline widgets in a layout. Also supports resizing by
 * dragging the right or bottom edge,
 */
class WidgetResizer extends Composite implements PositionAware {

    private final PageEditor manager;
    private final LayoutEditor editor;
    private final Widget target;

    private LayoutPanel panel = new LayoutPanel(new RowLayout(false, 0));
    private FlowPanel infoPanel = new FlowPanel();
    private HTML bar = new HTML();

    private boolean dragBottom;
    private Widget info;
    private long lastBarDragTime;
    private boolean needsLayout;

    private static final int BAR_SIZE = 5;

    public WidgetResizer(PageEditor manager, LayoutEditor editor,
            Widget target) {
        this.manager = manager;
        this.editor = editor;
        this.target = target;

        panel.add(infoPanel, new RowLayout.Constraints(RowLayout.Constraints.HIDDEN));
        panel.add(bar, new RowLayout.Constraints(BAR_SIZE));
        initWidget(panel);

        setStyleName("portlet-ed-resizer");
        infoPanel.setStyleName("portlet-ed-resizer-inner");
        bar.setStyleName("portlet-ed-bar");

        panel.addMouseListener(new Dragger(panel));
        bar.addMouseListener(new BarDragger(bar));

        panel.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                // workaround to ignore the click event that comes through in
                // response to the mouse up for a drag operation
                if (System.currentTimeMillis() - lastBarDragTime > 200L) {
                    WidgetResizer.this.manager.toggleMenuForWidget(WidgetResizer.this);
                }
            }
        });
    }

    public void boundsUpdated() {
        if (needsLayout) {
            needsLayout = false;
            panel.layout();
        } else {
            panel.boundsUpdated();
        }
    }

    public LayoutEditor getEditor() {
        return editor;
    }

    public Widget getTarget() {
        return target;
    }

    /**
     * Sync our position etc to match our target widget and its
     * constraints.
     */
    public void update() {
        dragBottom = editor.isDragBottomEdge(target);
        ((RowLayout)panel.getLayout()).setColumn(dragBottom);
        if (dragBottom) {
            infoPanel.addStyleDependentName("bottom");
            bar.addStyleDependentName("bottom");            
        } else {
            infoPanel.removeStyleDependentName("bottom");
            bar.removeStyleDependentName("bottom");            
        }

        Widget newInfo = editor.getConstraintInfo(target, info);
        if (newInfo != info) {
            if (info != null) {
                infoPanel.remove(info);
            }
            info = newInfo;
            if (info != null) {
                infoPanel.add(info);
            }
        }

        Rectangle r = LDOM.getBounds(target);
        String rs = r.toString();
        setTitle(manager.getWidgetDescription(target) + " (" + rs + ")");
        bar.setTitle("Drag to resize (" + rs + ")");
        if (!isAttached()) {
            RootPanel.get().add(this);
        }
        LDOM.setBounds(this, r);
    }

    /**
     * Destroy this resizer.
     */
    public void dispose() {
        if (isAttached()) {
            RootPanel.get().remove(this);
        }
    }

    /**
     * Adjust our targets constraints based on the distance dragged.
     */
    private void barDragged(int delta) {
        lastBarDragTime = System.currentTimeMillis();
        needsLayout = true;
        manager.beginUndo("Resize " + manager.getWidgetDescription(target));
        editor.resizeWidget(target, delta);
    }

    /**
     * Drags this whole resize widget to move our widget around.
     */
    private class Dragger extends DragAdapter {

        public Dragger(Widget target) {
            super(target);
        }

        private int getTargetTop(int clientY) {
            return getStartAbsoluteTop() + clientY - getStartClientY();
        }

        private int getTargetLeft(int clientX) {
            return getStartAbsoluteLeft() + clientX - getStartClientX();
        }

        protected void onDrag(int clientX, int clientY) {
            Element e = getElement();
            int left = getTargetLeft(clientX);
            int top = getTargetTop(clientY);
            LDOM.setClientPosition(e, left, top);
            manager.dragWidget(editor, WidgetResizer.this.getTarget(),
                    left, top);
        }

        protected void onDrop(int clientX, int clientY) {
            manager.dropWidget(
                    getTargetLeft(clientX), getTargetTop(clientY));
        }
    }

    /**
     * Drags our resize bar.
     */
    private class BarDragger extends DragAdapter {

        public BarDragger(Widget target) {
            super(target);
        }

        private int getDistance(int clientX, int clientY) {
            return dragBottom
                    ? clientY - getStartClientY()
                    : clientX - getStartClientX();
        }

        protected void onDrag(int clientX, int clientY) {
            int dist = getDistance(clientX, clientY);
            Element e = getTarget().getElement();
            int x = getStartAbsoluteLeft();
            int y = getStartAbsoluteTop();
            if (dragBottom) {
                y += dist;
            } else {
                x += dist;
            }
            LDOM.setClientPosition(e, x, y);
        }

        protected void onDrop(int clientX, int clientY) {
            barDragged(getDistance(clientX, clientY));
        }
    }

}