/*
 * GWT Portlets Framework (http://code.google.com/p/gwtportlets/)
 * Copyright 2010 Business Systems Group (Africa)
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

package smartgwt.client.data;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceDateField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import org.gwtportlets.portlet.smartgwt.client.SmartPortlet;
import org.gwtportlets.portlet.smartgwt.client.SmartPortletDataSource;

/**
 * The datasource for the TownRecord data type.
 */
public class TownDataSource extends SmartPortletDataSource {
    public TownDataSource(SmartPortlet portlet) {
        super(portlet);
        DataSourceField field;
        field = new DataSourceIntegerField ("id", "Id");
        field.setPrimaryKey(true);
        field.setRequired(false);
        addField(field);
        field = new DataSourceTextField ("name", "Name");
        field.setRequired(true);
        addField(field);
        field = new DataSourceDateField ("date", "Date");
        field.setRequired(false);
        addField(field);
    }

    public static TownRecord create(Record from) {
        TownRecord to = new TownRecord();
        to.setId(from.getAttributeAsInt("id"));
        to.setName(from.getAttributeAsString("name"));
        to.setDate(from.getAttributeAsDate("date"));
        return to;
    }

    public static Record createRecord(TownRecord from) {
        Record to = new ListGridRecord(); //TODO: Generic record
        to.setAttribute("id", from.getId());
        to.setAttribute("name", from.getName());
        to.setAttribute("date", from.getDate());
        return to;
    }
}
