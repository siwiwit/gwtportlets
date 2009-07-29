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
import org.gwtportlets.portlet.client.layout.DeckLayout;
import org.gwtportlets.portlet.client.layout.LDOM;

/**
 * Displays loading spinner.
 */
public class LoadingWidget extends PositionAwareComposite {

    private LayoutPanel outer = new LayoutPanel(new DeckLayout());
    private Widget blocker = LDOM.createInputBlocker();
    private Widget spinner = new HTML();

    private static final int[] SIZES = new int[]{32, 48, 64, 96, 128, 192};

    public LoadingWidget() {
        initWidget(outer);
        outer.add(blocker);
        outer.add(spinner);
        setStyleName("portlet-loading");
    }

    public void boundsUpdated() {
        update();
    }

    public void update() {
        int w = getOffsetWidth() / 2;
        int h = getOffsetHeight() / 2;
        int i;
        for (i = SIZES.length - 1; i > 0 && (SIZES[i] > w || SIZES[i] >h); i--);
        int size = SIZES[i];
        spinner.setStyleName(getStylePrimaryName() + "_" + size);
        outer.setLayoutConstraints(spinner, new DeckLayout.Constraints(size,
                size));
        outer.layout();
    }

    public void setStyleName(String style) {
        super.setStyleName(style);
        blocker.setStyleName(style + "-blocker");
    }

}
