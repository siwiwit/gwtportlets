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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import org.gwtportlets.portlet.client.util.GenUtil;

/**
 * Detects clicks on links that are history tokens and makes them work
 * property.
 */
public class HTMLEx extends HTML {

    public HTMLEx() {
        init();
    }

    public HTMLEx(String string) {
        super(string);
        init();
    }

    public HTMLEx(String string, boolean wordWrap) {
        super(string, wordWrap);
        init();
    }

    private void init() {
        sinkEvents(Event.ONCLICK);
    }

    public void onBrowserEvent(Event ev) {
        super.onBrowserEvent(ev);
        switch (DOM.eventGetType(ev)) {
            case Event.ONCLICK:
                onClick(ev);
                break;
        }
    }

    protected void onClick(Event ev) {
        String token = GenUtil.getTargetHistoryToken(ev);
        if (token != null) {
            int i = token.indexOf("%3F");
            if (i > 0) {
                token = token.substring(0, i) + '?' + token.substring(i + 3);
            }
            onTokenClicked(token);
            DOM.eventPreventDefault(ev);
        }
    }

    /**
     * A link containing a history token (e.g. '#abc') has been clicked.
     */
    protected void onTokenClicked(String token) {
        History.newItem(token);
    }
}
