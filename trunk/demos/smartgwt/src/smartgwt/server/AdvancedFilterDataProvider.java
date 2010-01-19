package smartgwt.server;

import org.apache.log4j.Logger;
import org.gwtportlets.portlet.server.smartgwt.SmartWidgetDataProvider;
import smartgwt.client.data.CountryRecord;
import smartgwt.client.ui.AdvancedFilterPortlet;

import java.util.ArrayList;
import java.util.List;

public class AdvancedFilterDataProvider extends SmartWidgetDataProvider<AdvancedFilterPortlet.Factory> {

    private static final Logger log = Logger.getLogger(AdvancedFilterDataProvider.class);

    public Class getWidgetFactoryClass() {
        return AdvancedFilterPortlet.Factory.class;
    }

    @Override
    public void executeFetch(AdvancedFilterPortlet.Factory f) {
        log.info("Doing fetch: " + f.getStartRow() + " -> " + f.getEndRow());
        List<CountryRecord> list = CountryRecordTestData.getList();
        log.info("Got " + list.size() + " records");
        f.data = new ArrayList<CountryRecord>(list.subList(f.getStartRow(), Math.min(f.getEndRow(), list.size())));
        log.info("Sending " + f.data.size() + " records back");
        f.setTotalRows(list.size());
    }

    @Override
    public void executeAdd(AdvancedFilterPortlet.Factory f) {

    }

    @Override
    public void executeUpdate(AdvancedFilterPortlet.Factory f) {

    }

    @Override
    public void executeRemove(AdvancedFilterPortlet.Factory f) {

    }
}
