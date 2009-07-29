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

package org.gwtportlets.portlet.client.impl;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Implementation for IE.
 */
public class LDOMImplIE6 extends LDOMImpl {

    public int getWidth(Element e) {
        String s = DOM.getStyleAttribute(e, "width");
        if (s != null && s.endsWith("px")) {
            try {
                return Integer.parseInt(s.substring(0, s.length() - 2));
            } catch (NumberFormatException x) {
                // ignore
            }
        }
        return super.getWidth(e);
    }

    public int getHeight(Element e) {
        String s = DOM.getStyleAttribute(e, "height");
        if (s != null && s.endsWith("px")) {
            try {
                return Integer.parseInt(s.substring(0, s.length() - 2));
            } catch (NumberFormatException x) {
                // ignore
            }
        }
        return super.getHeight(e);
    }

    public void setBounds(Element e, int left, int top,
            int width, int height) {
        setPosition(e, left, top);
        setSize(e, width, height);
    }

    public native String getComputedStyle(Element e, String property) /*-{
        var v = e.currentStyle ? e.currentStyle[property] : null;
        return e.style[property] || v || null;
    }-*/;

    public Widget createInputBlocker() {
        // use an IFRAME on IE to stop controls from showing through
        Frame f = new Frame();
        fixFrame(f.getElement());
        return f;
    }

    private native void fixFrame(Element f) /*-{
        f.src = "javascript:''";
        f.scrolling = 'no';
        f.frameBorder = 0;
    }-*/;

    public PopupPanel createOverlayPopup() {
        return new PopupPanel(false, false);
    }

}
