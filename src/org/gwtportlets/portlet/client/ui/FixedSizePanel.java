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

import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.WidgetInfo;
import org.gwtportlets.portlet.client.layout.HasMaximumSize;
import org.gwtportlets.portlet.client.layout.Layout;

/**
 * LayoutPanel that has a fixed width and height. This is useful to contain
 * a layout that needs to go into a popup.
 */
public class FixedSizePanel extends LayoutPanel implements HasMaximumSize {

    private int width = 400;
    private int height = 150;

    public FixedSizePanel() {
        this(null, 400, 150);
    }

    public FixedSizePanel(Layout layout, int width, int height) {
        super(layout);
        this.width = width;
        this.height = height;
        setLimitMaximize(true);
    }

    public String getDescription() {
        return "Fixed Size " + super.getDescription();
    }

    protected void onLoad() {
        super.onLoad();
        setPixelSize(width, height);
        layout();
    }

    public int getMaxWidth() {
        return width;
    }

    public int getMaxHeight() {
        return height;
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    @WidgetInfo(description = "LayoutPanel with a fixed size for use in popups")
    public static class Factory extends LayoutPanel.Factory {

        private int width = 400;
        private int height = 150;

        public Factory() {
        }

        public Factory(FixedSizePanel p) {
            super(p);
            width = p.width;
            height = p.height;
        }

        public void refreshSettings(LayoutPanel w) {
            super.refreshSettings(w);
            FixedSizePanel p = (FixedSizePanel)w;
            p.width = width;
            p.height = height;
        }

        public LayoutPanel createWidget() {
            return new FixedSizePanel();
        }
    }

}
