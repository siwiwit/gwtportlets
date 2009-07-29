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

package org.gwtportlets.portlet.client.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.DoNotSendToServer;
import org.gwtportlets.portlet.client.WidgetFactory;

/**
 * Displays any content served from our web application.
 */
public class WebAppContentPortlet extends Portlet {

    private String path;
    private String html;

    public WebAppContentPortlet() {
        initWidget((Widget)createWidget());
    }

    /**
     * Create the widget that will hold the HTML.
     */
    protected HasHTML createWidget() {
        return new HTMLEx();
    }

    private void restore(Factory f) {
        this.path = f.path;
        this.html = f.html;
        setTemplate(html);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    protected void setTemplate(String html) {
        ((HasHTML)getWidget()).setHTML(
                html == null ? "<i>Web App Content</i>" : html);
    }

    public boolean isConfigureSupported() {
        return true;
    }

    public void configure() {
        String s = Window.prompt("Enter path to web content to display:",
                path == null ? "" : path);
        if (s != null) {
            path = s;
            refresh();
        }
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    public static class Factory extends PortletFactory<WebAppContentPortlet> {

        public String path;
        @DoNotSendToServer
        public String html;

        public Factory() {
        }

        public Factory(WebAppContentPortlet p) {
            super(p);
            path = p.path;
            html = p.html;
        }

        public void refresh(WebAppContentPortlet w) {
            super.refresh(w);
            w.restore(this);
        }

        public WebAppContentPortlet createWidget() {
            return new WebAppContentPortlet();
        }
    }
}