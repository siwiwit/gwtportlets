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

package org.gwtportlets.portlet.client.layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.impl.LDOMImpl;
import org.gwtportlets.portlet.client.util.Rectangle;

/**
 * Static methods to query and position widgets and elements.
 */
public class LDOM {

    private static final LDOMImpl impl;

    static {
        impl = (LDOMImpl)GWT.create(LDOMImpl.class);
    }

    /**
     * Get the left edge of the content area for e relative to its parent.
     */
    public static int getContentLeft(Element e) {
        return impl.getContentLeft(e);
    }

    /**
     * Get the top edge of the content area for e relative to its parent.
     */
    public static int getContentTop(Element e) {
        return impl.getContentTop(e);
    }

    /**
     * Get the containing block for e. Coordinates of absolutely positioned
     * elements that are descendents of the containing block and that are not
     * in other containing blocks are relative to it (i.e. top=0, left=0 is the
     * top left corner of the containing block). Always returns non-null
     * as the document root element is the topmost containing block.
     */
    public static Element getContainingBlock(Element e) {
        return impl.getContainingBlock(e);
    }

    /**
     * <p>Return a rectangle positioned at x, y in the coordinate space of target
     * i.e. x=0, y=0 is the top left of the content area of target. The
     * containingBlock parameter must be the containing block of target.
     * The position of the rectangle is relative to containingBlock i.e.
     * the coordinate space for absolute positioning. The width and height
     * of the rectangle are those of the content area of target.</p>
     *
     * <p>Example code to overlay one element on top of another:</p
     *
     * <pre>
     * Element imageMap = ...,
     * Element marker = ...;
     * Rectangle b = LDOM.getPositionRelativeTo(
     *     LDOM.getContainingBlock(imageMap), imageMap, x, y);
     * LDOM.setPosition(marker, b.x, b.y);
     * DOM.appendChild(DOM.getParent(imageMap), marker);
     * </pre>
     *
     * @see #getContainingBlock(com.google.gwt.user.client.Element) 
     */
    public static Rectangle getPositionRelativeTo(Element containingBlock,
            Element target, int x, int y) {
        Rectangle r = LDOM.getContentBounds(target); // relative to the document
        r.x = x + r.x - DOM.getAbsoluteLeft(containingBlock)
                + DOM.getElementPropertyInt(containingBlock, "scrollLeft");
        r.y = y + r.y - DOM.getAbsoluteTop(containingBlock)
                + DOM.getElementPropertyInt(containingBlock, "scrollTop");
        return r;
    }

    /**
     * Position the widget absolutely (i.e. coordinates are relative to its
     * containing block). If it implements {@link PositionAware} then it is
     * notified of this change. The width and height are adjusted to account
     * for the borders and padding of the widget if needed. Note that its
     * margin is not considered.
     */
    public static void setBounds(Widget w, int left, int top,
            int width, int height) {
        setBounds(w.getElement(), left, top, width, height);
        if (w instanceof PositionAware) {
            ((PositionAware)w).boundsUpdated();
        }
    }

    /**
     * Position the widget absolutely (i.e. coordinates are relative to its
     * containing block). If it implements {@link PositionAware} then it is
     * notified of this change. The width and height are adjusted to account
     * for the borders and padding of the widget if needed. Note that its
     * margin is not considered.
     */
    public static void setBounds(Widget w, Rectangle r) {
        setBounds(w, r.x, r.y, r.width, r.height);            
    }

    /**
     * Position the element absolutely. The width and height are adjusted to
     * account for the borders and padding of the element if needed. Note that
     * its margin is not considered.
     *
     */
    public static void setBounds(Element e, int left, int top,
            int width, int height) {
        impl.setBounds(e, left, top, width, height);
    }

    /**
     * Get the width of e less space used by borders and padding.
     */
    public static int getContentWidth(Element e) {
        return impl.getContentWidth(e);
    }

