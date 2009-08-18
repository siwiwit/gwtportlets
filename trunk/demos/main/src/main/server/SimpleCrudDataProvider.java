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

package main.server;

import main.client.ui.SimpleCrudPortlet;
import org.gwtportlets.portlet.server.PageRequest;
import org.gwtportlets.portlet.server.WidgetDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid of contacts with add, edit and delete buttons. The Portlet refresh
 * mechanism is used to carry out the add, edit and delete operations. The
 * list of contacts is stored in the session to mimic a database.
 */
public class SimpleCrudDataProvider
        implements WidgetDataProvider<SimpleCrudPortlet.Factory> {

    private static final String SESSION_KEY = "contactList";

    public Class getWidgetFactoryClass() {
        return SimpleCrudPortlet.Factory.class;
    }

    public void refresh(SimpleCrudPortlet.Factory f, PageRequest req) {
        List<SimpleCrudPortlet.Contact> list = getContacts(req);
        if (f.update != null) {
            update(list, f.update);
        }
        if (f.deleteContactId > 0) {
            delete(list, f.deleteContactId);
        }
        save(req, list);
        f.contactList = list.toArray(new SimpleCrudPortlet.Contact[list.size()]);
    }

    /** Add/edit contact. */
    private void update(List<SimpleCrudPortlet.Contact> list,
            SimpleCrudPortlet.Contact c) {
        boolean adding = c.contactId == 0;

        // check if the name is unique
        for (SimpleCrudPortlet.Contact dto : list) {
            if (c.name != null && c.name.equalsIgnoreCase(dto.name)
                    && c.contactId != dto.contactId) {
                throw new IllegalArgumentException("Duplicate contact: '" +
                        c.name + "'");
            }
        }

        if (adding) {
            c.contactId = getNextId(list);
            list.add(c);
        } else {
            for (SimpleCrudPortlet.Contact dto : list) {
                if (dto.contactId == c.contactId) {
                    dto.name = c.name;
                    dto.mobile = c.mobile;
                }
            }
        }
    }

    private int getNextId(List<SimpleCrudPortlet.Contact> list) {
        int id = 0;
        for (SimpleCrudPortlet.Contact c : list) {
            if (c.contactId > id) {
                id = c.contactId;
            }
        }
        return id + 1;
    }

    /** Delete contact by id. */
    private void delete(List<SimpleCrudPortlet.Contact> list, int contactId) {
        for (int i = list.size() - 1; i >= 0; i--) {
            SimpleCrudPortlet.Contact c =  list.get(i);
            if (c.contactId == contactId) {
                list.remove(i);
            }
        }
    }

    /** Get list of contacts stored from the session */
    private List<SimpleCrudPortlet.Contact> getContacts(PageRequest req) {
        List<SimpleCrudPortlet.Contact> contactList = (List<SimpleCrudPortlet.Contact>)
                req.getServletRequest().getSession().getAttribute(SESSION_KEY);
        if (contactList == null) {
            save(req, contactList = createTestData());
        }
        return contactList;
    }

    private void save(PageRequest req, List list) {
        req.getServletRequest().getSession().setAttribute(SESSION_KEY, list);
    }

    private List<SimpleCrudPortlet.Contact> createTestData() {
        List<SimpleCrudPortlet.Contact> list = new ArrayList();
        list.add(createContact(1, "Bobby Test", "1234567890"));
        list.add(createContact(2, "John Doe", "+1234567890"));
        list.add(createContact(3, "Steve Bob", "+1234567890"));
        list.add(createContact(4, "Tommy Goat", "+1234567890"));
        list.add(createContact(5, "Sleepy Simon", "+1234567890"));
        return list;
    }

    private SimpleCrudPortlet.Contact createContact(int id, String name, String moblie) {
        SimpleCrudPortlet.Contact ans = new SimpleCrudPortlet.Contact();
        ans.contactId = id;
        ans.name = name;
        ans.mobile = moblie;
        return ans;
    }
}