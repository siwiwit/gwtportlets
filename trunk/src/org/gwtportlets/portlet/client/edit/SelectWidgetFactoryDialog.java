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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.WidgetFactoryMetaData;
import org.gwtportlets.portlet.client.ui.CssButton;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Displays a tree of WidgetFactoryMetaData instances for the user to select
 * from.
 */
public class SelectWidgetFactoryDialog extends PageEditorDialog {

    private Tree tree = new Tree();

    private boolean okPressed;

    public SelectWidgetFactoryDialog(List<WidgetFactoryMetaData> list) {
        this(false, true, list);
    }

    public SelectWidgetFactoryDialog(boolean autoHide, boolean modal,
            List<WidgetFactoryMetaData> list) {
        super(autoHide, modal);

        setText("Choose Widget To Add");

        // sort by category, then name
        Collections.sort(list, new Comparator<WidgetFactoryMetaData>() {
            public int compare(WidgetFactoryMetaData a,
                    WidgetFactoryMetaData b) {
                int d = a.getCategory().compareTo(b.getCategory());
                if (d != 0) {
                    return d;
                }
                return a.getName().compareTo(b.getName());
            }
        });

        int n = list.size();
        String lastCat = "";
        TreeItem cat = new TreeItem("Uncategorized");
        for (int i = 0; i < n; i++) {
            WidgetFactoryMetaData d = list.get(i);
            if (!lastCat.equals(d.getCategory())) {
                lastCat = d.getCategory();
                cat = tree.addItem(lastCat);
            }
            cat.addItem(createItem(d));
        }

        final Button ok = new CssButton("OK", new ClickHandler() {
            public void onClick(ClickEvent event) {
                okPressed = true;
                hide();
            }
        });
        ok.setEnabled(false);

        Button cancel = new CssButton("Cancel", new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });

        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            public void onSelection(SelectionEvent<TreeItem> event) {
                ok.setEnabled(getSelectedWidgetFactory() != null);
            }
        });

        addButton(ok);
        addButton(cancel);

        setWidget(tree);

        setPixelSize(600, 500);
    }

    /**
     * Did the user click the OK button to close the dialog?
     */
    public boolean isOkPressed() {
        return okPressed;
    }

    /**
     * Get the selected factory or null if none.
     */
    public WidgetFactoryMetaData getSelectedWidgetFactory() {
        TreeItem i = tree.getSelectedItem();
        return i == null ? null : (WidgetFactoryMetaData)i.getUserObject();
    }

    protected TreeItem createItem(WidgetFactoryMetaData d) {
        StringBuffer s = new StringBuffer();
        s.append("<img width=\"8\" height=\"8\" src=\"").append("img/portlet-")
                .append(d.isContainer() ? "square" : "dot")
                .append(".gif\"><span class=\"portlet-ed-label\">").append(d.getName())
                .append("</span><span class=\"portlet-ed-description\">")
                .append(d.getDescription()).append("</span>");
        TreeItem i = new TreeItem(s.toString());
        i.setUserObject(d);
        return i;
    }

}