    /**
     * Get the height of e less space used by borders and padding.
     */
    public static int getContentHeight(Element e) {
        return impl.getContentHeight(e);
    }

    /**
     * Get the pixel width of e.
     */
    public static int getWidth(Element e) {
        return impl.getWidth(e);
    }

    /**
     * Get the pixel height of e.
     */
    public static int getHeight(Element e) {
        return impl.getHeight(e);
    }

    public static void setSize(Element e, int width, int height) {
        impl.setSize(e, width, height);
    }

    /**
     * Position e abolutely within its parent (i.e. position set to 'absolute').
     */
    public static void setPosition(Element e, int left, int top) {
        impl.setPosition(e, left, top);
    }

    /**
     * Set the position of e relative to the browser client area.
     */
    public static void setClientPosition(Element e, int clientX, int clientY) {
        impl.setClientPosition(e, clientX, clientY);
    }

    public static int getLeft(Element e) {
        return impl.getLeft(e);
    }

    public static int getTop(Element e) {
        return impl.getTop(e);
    }

    public static int getBorderLeftWidth(Element e) {
        return impl.getBorderLeftWidth(e);
    }

    public static int getBorderRightWidth(Element e) {
        return impl.getBorderRightWidth(e);
    }

    public static int getBorderTopWidth(Element e) {
        return impl.getBorderTopWidth(e);
    }

    public static int getBorderBottomWidth(Element e) {
        return impl.getBorderBottomWidth(e);
    }

    public static int getPaddingLeft(Element e) {
        return impl.getPaddingLeft(e);
    }

    public static int getPaddingRight(Element e) {
        return impl.getPaddingRight(e);
    }

    public static int getPaddingTop(Element e) {
        return impl.getPaddingTop(e);
    }

    public static int getPaddingBottom(Element e) {
        return impl.getPaddingBottom(e);
    }

    public static int getIntComputedStyle(Element e, String s) {
        return impl.getIntComputedStyle(e, s);
    }

    public static String getComputedStyle(Element e, String property) {
        return impl.getComputedStyle(e, property);
    }

    /**
     * Does e contain the browser client area coordinates given?
     */
    public static boolean contains(Element e, int clientX, int clientY) {
        int left = DOM.getAbsoluteLeft(e);
        int top = DOM.getAbsoluteTop(e);
        int width = getWidth(e);
        int height = getHeight(e);
        return clientX >= left && clientX < left + width
                && clientY >= top && clientY < top + height;
    }

    /**
     * Get a bounding rectangle for w in browser client area coordinates.
     */
    public static Rectangle getBounds(Widget w) {
        return getBounds(w.getElement());
    }

    /**
     * Get a bounding rectangle for e in browser client area coordinates.
     */
    public static Rectangle getBounds(Element e) {
        Rectangle r = new Rectangle();
        r.x = DOM.getAbsoluteLeft(e);
        r.y = DOM.getAbsoluteTop(e);
        r.width = getWidth(e);
        r.height = getHeight(e);
        return r;
    }
    
    /**
     * Get a bounding rectangle for the content area of w in browser client
     * area coordinates. This area excludes space used by borders and padding.
     */
    public static Rectangle getContentBounds(Widget w) {
        return getContentBounds(w.getElement());
    }

    /**
     * Get a bounding rectangle for the content area of e in browser client
     * area coordinates. This area excludes space used by borders and padding.
     */
    public static Rectangle getContentBounds(Element e) {
        Rectangle r = new Rectangle();
        r.x = DOM.getAbsoluteLeft(e) + getBorderLeftWidth(e) + getContentLeft(e);
        r.y = DOM.getAbsoluteTop(e) + getBorderTopWidth(e) + getContentTop(e);
        r.width = getContentWidth(e);
        r.height = getContentHeight(e);
        return r;
    }

