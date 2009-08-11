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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import org.gwtportlets.portlet.client.DoNotPersist;
import org.gwtportlets.portlet.client.DoNotSendToServer;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.ui.*;
import org.gwtportlets.portlet.client.util.FormBuilder;

import java.io.Serializable;

/**
 * A Simple data provider which demonstrates how one can write CRUD user interfaces
 * with GWT portlets.
 *<p>
 * Displays add, edit and delete buttons above a grid of contacts.
 */
public class SimpleCrudPortlet extends Portlet {

    private Contact[] contactList;

    private LayoutPanel panel = new LayoutPanel();
    private FlexTable grid;
    private FormBuilder fbButtons;
    private ClickHandler handler;
    private CssButton add;
    private CssButton edit;

    private int lastSelectedRow;

    public SimpleCrudPortlet() {
        // ClickHandler for the grid which sets the style for the selected row
        handler = new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                lastSelectedRow = grid.getCellForEvent(clickEvent).getRowIndex();

                for (int i = 1; i < grid.getRowCount(); i++) {
                    if (i == lastSelectedRow) {
                        grid.getRowFormatter().setStyleName(i, "grid_selected");
                    } else {
                        grid.getRowFormatter().setStyleName(i, i % 2 == 0 ? "grid_even" : "grid_odd");
                    }
                }
            }
        };

        fbButtons = new FormBuilder();
        add = new CssButton("Add", new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                add();
            }
        });

        edit = new CssButton("Edit", new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                edit();
            }
        });

        CssButton delete = new CssButton("Delete", new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                delete();
            }
        });
        fbButtons.add(add, edit, delete);
        
        initWidget(panel);
    }

    private void restore(Factory f) {
        panel.clear();
        contactList = f.contactList;

        grid = new FlexTable();
        grid.addClickHandler(handler);
        fillData();

        panel.add(fbButtons.getFormInPanel(), 30);
        SimplePanel sp = new SimplePanel();
        sp.add(grid);
        panel.add(sp);
    }

    /**
     * Add the headers and contacts to the grid.
     */
    private void fillData() {
        grid.setText(0, 0, "Contact Id");
        grid.setText(0, 1, "Name");
        grid.setText(0, 2, "Moble");
        grid.getRowFormatter().setStyleName(0, "grid_header");

        for (int i = 0; i < contactList.length; i++) {
            Contact contact = contactList[i];
            int row = i + 1;
            grid.setText(row, 0, "" + contact.contactId);
            grid.setText(row, 1, contact.name);
            grid.setText(row, 2, contact.mobile);
            grid.getRowFormatter().setStyleName(row, row % 2 == 0 ? "grid_even" : "grid_odd");
        }
    }

    /**
     * Open the contact dialog with a new contact.
     */
    private void add() {
        showContactDialog(new Contact());
    }

    /**
     * Open the contact dialog with the selected contact's details.
     */
    private void edit() {
        Contact c = getSelectedContact();
        if (c != null) {
            showContactDialog(c);
        } else {
            Window.alert("Please select a contact to edit.");
        }
    }

    /**
     * Helper method which dispays a dialog to change the contacts details.
     */
    private void showContactDialog(final Contact c) {
        final Dialog d = new Dialog();
        final TextBox name = new TextBox();
        final TextBox mobile = new TextBox();

        boolean adding = c.contactId == 0;
        if (adding) {
            d.setText("Adding contact");
        } else {
            d.setText("Editing contact " + c.name);
            name.setText(c.name);
            mobile.setText(c.mobile);
        }

        FormBuilder fb = new FormBuilder();
        fb.label("Name").field(name).endRow();
        fb.label("Mobile").field(mobile).endRow();

        d.setWidget(fb.getForm());
        d.addButton(new CssButton("Save", new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                if (name.getText() == null || name.getText().length() == 0) {
                    Window.alert("Name is mandatory");
                    return;
                }
                if (mobile.getText() == null || mobile.getText().length() == 0) {
                    Window.alert("Mobile is mandatory");
                    return;
                }

                Factory f = new Factory(SimpleCrudPortlet.this);
                Contact dto = new Contact();
                dto.contactId = c.contactId;
                dto.name = name.getText();
                dto.mobile = mobile.getText();

                f.updateContact = dto;
                refresh(f, new AsyncCallback<WidgetFactory>() {
                    public void onFailure(Throwable throwable) {
                        Window.alert(throwable.getMessage());
                    }

                    public void onSuccess(WidgetFactory widgetFactory) {
                        d.hide();
                    }
                });
            }
        }));

        d.addCloseButton("Cancel");

        if (adding) {
            d.showNextTo(add);
        } else {
            d.showNextTo(edit);
        }
    }

    /**
     * Delete the selected contact.
     */
    private void delete() {
        Contact c = getSelectedContact();
        if (c != null &&
                Window.confirm("Are you sure you would like to delete " + c.name + "?")) {

            Factory f = new Factory(this);
            f.deleteContactId = c.contactId;
            refresh(f);
        } else {
            Window.alert("Please select a contact to delete.");
        }
    }

    /**
     * Helper method which returns the selected contact from the grid. Returns
     * null if no contact has been selected.
     */
    private Contact getSelectedContact() {
        Contact ans = null;
        if (lastSelectedRow > 0) {
            ans = contactList[lastSelectedRow -1];
        }
        return ans;
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    /**
     * Simple Data transfer object to send the contact's data between the
     * client and server
     */
    public static class Contact implements Serializable {
        public int contactId;
        public String name;
        public String mobile;
    }

    public static class Factory extends PortletFactory<SimpleCrudPortlet> {

        @DoNotSendToServer
        public Contact[] contactList;
        @DoNotPersist
        public int deleteContactId;
        @DoNotPersist
        public Contact updateContact;

        public Factory() {
        }

        public Factory(SimpleCrudPortlet p) {
            super(p);
        }

        public void refresh(SimpleCrudPortlet p) {
            super.refresh(p);
            p.restore(this);
        }

        public SimpleCrudPortlet createWidget() {
            return new SimpleCrudPortlet();
        }
    }

}