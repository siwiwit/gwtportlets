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

package org.gwtportlets.portlet.client.edit.row;

import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.edit.LayoutEditor;
import org.gwtportlets.portlet.client.edit.PageEditor;
import org.gwtportlets.portlet.client.layout.Container;
import org.gwtportlets.portlet.client.layout.LDOM;
import org.gwtportlets.portlet.client.layout.LayoutConstraints;
import org.gwtportlets.portlet.client.layout.RowLayout;
import org.gwtportlets.portlet.client.util.Rectangle;

/**
 * Drag and drop editing of a container using RowLayout.
 */
public class RowLayoutEditor implements LayoutEditor {

    private PageEditor manager;
    private Container container;
    private HandlerRegistration handlerRegistration;

    public RowLayoutEditor() {
    }

    public void init(PageEditor manager, Container container) {
        this.manager = manager;
        this.container = container;
        handlerRegistration = container.addLayoutHandler(manager);
    }

    public void dispose() {
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
            handlerRegistration = null;
        }
    }

    public PageEditor getManager() {
        return manager;
    }

    public Container getContainer() {
        return container;
    }

    public RowLayout getTargetLayout() {
        return (RowLayout)container.getLayout();
    }

    public void createMenuItems(final Widget widget, MenuBar bar) {
    }

    public Rectangle getDropArea(Widget widget, LayoutConstraints constraints,
            int clientX, int clientY) {
        int wc = container.getWidgetCount();
        if (wc == 0) {
            return LDOM.getContentBounds(container.getElement());
        }
        int i = getDropIndex(clientX, clientY);
        if (i >= wc) {
            Rectangle r = LDOM.getContentBounds(container.getElement());
            Rectangle r2 = LDOM.getBounds(container.getWidget(wc - 1));
            int spacing = getTargetLayout().getSpacing();
            if (getTargetLayout().isColumn()) {
                int y = r2.y + spacing + r2.height;
                int h = r.y + r.height - y;
                if (y <= 16) {
                    r.y += r.height - 16;
                    r.height = 16;
                } else {
                    r.y = y;
                    r.height = h;
                }
            } else {
                int x = r2.x + spacing + r2.width;
                int w = r.x + r.width - x;
                if (w <= 16) {
                    r.x += r.width - 16;
                    r.width = 16;
                } else {
                    r.x = x;
                    r.width = w;
                }
            }
            return r;
        } else {
            return LDOM.getBounds(container.getWidget(i));
        }
    }

    public boolean dropWidget(Widget widget, LayoutConstraints constraints,
            String overflow, int clientX, int clientY) {
        int i = getDropIndex(clientX, clientY);
        if (widget.getParent() == container) {
            int ci = container.getWidgetIndex(widget);
            int wc = container.getWidgetCount();
            if (i == ci || ci == wc - 1 && i == wc) {
                return false;
            }
            if (i > ci + 1) {
                --i;
            }
        }
        if (overflow == null) {

        }
        widget.removeFromParent();
        container.insert(widget, i, constraints instanceof RowLayout.Constraints
                ? constraints
                : new RowLayout.Constraints(0.0f, 1.0f, overflow));
        return true;
    }

    /**
     * Figure out the best index for widget in our container assuming it is
     * dropped at clientX and clientY.
     */
    private int getDropIndex(int clientX, int clientY) {
        int wc = container.getWidgetCount();
        if (getTargetLayout().isColumn()) {
            for (int i = wc - 1; i >= 0; i--) {
                Widget w = container.getWidget(i);
                int top = w.getAbsoluteTop();
                if (clientY >= top) {
                    return clientY >= top + w.getOffsetHeight() * 2 / 3 ? i + 1 : i;
                }
            }
        } else {
            for (int i = wc - 1; i >= 0; i--) {
                Widget w = container.getWidget(i);
                int left = w.getAbsoluteLeft();
                if (clientX >= left) {
                    return clientX >= left + w.getOffsetWidth() * 2 / 3 ? i + 1 : i;
                }
            }
        }
        return 0;
    }

    public boolean isEditLayout() {
        return true;
    }

    public void editLayout(CloseHandler closeHandler) {
        RowContainerDialog dlg = new RowContainerDialog(container);
        dlg.addCloseHandler(closeHandler);
        dlg.display();
    }

    public boolean isEditConstraints(Widget widget) {
        return true;
    }

    public void editConstraints(Widget widget,
            ValueChangeHandler<Void> changeHandler,
            CloseHandler closeHandler) {
        RowConstraintsDialog dlg = new RowConstraintsDialog(container, widget);
        if (changeHandler != null) {
            dlg.addValueChangeHandler(changeHandler);
        }
        dlg.addCloseHandler(closeHandler);
        dlg.display();
    }

    public boolean isDragBottomEdge(Widget target) {
        return getTargetLayout().isColumn();
    }

    public Widget getContainerInfo(Widget info) {
        HTML html;
        if (info == null) {
            html = new HTML();
            html.setStyleName("portlet-ed-info");
        } else {
            html = (HTML)info;
        }

        StringBuffer s = new StringBuffer();
        boolean column = getTargetLayout().isColumn();
        String spacer = column ? "<br>" : " ";

        if (container.getWidgetCount() == 0) {
            s.append("Empty");
            s.append(spacer);
        }
        s.append(container.getDescription());
        
        html.setHTML(s.toString());
        return html;
    }

    public Widget getConstraintInfo(Widget target, Widget info) {
        HTML html;
        if (info == null) {
            html = new HTML();
            html.setStyleName("portlet-ed-info");
        } else {
            html = (HTML)info;
        }

        RowLayout.Constraints c =
                (RowLayout.Constraints)container.getLayoutConstraints(target);
        StringBuffer s = new StringBuffer();
        String spacer = getTargetLayout().isColumn() ? " " : "<br>";
        if (target instanceof Container) {
            s.append(((Container)target).getDescription());
        }
        float size = c.getSize();
        if (size < 1.0f) {
            if (size > 0.0f) {
                if (s.length() > 0) {
                    s.append(spacer);
                }
                s.append((int)(size * 100));
                s.append(" %");
            }
        } else {
            if (s.length() > 0) {
                s.append(spacer);
            }
            s.append((int)size);
        }
        if (c.getMaxSize() > 0) {
            if (s.length() > 0) {
                s.append(spacer);
            }
            s.append("Max");
            s.append(spacer);
            s.append(c.getMaxSize());
        }
        if (c.getWeight() > 0.0f) {
            if (s.length() > 0) {
                s.append(spacer);
            }
            s.append("Grow");
            String ws = Float.toString(c.getWeight());
            if (!"1".equals(ws)) {
                s.append(spacer);
                s.append(ws);
            }
        }
        html.setHTML(s.toString());
        return html;
    }

    public void resizeWidget(Widget target, int delta) {
        if (delta == 0) {
            return;
        }

        Element ce = container.getElement();
        int containerSize = getTargetLayout().isColumn()
                ? LDOM.getContentHeight(ce)
                : LDOM.getContentWidth(ce);

        RowLayout.Constraints c =
                (RowLayout.Constraints)container.getLayoutConstraints(target);
        if (c.getMaxSize() > 0 && c.getActualSize() == c.getMaxSize()) {
            // change the max size if the widget is at max
            int sz = c.getMaxSize() + delta;
            if (sz < 10) {
                sz = 10;
            }
            c.setMaxSize(sz);
        } else {
            int sz = c.getActualSize() - c.getExtraSize() + delta;
            if (sz <= 0) {
                sz = 1;
            }
            if (c.getSize() < 1.0f) { // set new proportional size
                float f = (float)sz / containerSize;
                c.setSize(f > 0.999f ? 0.999f : f);
            } else { // set new pixel size
                c.setSize(sz);
            }
        }
        
        container.layout();
    }
}
