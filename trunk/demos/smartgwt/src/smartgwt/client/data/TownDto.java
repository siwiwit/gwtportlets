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

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceDateField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import org.gwtportlets.portlet.smartgwt.client.DataTransferObject;
import org.gwtportlets.portlet.smartgwt.client.SmartPortlet;
import org.gwtportlets.portlet.smartgwt.client.SmartPortletDataSource;

import java.util.Date;

public class TownDto implements DataTransferObject {
    public static final String DATA_SOURCE_ID = "townDataSource";

    private Integer id;
    private String name;
    private Date date;

    public TownDto() {
    }

    public TownDto(Integer id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public Integer getId () {
        return id;
    }

    public void setId (Integer id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Date getDate () {
        return date;
    }

    public void setDate (Date date) {
        this.date = date;
    }

    public static SmartPortletDataSource getDataSource(SmartPortlet portlet) {
        return getDataSource(portlet, DATA_SOURCE_ID);
    }

    public static SmartPortletDataSource getDataSource(SmartPortlet portlet, String id) {
        id = portlet.getDataSourceId(id);
        SmartPortletDataSource ds = (SmartPortletDataSource)DataSource.get(id);
        if (ds != null) {
            return ds;
        }
        ds = new SmartPortletDataSource(portlet);
        ds.setID(id);

        DataSourceField field;
        field = new DataSourceIntegerField("id", "Id");
        field.setPrimaryKey(true);
        field.setRequired(false);
        ds.addField(field);
        field = new DataSourceTextField("name", "Name");
        field.setRequired(true);
        ds.addField(field);
        field = new DataSourceDateField("date", "Date");
        field.setRequired(false);
        ds.addField(field);

        return ds;
    }

    public void copyFromRecord(Record from) {
        setId(from.getAttributeAsInt("id"));
        setName(from.getAttributeAsString("name"));
        setDate(from.getAttributeAsDate("date"));
    }

    public void copyToRecord(Record to) {
        to.setAttribute("id", getId());
        to.setAttribute("name", getName());
        to.setAttribute("date", getDate());
    }
}
