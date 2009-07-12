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

package org.gwtportlets.portlet.client.util;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.layout.LDOM;

/**
 * Animates moving a widget from one position to another.
 */
public class MoveAnimation {

    private Widget widget;
    private Rectangle from;
    private Rectangle to;
    private float pos;
    private int ms = 50;
    private float step = 0.199f;
    private Timer timer;
    private boolean detachOnComplete;
    private ChangeListener changeListener;

    public static final String STYLE_MOVE_ANIMATION = "portlet-move-animation";

    public MoveAnimation(Widget from, Widget to,
            ChangeListener changeListener) {
        this(LDOM.getBounds(from), LDOM.getBounds(to), changeListener);
    }

    public MoveAnimation(Rectangle from, Rectangle to,
            ChangeListener changeListener) {
        this(new HTML(), from, to, changeListener);
        widget.setStyleName(STYLE_MOVE_ANIMATION);
        detachOnComplete = true;
    }

    public MoveAnimation(Widget widget, Rectangle from, Rectangle to,
            ChangeListener changeListener) {
        this.widget = widget;
        this.from = from;
        this.to = to;
        this.changeListener = changeListener;
    }

    public void start() {
        timer = new Timer() {
            public void run() {
                pos += MoveAnimation.this.step;
                update();
            }
        };
        timer.scheduleRepeating(ms);
        update();
    }

    protected void update() {
        if (!widget.isAttached()) {
            RootPanel.get().add(widget);
        }
        Rectangle r = new Rectangle(to);
        boolean done = pos >= 1.0f;
        if (!done) {
            r.width =  from.width +  (int)((r.width - from.width) * pos);
            r.height = from.height + (int)((r.height - from.height) * pos);
            r.x = from.x + (int)((r.x - from.x) * pos);
            r.y = from.y + (int)((r.y - from.y) * pos);
        } else {
            timer.cancel();
            if (detachOnComplete) {
                RootPanel.get().remove(widget);    
            }
        }
        if (widget.isAttached()) {
            LDOM.setBounds(widget, r.x, r.y, r.width, r.height);
        }
        if (done && changeListener != null) {
            changeListener.onChange(null);
        }
    }

    public Widget getWidget() {
        return widget;
    }

    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    public Rectangle getFrom() {
        return from;
    }

    public void setFrom(Rectangle from) {
        this.from = from;
    }

    public Rectangle getTo() {
        return to;
    }

    public void setTo(Rectangle to) {
        this.to = to;
    }

    public int getMs() {
        return ms;
    }

    public void setMs(int ms) {
        this.ms = ms;
    }

    public float getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }

    public boolean isDetachOnComplete() {
        return detachOnComplete;
    }

    public void setDetachOnComplete(boolean detachOnComplete) {
        this.detachOnComplete = detachOnComplete;
    }
}
