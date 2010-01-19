package smartgwt.client.data;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.*;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import org.gwtportlets.portlet.client.smartgwt.SmartPortlet;
import org.gwtportlets.portlet.client.smartgwt.SmartPortletDataSource;


public class CountryDataSource extends SmartPortletDataSource {

    public CountryDataSource(SmartPortlet portlet) {
        super(portlet);
        DataSourceIntegerField pkField = new DataSourceIntegerField("pk");
        pkField.setHidden(true);
        pkField.setPrimaryKey(true);

        DataSourceTextField countryCodeField = new DataSourceTextField("countryCode", "Code");
        countryCodeField.setRequired(true);

        DataSourceTextField countryNameField = new DataSourceTextField("countryName", "Country");
        countryNameField.setRequired(true);

        DataSourceTextField capitalField = new DataSourceTextField("capital", "Capital");
        DataSourceTextField governmentField = new DataSourceTextField("government", "Government", 500);

        DataSourceBooleanField memberG8Field = new DataSourceBooleanField("member_g8", "G8");

        DataSourceTextField continentField = new DataSourceTextField("continent", "Continent");
        continentField.setValueMap("Europe", "Asia", "North America", "Australia/Oceania", "South America", "Africa");

        DataSourceDateField independenceField = new DataSourceDateField("independence", "Nationhood");
        DataSourceFloatField areaField = new DataSourceFloatField("area", "Area (km²)");
        DataSourceIntegerField populationField = new DataSourceIntegerField("population", "Population");
        DataSourceFloatField gdpField = new DataSourceFloatField("gdp", "GDP ($M)");

        setFields(pkField, countryCodeField, countryNameField, capitalField, governmentField,
                memberG8Field, continentField, independenceField, areaField, populationField,
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
