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

package org.gwtportlets.portlet.server;

import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.WidgetFactoryVisitor;

import java.util.Map;
import java.util.HashMap;

/**
 * Converts a history token (i.e. page) into a WidgetFactory tree to be
 * sent to the client. Looks up {@link WidgetDataProvider}'s to load
 * additional data into each WidgetFactory in the tree.
 */
public abstract class PageProvider {

    private Map<Class, WidgetDataProvider> providerMap =
            new HashMap<Class, WidgetDataProvider>();

    public PageProvider() {
        add(new WebAppContentDataProvider());
        add(new MenuDataProvider());
    }

    /**
     * Add a data provider to our map.
     */
    public void add(WidgetDataProvider p) {
        Class<? extends WidgetFactory> key = p.getWidgetFactoryClass();
        if (key == null) {
            throw new IllegalArgumentException("null not supported");
        }
        providerMap.put(key, p);
    }

    /**
     * Create a WidgetFactory tree for the page or return null if not found.
     */
    public WidgetFactory openPage(final PageRequest req) {
        WidgetFactory page = createWidgetFactory(req);
        if (page != null) {
            req.setOpenPage(true);
            refresh(req, page);
        }
        return page;
    }

    /**
     * Create a tree of widget factories for the page in req or return null
     * if not found.
     */
    protected abstract WidgetFactory createWidgetFactory(PageRequest req);

    /**
     * Refresh the data in the widget factory tree starting at top.
     */
    public void refresh(final PageRequest req, final WidgetFactory top) {
        top.accept(new WidgetFactoryVisitor() {
            public boolean visit(WidgetFactory wf) {
                WidgetDataProvider p = findWidgetDataProvider(wf);
                if (p != null) {
                    try {
                        p.refresh(wf, req);
                    } catch (Exception e) {
                        handleRefreshException(req, top, wf, e);
                    }
                }
                return true;
            }
        });
    }

    /**
     * Locate a data provider for wf or return null if none.
     */
    public WidgetDataProvider findWidgetDataProvider(WidgetFactory wf) {
        return providerMap.get(wf.getClass());
    }

    /**
     * An exception has occured during a refresh call.
     *
     * @param top The top factory in the tree being refreshed
     * @param wf The factory that through the exception
     */
    protected abstract void handleRefreshException(PageRequest req,
            WidgetFactory top, WidgetFactory wf, Exception e);

}