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
import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.DoNotPersist;
import org.gwtportlets.portlet.client.DoNotSendToServer;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.ui.*;
import org.gwtportlets.portlet.client.util.FormBuilder;

import java.io.Serializable;

/**
 * Grid of contacts with add, edit and delete buttons. The Portlet refresh
 * mechanism is used to carry out the add, edit and delete operations.
 */
public class SimpleCrudPortlet extends Portlet {

    private Contact[] contactList;

    private FlexTable grid;

    private CssButton edit= new CssButton("Edit", new ClickHandler() {
        public void onClick(ClickEvent clickEvent) {
            showContactDialog(getSelectedContact());
        }
    });
    private CssButton delete= new CssButton("Delete", new ClickHandler() {
        public void onClick(ClickEvent clickEvent) {
            delete();
        }
    });

    private int selectedRow;

    public SimpleCrudPortlet() {
        LayoutPanel panel = new LayoutPanel();
        initWidget(panel);

        FormBuilder b = new FormBuilder();
        b.caption("ID").caption("Name").caption("Mobile").endRow();

        grid = b.getForm();
        grid.addStyleName("data");
        grid.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                HTMLTable.Cell cell = grid.getCellForEvent(clickEvent);
                setSelection(cell == null ? -1 : cell.getRowIndex());
            }
        });

        FlowPanel p = new FlowPanel();
        p.add(new CssButton("Add", new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                showContactDialog(new Contact());
            }
        }));
        p.add(edit);
        p.add(delete);

        panel.add(p, 24);
        panel.add(b.getFormInPanel());
    }

    private void setSelection(int row) {
        this.selectedRow = row;
        HTMLTable.RowFormatter f = grid.getRowFormatter();
        for (int i = 1; i < grid.getRowCount(); i++) {
            if (i == row) {
                f.addStyleName(i, "selected");
            } else {
                f.removeStyleName(i, "selected");
            }
        }
        edit.setEnabled(row > 0);
        delete.setEnabled(row > 0);
    }

    private void restore(Factory f) {
        contactList = f.contactList;
        for (int i = grid.getRowCount() - 1; i >= 1; i--) {
            grid.removeRow(i);
        }
        if (contactList != null) {
            for (int i = 0; i < contactList.length; i++) {
                Contact contact = contactList[i];
                int row = i + 1;
                grid.setText(row, 0, "" + contact.contactId);
                grid.setText(row, 1, contact.name);
                grid.setText(row, 2, contact.mobile);
            }
        }
    }

    private void showContactDialog(final Contact c) {
        final Dialog dlg = new Dialog();
        final TextBox name = new TextBox();
        final TextBox mobile = new TextBox();

        boolean adding = c.contactId == 0;
        dlg.setText((adding ? "Add" : "Edit") + " Contact");
        name.setText(c.name);
        mobile.setText(c.mobile);

        FormBuilder fb = new FormBuilder();
        fb.label("Name").field(name).endRow();
        fb.label("Mobile").field(mobile).endRow();

        dlg.setWidget(fb.getForm());
        dlg.addButton(new CssButton("OK", new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                if (name.getText().length() == 0 || mobile.getText().length() == 0) {
                    Window.alert("Name and Mobile are required");
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
                        // ignore - handled by main.client.Demo
                    }
                    public void onSuccess(WidgetFactory f) {
                        dlg.hide();
                    }
                });
            }
        }));
        dlg.addCloseButton("Cancel");
        dlg.showNextTo(grid);
    }

    private void delete() {
        Contact c = getSelectedContact();
        if (Window.confirm("Are you sure you want to delete " + c.name + "?")) {
            Factory f = new Factory(this);
            f.deleteContactId = c.contactId;
            refresh(f);
        }
    }

    private Contact getSelectedContact() {
        return selectedRow >= 1 ? contactList[selectedRow - 1] : null;
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