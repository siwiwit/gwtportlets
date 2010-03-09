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

package smartgwt.server;

import org.apache.log4j.Logger;
import org.gwtportlets.portlet.smartgwt.server.SmartComparator;
import org.gwtportlets.portlet.smartgwt.server.SmartException;
import org.gwtportlets.portlet.smartgwt.server.SmartFilter;
import org.gwtportlets.portlet.smartgwt.server.SmartWidgetDataProvider;
import smartgwt.client.data.CountryRecord;
import smartgwt.client.ui.AdvancedFilterPortlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The data provider for the Advanced Filter portlet demo.
 *
 * @author Carl Crous
 */
public class AdvancedFilterDataProvider extends SmartWidgetDataProvider<AdvancedFilterPortlet.Factory> {

    private static final Logger log = Logger.getLogger(AdvancedFilterDataProvider.class);
    private static final SmartFilter<CountryRecord> filter = new SmartFilter<CountryRecord>(CountryRecord.class);

    public Class getWidgetFactoryClass() {
        return AdvancedFilterPortlet.Factory.class;
    }

    @Override
    public void executeFetch(AdvancedFilterPortlet.Factory f) {
        log.info("Doing fetch: " + f.getStartRow() + " -> " + f.getEndRow());
        List<CountryRecord> list = CountryRecordTestData.getList();
        if (f.getSort() != null) {
            log.info("Sorting");
            Collections.sort(list, new SmartComparator<CountryRecord>(CountryRecord.class, f.getSort()));
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
        f.data = new ArrayList<CountryRecord>(list.subList(Math.min(f.getStartRow(), list.size()), Math.min(f.getEndRow(), list.size())));
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
