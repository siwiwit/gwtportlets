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

package main.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.layout.LDOM;
import org.gwtportlets.portlet.client.ui.Portlet;
import org.gwtportlets.portlet.client.ui.PortletFactory;

/**
 * Displays a google gadget.
 */
public class GoogleGadgetPortlet extends Portlet {

    private String url;
    private int lastWidth = -1;
    private int lastHeight = -1;

    private HTML html = new HTML();
    
    private static final String SCRIPT_SRC = "<script src=\"";
    private static final String SCRIPT_SRC_END = "\"></script>";

    public GoogleGadgetPortlet() {
        initWidget(html);
        setStyleName("");
    }

    public void setUrl(String url) {
        if (url == null) {
            url = "";
        }
        // remove <script> wrapper
        if (url.startsWith(SCRIPT_SRC)) {
            url = url.substring(SCRIPT_SRC.length());
        }
        if (url.endsWith(SCRIPT_SRC_END)) {
            url = url.substring(0, url.length() - SCRIPT_SRC_END.length());
        }
        // change output type to html
        url = url.replaceAll("&amp;output=js", "&amp;output=html");
        // remove width and height
        url = url.replaceAll("&amp;w=\\d+&amp;", "&amp;");
        url = url.replaceAll("&amp;h=\\d+&amp;", "&amp;");
        this.url = url;
        lastWidth = -1;
        update();
    }

    public void boundsUpdated() {
        if (isAttached() && (LDOM.getContentWidth(getElement()) != lastWidth
                || LDOM.getContentHeight(getElement()) != lastHeight)) {
            update();
        }
    }

    private void update() {
        if (!isAttached()) {
            return;
        }
        Element e = getElement();
        lastWidth = LDOM.getContentWidth(e);
        lastHeight = LDOM.getContentHeight(e);
        if (url == null || url.length() == 0) {
            html.setHTML("No gadget");
        } else {
            StringBuffer s = new StringBuffer();
            s.append("<iframe src=\"\" height = \"")
                    .append(lastHeight).append("\" width = \"").append(lastWidth)
                    .append("\" frameborder = \"0\" scrolling = \"auto\"></iframe>");
            html.setHTML(s.toString());
            Element iframe = DOM.getFirstChild(html.getElement());
            DOM.setElementAttribute(iframe, "src", url);
        }
    }

    public boolean isConfigureSupported() {
        return true;
    }

    public void configure() {
        String s = Window.prompt("Enter Google Gadget code or URL", "");
        if (s != null) {
            if (s.startsWith(SCRIPT_SRC)) {
                s = s.substring(SCRIPT_SRC.length());
            }
            if (s.endsWith(SCRIPT_SRC_END)) {
                s = s.substring(0, s.length() - SCRIPT_SRC_END.length());
            }
            setUrl(s);
        }
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    public static class Factory extends PortletFactory<GoogleGadgetPortlet> {

        public String url;

        public Factory() {
        }

        public Factory(GoogleGadgetPortlet p) {
            super(p);
            url = p.url;
        }

        public void refresh(GoogleGadgetPortlet p) {
            super.refresh(p);
            p.setUrl(url);
        }

        public GoogleGadgetPortlet createWidget() {
            return new GoogleGadgetPortlet();
        }
    }

}

