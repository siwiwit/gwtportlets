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

package main.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.event.BroadcastListener;
import org.gwtportlets.portlet.client.event.CommandEvent;
import org.gwtportlets.portlet.client.ui.Portlet;
import org.gwtportlets.portlet.client.ui.PortletFactory;

/**
 * Displays the most recent
 * {@link org.gwtportlets.portlet.client.event.CommandEvent}.
 */
public class CommandDemoPortlet extends Portlet implements BroadcastListener {

    private Label label = new Label();

    public CommandDemoPortlet() {
        FlowPanel panel = new FlowPanel();
        initWidget(panel);
        panel.add(new Label("Select menu options from the 'Command' menu"));
        panel.add(label);
    }

    public void onBroadcast(Object event) {
        if (event instanceof CommandEvent) {
            CommandEvent ce = (CommandEvent)event;
            label.setText("Received CommandEvent: '" + ce.getCommand() + "'");
        }
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    public static class Factory extends PortletFactory<CommandDemoPortlet> {

        public Factory() {
        }

        public Factory(CommandDemoPortlet p) {
            super(p);
        }

        public CommandDemoPortlet createWidget() {
            return new CommandDemoPortlet();
        }
    }

}