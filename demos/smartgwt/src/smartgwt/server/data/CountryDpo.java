package smartgwt.server.data;

import org.apache.log4j.Logger;
import org.gwtportlets.portlet.smartgwt.client.DataTransferObject;
import org.gwtportlets.portlet.smartgwt.client.SmartPortletFactory;
import org.gwtportlets.portlet.smartgwt.server.DataProviderObject;
import org.gwtportlets.portlet.smartgwt.server.SmartComparator;
import org.gwtportlets.portlet.smartgwt.server.SmartException;
import org.gwtportlets.portlet.smartgwt.server.SmartFilter;
import smartgwt.client.data.CountryDto;
import smartgwt.server.CountryRecordTestData;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class CountryDpo implements DataProviderObject {
    private static final Logger log = Logger.getLogger(CountryDpo.class);
    private static final SmartFilter<CountryDto> filter = new SmartFilter<CountryDto>(CountryDto.class);

    public String getDataSourceId() {
        return CountryDto.DATA_SOURCE_ID;
    }

    public List<DataTransferObject> fetchData(SmartPortletFactory f) {
        log.info("Doing fetch: " + f.getStartRow() + " -> " + f.getEndRow());
        List<CountryDto> list = CountryRecordTestData.getList();
        if (f.getSort() != null) {
            log.info("Sorting");
            Collections.sort(list, new SmartComparator<CountryDto>(CountryDto.class, f.getSort()));
        }
        if (f.getCriteria() != null) {
            log.info("Filtering " + list.size() + " records");
            log.info("\n" + f.getCriteria().toString());
            try {
                list = filter.filter(f.getCriteria(), list);
            } catch (SmartException e) {
                log.error("Could not filter", e);
            }
            SmartFilter.FilterWalker walker = SmartFilter.SqlFilterWalker.MYSQL_FILTER_WALKER;
            try {
                filter.walk(f.getCriteria(), walker);
                log.info("SQL:\n" + walker.toString());
            } catch (SmartFilter.SmartFilterException e) {
                log.error("Could not get SQL filter", e);
            }
        }
        log.info("Got " + list.size() + " records");
        return f.limitList(list);
    }

    public DataTransferObject updateData(SmartPortletFactory smartPortletFactory) {
        return null;
    }

    public DataTransferObject addData(SmartPortletFactory smartPortletFactory) {
        return null;
    }

    public DataTransferObject removeData(SmartPortletFactory smartPortletFactory) {
        return null;
    }
}
