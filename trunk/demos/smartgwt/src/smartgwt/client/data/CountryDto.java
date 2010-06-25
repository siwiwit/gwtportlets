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
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceDateField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import org.gwtportlets.portlet.smartgwt.client.DataTransferObject;
import org.gwtportlets.portlet.smartgwt.client.SmartPortlet;
import org.gwtportlets.portlet.smartgwt.client.SmartPortletDataSource;

import java.util.Date;

public class CountryDto implements DataTransferObject {

    public static final String DATA_SOURCE_ID = "countryDataSource";
    
    private Integer id;
    private String countryName;
    private String continent;
    private String countryCode;
    private double area;
    private int population;
    private double gdp;
    private String government;
    private String capital;
    private Date independence;

    public CountryDto() {
    }

    public CountryDto(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public double getGdp() {
        return gdp;
    }

    public void setGdp(double gdp) {
        this.gdp = gdp;
    }

    public String getGovernment() {
        return government;
    }

    public void setGovernment(String government) {
        this.government = government;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public Date getIndependence() {
        return independence;
    }

    public void setIndependence(Date independence) {
        this.independence = independence;
    }

    public static SmartPortletDataSource getDataSource(SmartPortlet portlet) {
        return getDataSource(portlet, DATA_SOURCE_ID);
    }

    public static SmartPortletDataSource getDataSource(SmartPortlet portlet, String id) {
        id = portlet.getDataSourceId(id);
        SmartPortletDataSource ds = (SmartPortletDataSource) DataSource.get(id);
        if (ds != null) {
            return ds;
        }
        ds = new SmartPortletDataSource(portlet);
        ds.setID(id);

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
        DataSourceFloatField areaField = new DataSourceFloatField("area", "Area (km^2)");
        DataSourceIntegerField populationField = new DataSourceIntegerField("population", "Population");
        DataSourceFloatField gdpField = new DataSourceFloatField("gdp", "GDP ($M)");

        ds.setFields(pkField, countryCodeField, countryNameField, capitalField, governmentField,
                continentField, independenceField, areaField, populationField,
                gdpField);

        return ds;
    }

    public void copyFromRecord(Record from) {
        setId(from.getAttributeAsInt("id"));
        setCountryName(from.getAttributeAsString("countryName"));
        setContinent(from.getAttributeAsString("continent"));
        setCountryCode(from.getAttributeAsString("countryCode"));
        setArea(from.getAttributeAsDouble("area"));
        setPopulation(from.getAttributeAsInt("population"));
        setGdp(from.getAttributeAsDouble("gdp"));
        setGovernment(from.getAttributeAsString("government"));
        setCapital(from.getAttributeAsString("capital"));
        setIndependence(from.getAttributeAsDate("independence"));
    }

    public void copyToRecord(Record to) {
        to.setAttribute("id", getId());
        to.setAttribute("countryName", getCountryName());
        to.setAttribute("continent", getContinent());
        to.setAttribute("countryCode", getCountryCode());
        to.setAttribute("area", getArea());
        to.setAttribute("population", getPopulation());
        to.setAttribute("gdp", getGdp());
        to.setAttribute("government", getGovernment());
        to.setAttribute("capital", getCapital());
        to.setAttribute("independence", getIndependence());
    }
}
