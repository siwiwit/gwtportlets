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

package org.gwtportlets.portlet.client.edit;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Periodically looks for new popup windows and adds a style to set z-index.
 * This is used to ensure that popup windows opened by the page editor are
 * not hidden under the overlay panel used to prevent input from getting to
 * the GUI. 
 */
class WidgetZIndexer {

    private int startWidgetCount;
    private Timer timer;

    private static final String ZINDEX_STYLE = "portlet-ed-zindex";

    /**
     * Create and start scanning until there are no widgets with our style
     * left. Adds our style to lifeline.
     */
    public WidgetZIndexer() {
    }

    public void start() {
        startWidgetCount = RootPanel.get().getWidgetCount();
        timer = new Timer() {
            public void run() {
                scan();
            }
        };
        timer.scheduleRepeating(100);
    }

    public void stop() {
        timer.cancel();
    }

    private void scan() {
        RootPanel p = RootPanel.get();
        int n = p.getWidgetCount();
        if (n == startWidgetCount) {
            stop();
        }
        for (int i = startWidgetCount; i < n; i++) {
            Widget w = p.getWidget(i);
            if (!w.getStyleName().contains(ZINDEX_STYLE)) {
                w.addStyleName(ZINDEX_STYLE);
            }
        }
    }
}