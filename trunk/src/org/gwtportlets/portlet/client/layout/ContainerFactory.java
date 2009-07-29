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

package org.gwtportlets.portlet.client.layout;

import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.AbstractWidgetFactory;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.WidgetFactoryProvider;
import org.gwtportlets.portlet.client.WidgetFactoryVisitor;

/**
 * Base class to holds the state of a {@link Container}.
 * Remembers the style, layout, children and constraints.
 */
public abstract class ContainerFactory<T extends Widget>
        extends AbstractWidgetFactory<T> {

    public WidgetFactory[] widgets;
    public LayoutConstraints[] constraints;
    public LayoutFactory layout;

    protected ContainerFactory() {
    }

    protected ContainerFactory(Container c) {
        super((Widget)c);
        saveLayout(c);
        saveChildren(c);
    }

    protected void saveLayout(Container c) {
        Layout layout = c.getLayout();
        if (layout != null) {
            this.layout = layout.createLayoutFactory();
        }
    }

    protected void saveChildren(Container c) {
        int wc = c.getWidgetCount();
        if (wc > 0) {
            widgets = new WidgetFactory[wc];
            constraints = new LayoutConstraints[wc];
            for (int i = 0; i < wc; i++) {
                Widget w = c.getWidget(i);
                widgets[i] = ((WidgetFactoryProvider)w).createWidgetFactory();
                LayoutConstraints con = c.getLayoutConstraints(w);
                constraints[i] = con == null ? null : con.copy();
            }
        }
    }

    public void refresh(T w) {
        super.refresh(w);
        Container c = (Container)w;
        c.clear();
        if (layout != null) {
            c.setLayout(layout.createLayout());
        }
        if (widgets != null) {
            for (int i = 0; i < widgets.length; i++) {
                WidgetFactory wf = widgets[i];
                if (wf != null) {
                    c.add(LayoutUtil.createWidget(wf),
                            constraints == null ? null : constraints[i]);
                }
            }
        }
    }

    public void accept(WidgetFactoryVisitor v) {
        if (v.visit(this) && widgets != null) {
            for (int i = 0; i < widgets.length; i++) {
                widgets[i].accept(v);
            }
        }
    }
}
