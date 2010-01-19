package smartgwt.client.data;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceDateField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import org.gwtportlets.portlet.client.smartgwt.SmartPortlet;
import org.gwtportlets.portlet.client.smartgwt.SmartPortletDataSource;

public class TownDataSource extends SmartPortletDataSource {

    public TownDataSource(SmartPortlet portlet) {
        super(portlet);
        DataSourceField field;
        field = new DataSourceIntegerField ("id", "Id");
        field.setPrimaryKey (true);
        field.setRequired (false);
        addField(field);
        field = new DataSourceTextField ("name", "Name");
        field.setRequired (true);
        addField(field);
        field = new DataSourceDateField ("date", "Date");
        field.setRequired (false);
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
