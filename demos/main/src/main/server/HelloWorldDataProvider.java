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

package main.server;

import main.client.ui.HelloWorldPortlet;
import org.gwtportlets.portlet.server.PageRequest;
import org.gwtportlets.portlet.server.WidgetDataProvider;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hello world with time on server.
 */
public class HelloWorldDataProvider
        implements WidgetDataProvider<HelloWorldPortlet.Factory> {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z");

    public Class getWidgetFactoryClass() {
        return HelloWorldPortlet.Factory.class;
    }

    public void refresh(HelloWorldPortlet.Factory f, PageRequest req) {
        f.serverTime = DATE_FORMAT.format(new Date());
    }
}
