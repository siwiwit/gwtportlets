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

package org.gwtportlets.portlet.client.ui;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.util.GenUtil;
import org.gwtportlets.portlet.client.util.Rectangle;
import org.gwtportlets.portlet.client.layout.LDOM;

/**
 * <p>Responds to click and/or mouse move events by popping up a Widget in
 * a position relative to the source element. May have a parent popup that
 * is hidden when any popups created by us are hidden. Makes sure that only
 * one popup is active at a time. Useful for building cascading menus and so
 * on.</p>
 *
 * <p>Add as a click and/or mouse listener to other widget(s).</p>
 */
public abstract class CascadingPopupManager extends MouseListenerAdapter
        implements ClickListener, PopupListener {

    protected PopupPanel parent;
    protected PopupPanel active;
    protected Element lastMoveElement;

    protected CascadingPopupManager() {
    }

    /**
     * Parent will be hidden when our active popup is hidden. Our active
     * popup will be hidden when parent is hidden.
     */
    protected CascadingPopupManager(PopupPanel parent) {
        this.parent = parent;
        parent.addPopupListener(this);
    }

    protected Element getTargetLink() {
        Element e;
        try {
            e = DOM.eventGetTarget(DOM.eventGetCurrentEvent());
        } catch (JavaScriptException x) {
            return null;
            // sometimes getting the target throws an exception for some reason
        }
        return e != null && GenUtil.isLink(e) ? e : null;
    }

    public void onClick(Widget sender) {
        Event ev = DOM.eventGetCurrentEvent();
        String token = GenUtil.getTargetHistoryToken(ev);
        if (token != null) {
            DOM.eventPreventDefault(ev);
            if (token.endsWith("()")) {
                onMethodClick(sender, DOM.eventGetTarget(ev), 
                        token.substring(0, token.length() - 2));
            } else {
                onHistoryTokenClick(token);
            }
        }
    }

    protected void onHistoryTokenClick(String token) {
        History.newItem(token);
    }

    public void onMouseMove(Widget sender, int x, int y) {
        Element e = getTargetLink();
        if (e != null && lastMoveElement == null || !DOM.compare(e, lastMoveElement)) {
            lastMoveElement = e;
            Event ev = DOM.eventGetCurrentEvent();
            String token = GenUtil.getTargetHistoryToken(ev);
            if (token != null) {
                onMethodHover(sender, e, token.substring(0, token.length() - 2));
            } else {
                hidePopup();
            }
        }
    }

    /**
     * A history token ending with '()' has been clicked. The method param
     * is the token without the brackets. Override this to perform different
     * actions depending on the method parameter.
     */
    protected void onMethodClick(Widget sender, Element e, String method) {
    }

    /**
     * The mouse has moved over a link with a history token ending with
     * '()'. The method param is the token without the brackets. Override
     * this to perform different actions depending on the method parameter.
     */
    protected void onMethodHover(Widget sender, Element e, String method) {
    }

    /**
     * Display p relative to e. Any currently active sibling popup is hidden.
     */
    public void showPopup(final PopupPanel p, final Element e, 
            final boolean toRightOrLeft, final int spacing) {
        hidePopup();
        active = p;
        p.addPopupListener(this);
        p.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                setPopupPosition(e, p, offsetWidth, offsetHeight,
                        toRightOrLeft, spacing);
            }
        });
    }

    /**
     * Hide the active popup (if any).
     */
    public void hidePopup() {
        if (active != null) {
            active.removePopupListener(this);
            active.hide();
            active = null;
        }
    }

    /**
     * Hide the active popup and all others in the tree.
     */
    public void hideAllPopups() {
        if (parent != null) {
            parent.hide();
        } else if (active != null) {
            active.hide();
        }
    }

    /**
     * Set the position of p relative to e (the element that triggered the
     * popup).
     */
    protected void setPopupPosition(Element e, PopupPanel p, int offsetWidth,
            int offsetHeight, boolean toRightOrLeft, int spacing) {
        Rectangle nextTo = LDOM.getBounds(e);
        Rectangle r = LDOM.getNextToPosition(
                offsetWidth, offsetHeight, nextTo, toRightOrLeft, spacing);
        LDOM.setPosition(p.getElement(), r.x, r.y);
    }

    /**
     * Our parent or active popup has been closed.
     */
    public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
        lastMoveElement = null;
        if (parent != null) {
            PopupPanel p = parent;
            parent = null;
            p.hide();
        }
        if (active != null) {
            PopupPanel p = active;
            active = null;
            p.hide();
        }
    }

}