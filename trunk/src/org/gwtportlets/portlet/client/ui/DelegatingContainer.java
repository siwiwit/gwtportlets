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

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.shared.HandlerRegistration;
import org.gwtportlets.portlet.client.layout.Container;
import org.gwtportlets.portlet.client.layout.LayoutHandler;
import org.gwtportlets.portlet.client.layout.*;

import java.util.Iterator;

/**
 * Container that contains another inner container and delegates container
 * calls to that inner container. Useful for writing containers that decorate
 * other containers. The delegate must not change.
 */
public abstract class DelegatingContainer extends PositionAwareComposite
        implements Container {

    private HandlerRegistration handlerRegistration;

    protected abstract Container getDelegate();

    public void layout() {
        Widget w = getWidget();
        if (w instanceof Container) {
            ((Container)w).layout();
        }
        getDelegate().layout();
    }

    public Layout getLayout() {
        return getDelegate().getLayout();
    }

    public void setLayout(Layout layout) {
        getDelegate().setLayout(layout);
    }

    public void add(Widget child) {
        getDelegate().add(child);
    }

    public void add(Widget widget, LayoutConstraints constraints) {
        getDelegate().add(widget, constraints);
    }

    public void add(Widget widget, float constraints) {
        getDelegate().add(widget, constraints);
    }

    public void add(Widget widget, String constraints) {
        getDelegate().add(widget, constraints);
    }

    public void insert(Widget w, int beforeIndex,
            LayoutConstraints constraints) {
        getDelegate().insert(w, beforeIndex, constraints);
    }

    public boolean remove(Widget w) {
        return getDelegate().remove(w);
    }

    public void setLayoutConstraints(Widget widget,
            LayoutConstraints constraints) {
        getDelegate().setLayoutConstraints(widget, constraints);
    }

    public LayoutConstraints getLayoutConstraints(Widget widget) {
        return getDelegate().getLayoutConstraints(widget);
    }

    public Widget getLogicalParent() {
        return getParent();
    }

    public void clear() {
        getDelegate().clear();
    }

    public HandlerRegistration addLayoutHandler(LayoutHandler handler) {
        if (handlerRegistration == null) {
            handlerRegistration = getDelegate().addLayoutHandler(new LayoutHandler() {
                public void onLayoutUpdated(LayoutEvent event) {
                    LayoutEvent.fire(DelegatingContainer.this);
                }
            });            
        }
        return addHandler(handler, LayoutEvent.getType());
    }

    public boolean isLimitMaximize() {
        return getDelegate().isLimitMaximize();
    }

    public Widget getWidget(int index) {
        return getDelegate().getWidget(index);
    }

    public int getWidgetCount() {
        return getDelegate().getWidgetCount();
    }

    public int getWidgetIndex(Widget child) {
        return getDelegate().getWidgetIndex(child);
    }

    public Iterator iterator() {
        return getDelegate().iterator();
    }

    public boolean remove(int index) {
        return getDelegate().remove(index);
    }

}