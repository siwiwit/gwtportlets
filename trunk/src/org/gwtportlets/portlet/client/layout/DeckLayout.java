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

package org.gwtportlets.portlet.client.layout;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * Arranges widgets on top of each other with each widget filling the available
 * space (or centered horizontally and/or vertically). The constraints define
 * which widget(s) are visible.
 */
public class DeckLayout implements Layout {

    public DeckLayout() {
    }

    public String getDescription() {
        return "Deck";
    }

    public void layoutWidgets(Container container,
            int contentLeft, int contentTop, int width, int height) {
        int wc = container.getWidgetCount();
        if (wc == 0) {
            return;
        }
        Constraints[] cons = ensureConstraints(container);

        // position each widget to fill content area or be centered etc
        for (int i = 0; i < wc; i++) {
            Constraints c = cons[i];
            Widget widget = container.getWidget(i);
            int w, h;
            if (widget instanceof HasMaximumSize) {
                HasMaximumSize fs = (HasMaximumSize)widget;
                w = fs.getMaxWidth();
                h = fs.getMaxHeight();
            } else {
                w = c.width == 0 ? width : c.width;
                h = c.height == 0 ? height : c.height;
            }
            LDOM.setBounds(widget,
                    contentLeft + align(w, width, c.alignX),
                    contentTop + align(h, height, c.alignY),
                    w, h);
            DOM.setStyleAttribute(widget.getElement(), "overflow", c.overflow);
        }
    }

    private int align(int inner, int outer, int align) {
        switch (align) {
            case Constraints.LEFT:      return 0;
            case Constraints.RIGHT:     return outer - inner;
        }
        return (outer - inner) / 2;
    }

    private Constraints[] ensureConstraints(Container container) {
        int wc = container.getWidgetCount();
        Constraints[] cons = new Constraints[wc];
        for (int i = 0; i < wc; i++) {
            Widget w = container.getWidget(i);
            Object o = container.getLayoutConstraints(w);
            if (o instanceof Constraints) {
                cons[i] = (Constraints)o;
            } else {
                container.setLayoutConstraints(w, cons[i] = new Constraints());
            }
        }
        return cons;
    }

    public LayoutConstraints convertConstraints(LayoutConstraints layoutConstraints) {
        if (layoutConstraints instanceof Constraints) {
            return new Constraints((Constraints)layoutConstraints);
        }
        return null;
    }

    public LayoutFactory createLayoutFactory() {
        return new Factory();
    }

    /**
     * Contains values for the 'display' and 'visibility' style attributes
     * and an optional size. If the size is specified then the widget is
     * can be centered etc.
     */
    public static class Constraints implements LayoutConstraints {

        public static final String DISPLAY_NONE = "none";
        public static final String DISPLAY_DEFAULT = "";

        public static final String VISIBILITY_HIDDEN = "hidden";
        public static final String VISIBILITY_VISIBLE = "visible";

        public static final int LEFT = 0;
        public static final int CENTER = 1;
        public static final int RIGHT = 2;
        public static final int TOP = 0;
        public static final int BOTTOM = 2;

        private int width;
        private int height;
        private int alignX = CENTER;
        private int alignY = CENTER;
        private String overflow = HIDDEN;

        public Constraints() {
        }

        public Constraints(String overflow) {
            this.overflow = overflow;
        }

        public Constraints(int width, int height) {
            this(width, height, CENTER, CENTER);
        }

        public Constraints(int width, int height, int alignX, int alignY) {
            this.width = width;
            this.height = height;
            this.alignX = alignX;
            this.alignY = alignY;
        }

        public Constraints(Constraints src) {
            width = src.width;
            height = src.height;
            alignX = src.alignX;
            alignY = src.alignY;
            overflow = src.overflow;
        }

        public LayoutConstraints copy() {
            return new Constraints(this);
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getAlignX() {
            return alignX;
        }

        public void setAlignX(int alignX) {
            this.alignX = alignX;
        }

        public int getAlignY() {
            return alignY;
        }

        public void setAlignY(int alignY) {
            this.alignY = alignY;
        }

        public String getOverflow() {
            return overflow;
        }

        public void setOverflow(String overflow) {
            this.overflow = overflow;
        }

        public String toString() {
            return "w=" + width + " h=" + height + " alignX=" + alignX +
                    " alignY=" + alignY + " overflow=" + overflow;
        }
    }

    /**
     * Our factory class.
     */
    public static class Factory implements LayoutFactory {

        public Factory() {
        }

        public Layout createLayout() {
            return new DeckLayout();
        }
    }

}