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

package main.client.ui;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.ui.Portlet;
import org.gwtportlets.portlet.client.ui.PortletFactory;
import org.gwtportlets.portlet.client.ui.Theme;
import org.gwtportlets.portlet.client.util.FormBuilder;

/**
 * Displays list of themes and changes theme when one is selected.
 */
public class ThemeListPortlet extends Portlet {

    public ThemeListPortlet() {
        final ListBox list = new ListBox();
        int sel = -1;
        String current = Theme.get().getCurrentTheme();
        String[] a = Theme.get().getThemes();
        for (int i = 0; i < a.length; i++) {
            String theme = a[i];
            list.addItem(insertSpaces(theme) + " ", theme);
            if (theme.equals(current)) {
                sel = i;
            }
        }
        if (sel >= 0) {
            list.setSelectedIndex(sel);
        }
        list.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                int i = list.getSelectedIndex();
                if (i >= 0) {
                    Theme.get().changeTheme(list.getValue(i));
                }
            }
        });
        list.setWidth("100%");

        FormBuilder b = new FormBuilder();
        b.label("Theme").field(list).width("100%").endRow();
        initWidget(b.getForm());
    }

    private String insertSpaces(String s) {
        int n = s.length();
        StringBuffer b = new StringBuffer(n + 8);
        boolean prevCaps = true;
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                if (!prevCaps) {
                    b.append(' ');
                    prevCaps = true;
                }
            } else {
                prevCaps = false;
            }
            b.append(c);
        }
        return b.toString();
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    public static class Factory extends PortletFactory<ThemeListPortlet> {

        public Factory() {
        }

        public Factory(ThemeListPortlet p) {
            super(p);
        }

        public ThemeListPortlet createWidget() {
            return new ThemeListPortlet();
        }
    }

}
