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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.*;
import org.gwtportlets.portlet.client.layout.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Container that uses a pluggable layout strategy and constraints to arrange
 * its children.
 */
public class LayoutPanel extends ComplexPanel implements Container {

    private Layout layout;
    private LayoutConstraints[] constraints = new LayoutConstraints[4];
    private int lastWidth = -1;
    private int lastHeight = -1;
    private boolean limitMaximize;
    
    private List layoutListeners;
    private ClickListenerCollection clickListeners;
    private MouseListenerCollection mouseListeners;
    private MouseWheelListenerCollection mouseWheelListeners;

    public LayoutPanel() {
        setElement(DOM.createDiv());
        sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.ONMOUSEWHEEL);
    }

    public LayoutPanel(Layout layout) {
        this();
        this.layout = layout;
    }

    /**
     * Create with RowLayout in column (true) or row (false).
     */
    public LayoutPanel(boolean column) {
        this(new RowLayout(column));
    }

    /**
     * Create with RowLayout in column (true) or row (false) with spacing
     * between widgets in pixels.
     */
    public LayoutPanel(boolean column, int spacing) {
        this(new RowLayout(column, spacing));
    }

    public String getDescription() {
        ensureLayout();
        return layout.getDescription();
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    /**
     * Add a widget with default layout constraints. This does not redo the
     * layout.
     */
    public void add(Widget child) {
        add(child, getElement());
    }

    /**
     * Add a widget with layout constraints. This does not redo the layout.
     */
    public void add(Widget widget, LayoutConstraints constraints) {
        add(widget);
        setLayoutConstraints(widget, constraints);
    }

    /**
     * Add a widget with FloatLayoutConstraints. This does not redo the layout.
     */
    public void add(Widget widget, float constraints) {
        add(widget, new FloatLayoutConstraints(constraints));
    }

    /**
     * Add a widget with StringLayoutConstraints. This does not redo the layout.
     */
    public void add(Widget widget, String constraints) {
        add(widget, new StringLayoutConstraints(constraints));
    }

    public void insert(Widget w, int beforeIndex, LayoutConstraints constraints) {
        insert(w, getElement(), beforeIndex, true);
        int wc = getWidgetCount();
        ensureConstraintsFor(wc - 1);
        for (int i = wc - 2; i >= beforeIndex; i--) {
            this.constraints[i + 1] = this.constraints[i];
        }
        this.constraints[beforeIndex] = constraints;
    }

    /**
     * Remove the widget. This does not redo the layout.
     */
    public boolean remove(Widget w) {
        int i = getWidgetIndex(w);
        if (i < 0) {
            return false;
        }
        int wc = getWidgetCount();
        super.remove(w);
        if (wc > constraints.length) {
            wc = constraints.length;
        }
        for (int j = i; j < wc - 1; j++) {
            constraints[j] = constraints[j + 1];
        }
        constraints[wc - 1] = null;
        return true;
    }

    public void setLayoutConstraints(Widget widget, LayoutConstraints constraints) {
        int i = getWidgetIndex(widget);
        if (i < 0) {
            throw new IllegalArgumentException("No such child");
        }
        ensureConstraintsFor(i);
        this.constraints[i] = constraints;
    }

    /**
     * Make sure our constraints array is big enough for an index of i
     * (i.e. has length of least i + 1).
     */
    private void ensureConstraintsFor(int i) {
        if (i >= constraints.length) {
            LayoutConstraints[] a = new LayoutConstraints[constraints.length + 4];
            for (int j = 0; j < constraints.length; j++) {
                a[j] = constraints[j];
            }
            constraints = a;
        }
    }

    public LayoutConstraints getLayoutConstraints(Widget widget) {
        int i = getWidgetIndex(widget);
        if (i < 0) {
            throw new IllegalArgumentException("No such child");
        }
        return i < constraints.length ? constraints[i] : null;        
    }

    public void clear() {
        if (getWidgetCount() > 0) {
            super.clear();
        }
    }

    /**
     * Redo our layout if our width or height have changed.
     */
    public void boundsUpdated() {
        if (isAttached() && (LDOM.getContentWidth(getElement()) != lastWidth
                || LDOM.getContentHeight(getElement()) != lastHeight)) {
            layout();
        }
    }

    public void layout() {
        if (!isAttached()) {
            return;
        }
        LayoutPerf.enter(this);
        try {
            Element e = getElement();
            lastWidth = LDOM.getContentWidth(e);
            lastHeight = LDOM.getContentHeight(e);
            ensureLayout();
            layout.layoutWidgets(this,
                LDOM.getContentLeft(e), LDOM.getContentTop(e),
                    lastWidth, lastHeight);
            fireLayoutUpdated();
        } finally {
            LayoutPerf.leave();
        }
    }

    /**
     * Set a RowLayout if we do not have a layout.
     */
    protected void ensureLayout() {
        if (layout == null) {
            setLayout(new RowLayout());
        }
    }

    protected void fireLayoutUpdated() {
        if (layoutListeners != null) {
            for (int i = layoutListeners.size() - 1; i >= 0; i--) {
                ((ContainerListener)layoutListeners.get(i)).layoutUpdated(this);
            }
        }
    }

    public void addContainerListener(ContainerListener l) {
        if (layoutListeners == null) {
            layoutListeners = new ArrayList();
        }
        layoutListeners.add(l);
    }

    public void removeContainerListener(ContainerListener l) {
        if (layoutListeners != null) {
            layoutListeners.remove(l);
            if (layoutListeners.isEmpty()) {
                layoutListeners = null;
            }
        }
    }

    public Widget getLogicalParent() {
        return getParent();
    }

    public void addClickListener(ClickListener listener) {
        if (clickListeners == null) {
            clickListeners = new ClickListenerCollection();
        }
        clickListeners.add(listener);
    }

    public void addMouseListener(MouseListener listener) {
        if (mouseListeners == null) {
            mouseListeners = new MouseListenerCollection();
        }
        mouseListeners.add(listener);
    }

    public void addMouseWheelListener(MouseWheelListener listener) {
        if (mouseWheelListeners == null) {
            mouseWheelListeners = new MouseWheelListenerCollection();
        }
        mouseWheelListeners.add(listener);
    }

    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
            case Event.ONCLICK:
                if (clickListeners != null) {
                    clickListeners.fireClick(this);
                }
                break;

            case Event.ONMOUSEDOWN:
            case Event.ONMOUSEUP:
            case Event.ONMOUSEMOVE:
            case Event.ONMOUSEOVER:
            case Event.ONMOUSEOUT:
                if (mouseListeners != null) {
                    mouseListeners.fireMouseEvent(this, event);
                }
                break;

            case Event.ONMOUSEWHEEL:
                if (mouseWheelListeners != null) {
                    mouseWheelListeners.fireMouseWheelEvent(this, event);
                }
                break;
        }
    }

    public void removeClickListener(ClickListener listener) {
        if (clickListeners != null) {
            clickListeners.remove(listener);
        }
    }

    public void removeMouseListener(MouseListener listener) {
        if (mouseListeners != null) {
            mouseListeners.remove(listener);
        }
    }

    public void removeMouseWheelListener(MouseWheelListener listener) {
        if (mouseWheelListeners != null) {
            mouseWheelListeners.remove(listener);
        }
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    public boolean isLimitMaximize() {
        return limitMaximize;
    }

    public void setLimitMaximize(boolean limitMaximize) {
        this.limitMaximize = limitMaximize;
    }

    @WidgetInfo(description = "Panel with pluggable layout strategy and constraints")
    public static class Factory extends ContainerFactory<LayoutPanel> {

        private boolean limitMaximize;

        public Factory() {
        }

        public Factory(LayoutPanel c) {
            super(c);
            limitMaximize = c.limitMaximize;
        }

        public void refresh(LayoutPanel p) {
            refreshSettings(p);
            super.refresh(p);
        }

        /**
         * Refresh only the configurable settings of p.
         */
        public void refreshSettings(LayoutPanel p) {
            p.limitMaximize = limitMaximize;
        }

        public LayoutPanel createWidget() {
            return new LayoutPanel();
        }
    }
}
