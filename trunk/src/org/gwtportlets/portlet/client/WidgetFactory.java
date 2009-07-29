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

package org.gwtportlets.portlet.client;

import com.google.gwt.user.client.ui.Widget;

import java.io.Serializable;

/**
 * A factory to create a widget and restore its state.
 */
public interface WidgetFactory<T extends Widget> extends Serializable {

    /**
     * Create the widget instance. This should not do any configuration that
     * requires information from our fields. This should be done by
     * {@link #refresh(com.google.gwt.user.client.ui.Widget)}.
     */
    public T createWidget();

    /**
     * Restore w to the state stored in this object.
     */
    public void refresh(T w);

    /**
     * Visit this factory and its children (if any).
     */
    public void accept(WidgetFactoryVisitor v);

}
