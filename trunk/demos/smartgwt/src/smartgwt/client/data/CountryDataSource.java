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

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceDateField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import org.gwtportlets.portlet.client.smartgwt.SmartPortletDataSource;

/**
 * A datasource for the CountryRecord data type.
 *
 * This is based on a sample from the SmartGwt showcase.
 */
public class CountryDataSource extends SmartPortletDataSource {

    public CountryDataSource() {
        super();
        DataSourceIntegerField pkField = new DataSourceIntegerField("pk");
        pkField.setHidden(true);
        pkField.setPrimaryKey(true);

        DataSourceTextField countryCodeField = new DataSourceTextField("countryCode", "Code");
        countryCodeField.setRequired(true);

        DataSourceTextField countryNameField = new DataSourceTextField("countryName", "Country");
        countryNameField.setRequired(true);

        DataSourceTextField capitalField = new DataSourceTextField("capital", "Capital");
        DataSourceTextField governmentField = new DataSourceTextField("government", "Government", 500);

        DataSourceTextField continentField = new DataSourceTextField("continent", "Continent");
        continentField.setValueMap("Europe", "Asia", "North America", "Australia/Oceania", "South America", "Africa");

        DataSourceDateField independenceField = new DataSourceDateField("independence", "Nationhood");
        DataSourceFloatField areaField = new DataSourceFloatField("area", "Area (km²)");
        DataSourceIntegerField populationField = new DataSourceIntegerField("population", "Population");
        DataSourceFloatField gdpField = new DataSourceFloatField("gdp", "GDP ($M)");

        setFields(pkField, countryCodeField, countryNameField, capitalField, governmentField,
                continentField, independenceField, areaField, populationField,
                gdpField);

    }

    public static CountryRecord create(Record from) {
        CountryRecord to = new CountryRecord();
        to.setId(from.getAttributeAsInt("id"));
        to.setCountryName(from.getAttributeAsString("countryName"));
        to.setContinent(from.getAttributeAsString("continent"));
        to.setCountryCode(from.getAttributeAsString("countryCode"));
        to.setArea(from.getAttributeAsDouble("area"));
        to.setPopulation(from.getAttributeAsInt("population"));
        to.setGdp(from.getAttributeAsDouble("gdp"));
        to.setGovernment(from.getAttributeAsString("government"));
        to.setCapital(from.getAttributeAsString("capital"));
        to.setIndependence(from.getAttributeAsDate("independence"));
        return to;
    }

    public static Record createRecord(CountryRecord from) {
        Record to = new ListGridRecord();
        to.setAttribute("id", from.getId());
        to.setAttribute("countryName", from.getCountryName());
        to.setAttribute("continent", from.getContinent());
        to.setAttribute("countryCode", from.getCountryCode());
        to.setAttribute("area", from.getArea());
        to.setAttribute("population", from.getPopulation());
        to.setAttribute("gdp", from.getGdp());
        to.setAttribute("government", from.getGovernment());
        to.setAttribute("capital", from.getCapital());
        to.setAttribute("independence", from.getIndependence());
        return to;
    }
}
