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

import org.gwtportlets.portlet.client.layout.LayoutUtil;

/**
 * Information about a WidgetFactory.
 */
public class WidgetFactoryMetaData {

    private final int index;
    private final String name;
    private final String category;
    private final String description;
    private final boolean container;
    private final Class cls;

    public WidgetFactoryMetaData(int index, String name, String category,
            String description, boolean container, Class cls) {
        this.index = index;
        this.name = name;
        this.category = category;
        this.description = description;
        this.container = container;
        this.cls = cls;
    }

    /**
     * Create an instance of the WidgetFactory we describe.
     */
    public WidgetFactory createWidgetFactory() {
        return LayoutUtil.createWidgetFactory(index);
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Does the factory extend
     * {@link org.gwtportlets.portlet.client.layout.ContainerFactory}?
     */
    public boolean isContainer() {
        return container;
    }

    public Class getCls() {
        return cls;
    }

    public String toString() {
        return name + "[" + index + "]";
    }
    
}
