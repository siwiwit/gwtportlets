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

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.WidgetInfo;
import org.gwtportlets.portlet.client.layout.*;

/**
 * Container that uses a pluggable layout strategy and constraints to arrange
 * its children.
 */
public class LayoutPanel extends ComplexPanel implements Container,
        HasClickHandlers, HasMouseDownHandlers, HasMouseUpHandlers,
        HasMouseOutHandlers, HasMouseOverHandlers, HasMouseMoveHandlers,
        HasMouseWheelHandlers {

    private Layout layout;
    private LayoutConstraints[] constraints = new LayoutConstraints[4];
    private int lastWidth = -1;
    private int lastHeight = -1;
    private boolean limitMaximize;

    public LayoutPanel() {
        setElement(DOM.createDiv());
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
            LayoutEvent.fire(this);
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

    public HandlerRegistration addLayoutHandler(LayoutHandler handler) {
        return addHandler(handler, LayoutEvent.getType());
    }

    public Widget getLogicalParent() {
        return getParent();
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
        return addDomHandler(handler, MouseDownEvent.getType());
    }

    public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
        return addDomHandler(handler, MouseUpEvent.getType());
    }

    public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return addDomHandler(handler, MouseOutEvent.getType());
    }

    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return addDomHandler(handler, MouseOverEvent.getType());
    }

    public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
        return addDomHandler(handler, MouseMoveEvent.getType());
    }

    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
        return addDomHandler(handler, MouseWheelEvent.getType());
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
