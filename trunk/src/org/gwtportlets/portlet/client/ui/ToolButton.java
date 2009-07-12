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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.core.client.GWT;
import org.gwtportlets.portlet.client.layout.HasMaximumSize;
import org.gwtportlets.portlet.client.layout.LDOM;

/**
 * Small button for toolbars and so on. Styled using a background image
 * sprite. Calculates the background-position values automatically based on
 * its image index and button size obtained from the theme.
 */
public class ToolButton extends PositionAwareComposite
        implements HasMaximumSize, SourcesClickEvents, SourcesMouseEvents {

    public static final int REFRESH = 0;
    public static final int MINIMIZE = 1;
    public static final int MAXIMIZE = 2;
    public static final int CLOSE = 3;
    public static final int CONFIGURE = 4;
    public static final int RESTORE = 5;

    private int imageIndex;
    private int width;
    private int height;
    private boolean enabled = true;

    private Image img = new Image(GWT.getModuleBaseURL() + "clear.cache.gif");

    public ToolButton(int imageIndex) {
        initWidget(img);
        setStyleName("portlet-toolbutton");
        Theme.get().updateToolButton(this);
        setImageIndex(imageIndex);
        updateImage(false);
        sinkEvents(Event.MOUSEEVENTS);
    }

    public ToolButton() {
        this(REFRESH);
    }

    public ToolButton(int imageIndex, String tooltip, ClickListener clickListener) {
        this(imageIndex);
        setTitle(tooltip);
        addClickListener(clickListener);
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

    public void addClickListener(ClickListener listener) {
        img.addClickListener(listener);
    }

    public void removeClickListener(ClickListener listener) {
        img.removeClickListener(listener);
    }

    public void addMouseListener(MouseListener listener) {
        img.addMouseListener(listener);
    }

    public void removeMouseListener(MouseListener listener) {
        img.removeMouseListener(listener);
    }
}
