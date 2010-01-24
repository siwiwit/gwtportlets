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

package smartgwt.client.ui;

import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VStack;
import org.gwtportlets.portlet.client.DoNotSendToServer;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.smartgwt.SmartPortlet;
import org.gwtportlets.portlet.client.smartgwt.SmartPortletDataSource;
import org.gwtportlets.portlet.client.smartgwt.SmartPortletFactory;
import org.gwtportlets.portlet.client.ui.Portlet;
import smartgwt.client.data.CountryDataSource;
import smartgwt.client.data.CountryRecord;

import java.util.List;

/**
 * A portlet which demonstrates Advanced filtering.
 * This is based on the Advanced Filter SmartGwt showcase example.
 */
public class AdvancedFilterPortlet extends SmartPortlet {
    final SmartPortletDataSource dataSource;

    public AdvancedFilterPortlet() {
        VStack vStack = new VStack(10);
        initWidget(vStack);
             
        dataSource = new CountryDataSource();
        dataSource.setPortlet(this);

        final FilterBuilder filterBuilder = new FilterBuilder();
        filterBuilder.setDataSource(dataSource);

        final ListGrid countryGrid = new ListGrid();
        countryGrid.setWidth(550);
        countryGrid.setHeight(224);
        countryGrid.setDataSource(dataSource);
        countryGrid.setAutoFetchData(true);

        ListGridField nameField = new ListGridField("countryName", "Country");
        ListGridField continentField = new ListGridField("continent", "Continent");

        ListGridField populationField = new ListGridField("population", "Population");
        populationField.setType(ListGridFieldType.INTEGER);
        populationField.setCellFormatter(new CellFormatter() {
            public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
                if(value == null) return null;
                try {
                    NumberFormat nf = NumberFormat.getFormat("0,000");
                    return nf.format(((Number) value).longValue());
                } catch (Exception e) {
                    return value.toString();
                }
            }
        });
        ListGridField independenceField = new ListGridField("independence", "Independence");
        countryGrid.setFields(nameField,continentField, populationField, independenceField);

        IButton filterButton = new IButton("Filter");
        filterButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                countryGrid.filterData(filterBuilder.getCriteria());
            }
        });

        vStack.addMember(filterBuilder);
        vStack.addMember(filterButton);
        vStack.addMember(countryGrid);

    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    private void restore(Factory f) {
        DSRequest request = removeRequest(f);
        if (request != null) {
            switch (request.getOperationType()) {
                case FETCH:
                    fetchResponse(request, f);
                    break;
            }
        }
    }

    private void fetchResponse(DSRequest request, Factory f) {
        DSResponse response = SmartPortlet.createResponse(request, f);
        Record[] list = new Record[f.data.size()];
        for (int i = 0; i < list.length; i++) {
            list[i] = CountryDataSource.createRecord(f.data.get(i));
        }
        response.setData(list);
        dataSource.processResponse(request.getRequestId(), response);
    }


    public static class Factory extends SmartPortletFactory<AdvancedFilterPortlet> {
        @DoNotSendToServer
        public List<CountryRecord> data;

        public Factory() {
        }

        public Factory(Portlet p) {
            super(p);
        }

        public AdvancedFilterPortlet createWidget() {
            return new AdvancedFilterPortlet();
        }

        @Override
        public void refresh(AdvancedFilterPortlet p) {
            super.refresh(p);
            p.restore(this);
        }
    }
}
