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

package main.client.ui;

import com.google.gwt.user.client.ui.Label;
import org.gwtportlets.portlet.client.DoNotSendToServer;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.ui.Portlet;
import org.gwtportlets.portlet.client.ui.PortletFactory;

/**
 * Hello world with time on server.
 */
public class HelloWorldPortlet extends Portlet {

    private String serverTime;

    private Label label = new Label();

    public HelloWorldPortlet() {
        initWidget(label);
    }

    private void restore(Factory f) {
        serverTime = f.serverTime;
        label.setText("Hello World, the time on the server is " + serverTime);
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    public static class Factory extends PortletFactory<HelloWorldPortlet> {

        @DoNotSendToServer
        public String serverTime;

        public Factory() {
        }

        public Factory(HelloWorldPortlet p) {
            super(p);
            serverTime = p.serverTime;
        }

        public void refresh(HelloWorldPortlet p) {
            super.refresh(p);
            p.restore(this);
        }

        public HelloWorldPortlet createWidget() {
            return new HelloWorldPortlet();
        }
    }

}
