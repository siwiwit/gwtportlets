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

package org.gwtportlets.portlet.client.event;

import com.google.gwt.user.client.ui.Widget;

import java.util.EventObject;

/**
 * A command has been activated (e.g. by clicking an item on a
 * {@link org.gwtportlets.portlet.client.ui.MenuPortlet}).
 */
public class CommandEvent extends EventObject {

    private String command;
    private String args;
    private boolean handled;

    public CommandEvent(Widget w, String command, String args) {
        super(w);
        this.command = command;
        this.args = args;
    }

    public CommandEvent(Widget w, String command) {
        this(w, command, null);
    }

    public String getCommand() {
        return command;
    }

    public String getArgs() {
        return args;
    }

    /**
     * Has this command already been handled?
     */
    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public String toString() {
        return getClass().getName() + "[command=" + command +
                ", args=" + args + ", source=" + source + "]";
    }

    /**
     * If token is a command ('method(args)') then create a CommandEvent for
     * it otherwise return null.
     */
    public static CommandEvent createCommand(Widget sender, String token) {
        int n = token.length();
        if (n < 3 || token.charAt(n - 1) != ')') {
            return null;
        }
        int i = token.lastIndexOf('(', n - 1);
        if (i < 0) {
            return null;
        }
        return new CommandEvent(sender,
                token.substring(0, i), token.substring(i + 1, n - 1));
    }
}

