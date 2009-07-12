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

package org.gwtportlets.portlet.client.impl;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Browser specific implementation of
 * {@link org.gwtportlets.portlet.client.layout.LDOM}.
 */
public class LDOMImpl {

    public int getContentLeft(Element e) {
        return getPaddingLeft(e);
    }

    public int getContentTop(Element e) {
        return getPaddingTop(e);
    }

    public int getContentWidth(Element e) {
        return getWidth(e)
                - getBorderLeftWidth(e) - getBorderRightWidth(e)
                - getPaddingLeft(e) - getPaddingRight(e);
    }

    public int getContentHeight(Element e) {
        return getHeight(e)
                - getBorderTopWidth(e) - getBorderBottomWidth(e)
                - getPaddingTop(e) - getPaddingBottom(e);
    }

    public int getWidth(Element e) {
        return DOM.getElementPropertyInt(e, "offsetWidth");
    }

    public int getHeight(Element e) {
        return DOM.getElementPropertyInt(e, "offsetHeight");
    }

    public void setBounds(Element e, int left, int top, int width, int height) {
        width -= getBorderLeftWidth(e) + getBorderRightWidth(e)
                + getPaddingLeft(e) + getPaddingRight(e);
        height -= getBorderTopWidth(e) + getBorderBottomWidth(e)
                + getPaddingTop(e) + getPaddingBottom(e);
        setPosition(e, left, top);
        setSize(e, width, height);
    }

    public void setSize(Element e, int width, int height) {
        DOM.setStyleAttribute(e, "width", width < 0 ? "0px" : width + "px");
        DOM.setStyleAttribute(e, "height", height < 0 ? "0px" : height + "px");
    }

    public void setPosition(Element e, int left, int top) {
        DOM.setStyleAttribute(e, "position", "absolute");
        DOM.setStyleAttribute(e, "left", left + "px");
        DOM.setStyleAttribute(e, "top", top + "px");
    }

    public void setClientPosition(Element e, int clientX, int clientY) {
        Element p = DOM.getParent(e);
        setPosition(e, 
                clientX - DOM.getAbsoluteLeft(p) - getPaddingLeft(p) - getBorderLeftWidth(p),
                clientY - DOM.getAbsoluteTop(p) - getPaddingTop(p) - getBorderTopWidth(p));
    }

    public int getLeft(Element e) {
        return getIntComputedStyle(e, "left");
    }

    public int getTop(Element e) {
        return getIntComputedStyle(e, "top");
    }

    public int getBorderLeftWidth(Element e) {
        return getIntComputedStyle(e, "borderLeftWidth");
    }

    public int getBorderRightWidth(Element e) {
        return getIntComputedStyle(e, "borderRightWidth");
    }

    public int getBorderTopWidth(Element e) {
        return getIntComputedStyle(e, "borderTopWidth");
    }

    public int getBorderBottomWidth(Element e) {
        return getIntComputedStyle(e, "borderBottomWidth");
    }

    public int getPaddingLeft(Element e) {
        return getIntComputedStyle(e, "paddingLeft");
    }

    public int getPaddingRight(Element e) {
        return getIntComputedStyle(e, "paddingRight");
    }

    public int getPaddingTop(Element e) {
        return getIntComputedStyle(e, "paddingTop");
    }

    public int getPaddingBottom(Element e) {
        return getIntComputedStyle(e, "paddingBottom");
    }

    public void setBackgroundPosition(Element e, String value) {
        DOM.setStyleAttribute(e, "backgroundPosition", value);
    }

    public int getIntComputedStyle(Element e, String s) {
        String v = getComputedStyle(e, s);
        if (v == null || v.length() == 0) {
            return 0;
        }
        if (v.endsWith("px")) {
            v = v.substring(0, v.length() - 2);
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException x) {
            return 0;
        }
    }

    public native String getComputedStyle(Element e, String property) /*-{
        var v = null;
        var comp = $wnd.document.defaultView.getComputedStyle(e, '');
        if (comp) {
            v = comp[property];
        }
        return e.style[property] || v || null;
    }-*/;

    public Widget createInputBlocker() {
        return new HTML("");
    }

    public PopupPanel createOverlayPopup() {
        return null;
    }

    public int getDialogWidthFudgePx() {
        return 33;
    }

    public boolean isScrollbarWorkaroundRequired() {
        return false;
    }

    public Element getContainingBlock(Element e) {
        for (Element p = DOM.getParent(e); p != null; p = DOM.getParent(p)) {
            String s = DOM.getStyleAttribute(p, "position");
            if (s != null && ("absolute".equals(s) || "relative".equals(s)
                    || "fixed".equals(s))) {
                return p;
            }
        }
        return getRootElement();
    }

    private static native Element getRootElement() /*-{
      return $doc;
    }-*/;

}