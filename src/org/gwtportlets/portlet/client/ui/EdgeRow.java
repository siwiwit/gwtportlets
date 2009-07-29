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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.*;
import org.gwtportlets.portlet.client.layout.Layout;
import org.gwtportlets.portlet.client.layout.RowLayout;
import org.gwtportlets.portlet.client.layout.Container;
import org.gwtportlets.portlet.client.layout.LDOM;

/**
 * A container with support for (rounded) left and right edges. These are
 * styled using a CSS background. The 'background-position' style attributes
 * of the edges etc are calculated to simplify the CSS. Likewise the
 * constraints for different elements are automatically worked out.<p>
 *
 * The sprite must consist of 2 rows of equal height:
 *
 * <li><b>0</b>: center background image
 * <li><b>1</b>: the left and right edge images
 */
public class EdgeRow extends DelegatingContainer {

    private final LayoutPanel outer;
    private final HTML leftEdge = new HTML();
    private final HTML rightEdge = new HTML();
    private final Container inner;

    private int offset;
    private int height;

    public EdgeRow(Container inner) {
        this.inner = inner;
        outer = new LayoutPanel(new RowLayout(false, 0));
        initWidget(outer);
        outer.add(leftEdge);
        outer.add((Widget)inner, new RowLayout.Constraints(
                RowLayout.Constraints.HIDDEN));
        outer.add(rightEdge);
        setDimensions(0, 19, 4, 4);
    }

    public void setStyleName(String style) {
        super.setStyleName(style);
        String s = style + "-bg";
        leftEdge.setStyleName(s);
        inner.setStyleName(s + " " + s + "-inner");
        rightEdge.setStyleName(s);
    }

    protected Container getDelegate() {
        return inner;
    }

    public String getDescription() {
        return "EdgeRow";
    }

    public WidgetFactory createWidgetFactory() {
        throw new UnsupportedOperationException();
    }

    public void setLayout(Layout layout) {
        throw new UnsupportedOperationException();
    }

    public void setDimensions(int offset, int height, int left, int right) {
        this.offset = offset;
        this.height = height;
        outer.setLayoutConstraints(leftEdge,
                new RowLayout.Constraints(left));
        outer.setLayoutConstraints(rightEdge,
                new RowLayout.Constraints(right));
        LDOM.setBackgroundPosition(inner.getElement(),
                "0 -" + offset + "px");
        LDOM.setBackgroundPosition(leftEdge.getElement(),
                "0 -" + (offset + height) + "px");
        LDOM.setBackgroundPosition(rightEdge.getElement(),
                "-" + left + "px -" + (offset + height) + "px");
    }

    public int getHeight() {
        return height;
    }

    public int getOffset() {
        return offset;
    }
}