    /**
     * Calculate a position for w so that it is next to the 'nextTo' element.
     * Both w and nextTo must be attached i.e. offsetWith and offsetHeight
     * are available. This method will not return a rectangle that is off
     * the screen.
     *
     * @param spacing Pixels to keep between e and nextTo
     */
    public static Rectangle getNextToPosition(Widget w, Element nextTo,
            int spacing) {
        Rectangle r = LDOM.getBounds(nextTo);
        // place to left or right if there is more space there
        int vert = Math.max(r.y, Window.getClientHeight() - r.y - r.height);
        int horiz = Math.max(r.x, Window.getClientWidth() - r.x - r.width);
        return getNextToPosition(w.getOffsetWidth(), w.getOffsetHeight(),
                r, horiz > vert, spacing);
    }

    /**
     * Calculate a position for w so that it is next to the 'nextTo' element.
     * Both w and nextTo must be attached i.e. offsetWith and offsetHeight
     * are available. This method will not return a rectangle that is off
     * the screen.
     *
     * @param toRightOrLeft Position to right or left (true) or below or above (false)
     * @param spacing Pixels to keep between e and nextTo
     */
    public static Rectangle getNextToPosition(Widget w, Element nextTo,
            boolean toRightOrLeft, int spacing) {
        return getNextToPosition(w.getOffsetWidth(), w.getOffsetHeight(),
                LDOM.getBounds(nextTo), toRightOrLeft, spacing);
    }

    /**
     * Calculate a position for something of size width x height so that it is
     * next to the 'nextTo' rectangle. This method will not return a rectangle
     * that is off the screen.
     *
     * @param toRightOrLeft Position to right or left (true) or below or above (false)
     * @param spacing Pixels to keep between e and nextTo
     */
    public static Rectangle getNextToPosition(int width, int height,
            Rectangle nextTo, boolean toRightOrLeft, int spacing) {
        int ch = Window.getClientHeight();
        int cw = Window.getClientWidth();
        int x, y;
        if (toRightOrLeft) {   // display on right or left
            x = nextTo.x + nextTo.width + spacing;
            if (x + width >= cw) {  // not enough space on right
                x = nextTo.x - (width + spacing);
                if (x < 0) {    // not enough space on left either so center it
                    x = (cw - width) / 2;
                }
            }
            if (nextTo.y + height > ch) {
                y = ch - height;
            } else {
                y = nextTo.y;
            }
        } else {    // display below or above
            y = nextTo.y + nextTo.height + spacing;
            if (y + height >= ch) { // not enough space below
                y = nextTo.y - (height + spacing);
                if (y < 0) { // not enough space above either so center it
                    y = (ch - height) / 2;
                }
            }
            if (nextTo.x + width > cw) {
                x = cw - width;
            } else {
                x = nextTo.x;
            }
        }
        Rectangle ans = new Rectangle();
        ans.x = x;
        ans.y = y;
        ans.width = width;
        ans.height = height;
        return ans;
    }

    /**
     * Set the background-position style attribute on e.
     */
    public static void setBackgroundPosition(Element e, String value) {
        impl.setBackgroundPosition(e, value);
    }

    /**
     * Create a widget that can be placed on top of another widget to
     * prevent it from receiving input.
     */
    public static Widget createInputBlocker() {
        return impl.createInputBlocker();
    }

    /**
     * Create the popup placed behide overlay panels to prevent controls
     * from showing through on IE. Returns null if decent browser.
     */
    public static PopupPanel createOverlayPopup() {
        return impl.createOverlayPopup();
    }

    /**
     * Get the fudge px added to the "automatic" width of a dialog to get it
     * to come up without scrollbars.
     */
    public static int getDialogWidthFudgePx() {
        return impl.getDialogWidthFudgePx();
    }

    /**
     * Returns true for browsers that have problems with double scrollbars
     * when the browser client area shrinks (e.g. Safari).
     */
    public static boolean isScrollbarWorkaroundRequired() {
        return impl.isScrollbarWorkaroundRequired();
    }

}
