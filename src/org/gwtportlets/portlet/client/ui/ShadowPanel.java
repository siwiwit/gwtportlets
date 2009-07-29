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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.layout.LDOM;

/**
 * Adds a fuzzy shadow to a single widget.
 */
public class ShadowPanel extends SimplePanel {

    private Element tdNorthWest;
    private Element tdNorth;
    private Element tdNorthEast;
    private Element tdCenter;
    private Element tdEast;
    private Element tdSouthWest;
    private Element tdSouth;
    private Element tdSouthEast;

    private static final String TEMPLATE =
            "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody>" +
            "<tr class=\"sss-north\"><td></td><td></td><td></td></tr>" +
            "<tr class=\"sss-middle\"><td></td><td></td><td></td></tr>" +
            "<tr class=\"sss-south\"><td></td><td></td><td></td></tr>" +
            "</tbody></table>";

    public ShadowPanel() {
        setStyleName("portlet-shadow");
    }

    /**
     * Create containing inner.
     */
    public ShadowPanel(Widget inner) {
        this();
        setWidget(inner);
    }

    public void setStyleName(String style) {
        super.setStyleName(style);

        Widget w = getWidget();
        if (w != null) {
            clear();
        }

        // calling DOM.setStyleAttribute("class") does not work properly on IE
        // so build the style names into the template
        Element e = getElement();
        DOM.setInnerHTML(e, TEMPLATE.replaceAll("sss", style));

        Element tbody = DOM.getFirstChild(DOM.getFirstChild(e));
        Element trNorth = DOM.getChild(tbody, 0);
        Element trMiddle = DOM.getChild(tbody, 1);
        Element trSouth = DOM.getChild(tbody, 2);
        tdNorthWest = DOM.getChild(trNorth, 0);
        tdNorth = DOM.getChild(trNorth, 1);
        tdNorthEast = DOM.getChild(trNorth, 2);
        tdCenter = DOM.getChild(trMiddle, 1);
        tdEast = DOM.getChild(trMiddle, 2);
        tdSouthWest = DOM.getChild(trSouth, 0);
        tdSouth = DOM.getChild(trSouth, 1);
        tdSouthEast = DOM.getChild(trSouth, 2);

        Theme.get().updateShadowPanel(this);

        if (w != null) {
            setWidget(w);
        }
    }

    protected Element getContainerElement() {
        return tdCenter;
    }

    public void setDimensions(int northHeight, int southHeight, int westWidth,
            int eastWidth) {
        setWidth(tdNorthWest, westWidth);
        setWidth(tdNorthEast, eastWidth);

        LDOM.setBackgroundPosition(tdNorth, "0 -" + northHeight + "px");
        LDOM.setBackgroundPosition(tdNorthEast, "0 -" + (northHeight * 2) + "px");

        int y = northHeight * 3;
        LDOM.setBackgroundPosition(tdSouthWest, "0 -" + y + "px");
        LDOM.setBackgroundPosition(tdSouth, "0 -" + (y + southHeight) + "px");
        LDOM.setBackgroundPosition(tdSouthEast, "0 -" + (y + southHeight * 2) + "px");

        LDOM.setBackgroundPosition(tdEast, "-" + westWidth + "px 0");
    }

    private void setWidth(Element e, int width) {
        DOM.setStyleAttribute(e, "width", width + "px");
    }

}
