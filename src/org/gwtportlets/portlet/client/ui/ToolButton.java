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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import org.gwtportlets.portlet.client.layout.HasMaximumSize;
import org.gwtportlets.portlet.client.layout.LDOM;

/**
 * Small button for toolbars and so on. Styled using a background image
 * sprite. Calculates the background-position values automatically based on
 * its image index and button size obtained from the theme.
 */
public class ToolButton extends PositionAwareComposite
        implements HasMaximumSize, HasClickHandlers, HasMouseOverHandlers {

    public static final int REFRESH = 0;
    public static final int MINIMIZE = 1;
    public static final int MAXIMIZE = 2;
    public static final int CLOSE = 3;
    public static final int CONFIGURE = 4;
    public static final int RESTORE = 5;
    public static final int HIDE = 1;
    public static final int SHOW = 2;

    private int imageIndex;
    private int width;
    private int height;
    private boolean enabled = true;

    public ToolButton(int imageIndex) {
        initWidget(new Image(GWT.getModuleBaseURL() + "clear.cache.gif"));
        setStyleName("portlet-toolbutton");
        Theme.get().updateToolButton(this);
        setImageIndex(imageIndex);
        updateImage(false);
    }

    public ToolButton() {
        this(REFRESH);
    }

    public ToolButton(int imageIndex, String tooltip, ClickHandler clickHandler) {
        this(imageIndex);
        setTitle(tooltip);
        addClickHandler(clickHandler);
    }

    private void updateImage(boolean hover) {
        int row = enabled ? hover ? 1 : 0 : 2;
        LDOM.setBackgroundPosition(getElement(),
                "-" + (width * imageIndex) + "px -" + (height * row) + "px");
    }

    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEOVER:
                updateImage(true);
                break;
            case Event.ONMOUSEOUT:
                updateImage(false);
                break;
            case Event.ONCLICK:
                if (!enabled) {
                    return;
                }
                break;
        }
        super.onBrowserEvent(event);
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        if (this.imageIndex != imageIndex) {
            this.imageIndex = imageIndex;
            if (isAttached()) {
                updateImage(false);
            }
        }
    }

    public int getMaxWidth() {
        return width;
    }

    public int getMaxHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
        setWidth(width + "px");
    }

    public void setHeight(int height) {
        this.height = height;
        setHeight(height + "px");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (isAttached()) {
                updateImage(false);
            }
        }
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }

    public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return addDomHandler(handler, MouseOverEvent.getType());
    }
}
