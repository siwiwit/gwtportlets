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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import org.gwtportlets.portlet.client.layout.LDOM;

/**
 * Button styled with a background image sprite. Selects different image
 * for different width buttons to avoid scaling problems.
 */
public class CssButton extends Button {

    private int height;
    private int offset;
    private int width;
    private boolean def;

    private boolean hover;
    private boolean down;

    private static final int NORMAL = 0;
    private static final int HOVER = 3;
    private static final int DISABLED = 2;
    private static final int DEFAULT = 1;
    private static final int DOWN = 4;
    
    private static final String STYLE_DISABLED = "disabled";

    public CssButton() {
        init();
    }

    public CssButton(String html) {
        super(html);
        init();
    }

    public CssButton(String html, ClickHandler handler) {
        super(html, handler);
        init();
    }

    public CssButton(String html, ClickHandler handler, String title) {
        super(html, handler);
        init();
        setTitle(title);
    }

    private void init() {
        sinkEvents(Event.MOUSEEVENTS);        
        setStyleName("portlet-button");
        Theme.get().updateHeight(this);
        update();
    }

    protected void onLoad() {
        updateWidth();
        update();
    }

    private void updateWidth() {
        Theme.get().updateButtonWidth(this);
    }

    protected void onUnload() {
        hover = down = false;
    }

    public void setHTML(String html) {
        super.setHTML(html);
        if (isAttached()) {
            updateWidth();
        }
    }

    public void setText(String text) {
        super.setText(text);
        if (isAttached()) {
            updateWidth();
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        setHeight(height + "px");
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        LDOM.setSize(getElement(), width, height);
    }

    public boolean isDefault() {
        return def;
    }

    public void setDefault(boolean def) {
        this.def = def;
    }

    protected void update() {
        int i;
        if (isEnabled()) {
            i = down ? DOWN : hover ? HOVER : def ? DEFAULT : NORMAL;
        } else {
            i = DISABLED;
        }
        LDOM.setBackgroundPosition(getElement(), "-" + offset + "px -" + i * height + "px");
    }

    public void onBrowserEvent(Event ev) {
        switch (DOM.eventGetType(ev)) {
            case Event.ONMOUSEOVER:
                hover = true;
                update();
                break;
            case Event.ONMOUSEOUT:
                hover = false;
                update();
                break;
            case Event.ONMOUSEDOWN:
                down = true;
                update();
                break;
            case Event.ONMOUSEUP:
                down = false;
                update();
                break;
            case Event.ONFOCUS:
                setFocus(false);
                break;
        }
        super.onBrowserEvent(ev);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            removeStyleDependentName(STYLE_DISABLED);
        } else {
            addStyleDependentName(STYLE_DISABLED);
        }
        update();
    }
}