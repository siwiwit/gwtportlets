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

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.gwtportlets.portlet.client.DoNotSendToServer;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.smartgwt.SmartPortlet;
import org.gwtportlets.portlet.client.smartgwt.SmartPortletDataSource;
import org.gwtportlets.portlet.client.smartgwt.SmartPortletFactory;
import smartgwt.client.data.TownDataSource;
import smartgwt.client.data.TownRecord;

import java.util.List;

/**
 * A portlet example which demonstrates a CRUD application.
 * This is based on the Compound Editor demo in the SmartGwt showcase.
 */
public class CompoundEditorPortlet extends SmartPortlet {
    final SmartPortletDataSource dataSource;

    private final CompoundEditor cEditor;

    public CompoundEditorPortlet() {

        dataSource = new TownDataSource();
        dataSource.setPortlet(this);

        cEditor = new CompoundEditor(dataSource);
        cEditor.setDatasource(dataSource);

        VLayout layout = new VLayout(15);
        initWidget(layout);
        layout.setWidth100();
        layout.setHeight("80%");
        layout.addMember(cEditor);
    }

    private void restore(Factory f) {
        DSRequest request = removeRequest(f);
        if (request != null) {
            switch (request.getOperationType()) {
                case FETCH:
                    fetchResponse(request, f);
                    break;
                case ADD:
                case UPDATE:
                    updateResponse(request, f, true);
                    break;
                case REMOVE:
                    updateResponse(request, f, false);
                    break;
            }
        }
    }

    private void fetchResponse(DSRequest request, Factory f) {
        DSResponse response = SmartPortlet.createResponse(request, f);
        Record[] list = new Record[f.data.size()];
        for (int i = 0; i < list.length; i++) {
            list[i] = TownDataSource.createRecord(f.data.get(i));
        }
        response.setData(list);
        dataSource.processResponse(request.getRequestId(), response);
    }

    private void updateResponse(DSRequest request, Factory f, boolean select) {
        DSResponse response = SmartPortlet.createResponse(request);
        Record[] list = new Record[1];
        list[0] = TownDataSource.createRecord(f.record);
        response.setData(list);

        dataSource.processResponse(request.getRequestId(), response);
        if (select) {
            cEditor.selectSingleRecord(list[0]);
        }
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    public static class Factory extends SmartPortletFactory<CompoundEditorPortlet> {
        @DoNotSendToServer
        public List<TownRecord> data;

        public TownRecord record;

        public Factory() {
        }

        public Factory(CompoundEditorPortlet p) {
            super(p);
        }

        public CompoundEditorPortlet createWidget() {
            return new CompoundEditorPortlet();
        }

        @Override
        public void refresh(CompoundEditorPortlet p) {
            super.refresh(p);
            p.restore(this);
        }

        @Override
        public void executeAdd(SmartPortletDataSource dataSource, DSRequest request) {
            super.executeAdd(dataSource, request);
            updateSelectedRecord(request);
        }

        @Override
        public void executeUpdate(SmartPortletDataSource dataSource, DSRequest request) {
            super.executeUpdate(dataSource, request);
            updateSelectedRecord(request);
        }

        @Override
        public void executeRemove(SmartPortletDataSource dataSource, DSRequest request) {
            super.executeRemove(dataSource, request);
            updateSelectedRecord(request);
        }

        private void updateSelectedRecord(DSRequest request) {
            JavaScriptObject data = request.getData();
            final ListGridRecord rec = new ListGridRecord(data);
            record = TownDataSource.create(rec);
        }
    }

    private static class CompoundEditor extends HLayout {
        private DataSource datasource;
        private DynamicForm form;
        private ListGrid grid;
        private IButton saveButton;
        private IButton newButton;
        private IButton removeButton;

        private CompoundEditor(DataSource datasource) {
            this.datasource = datasource;
        }

        protected void onInit() {
            super.onInit();
            this.form = new DynamicForm();

            form.setDataSource(datasource);

            saveButton = new IButton("Update");
            saveButton.setLayoutAlign(Alignment.CENTER);
            saveButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    form.setSaveOperationType(DSOperationType.UPDATE);
                    form.saveData();
                }
            });
            newButton = new IButton("New");
            newButton.setLayoutAlign(Alignment.CENTER);
            newButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    form.setSaveOperationType(DSOperationType.ADD);
                    form.saveData();
                }
            });
            removeButton = new IButton("Remove");
            removeButton.setLayoutAlign(Alignment.CENTER);
            removeButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    form.setSaveOperationType(DSOperationType.REMOVE);
                    form.saveData();
                }
            });

            VLayout editorLayout = new VLayout(5);
            editorLayout.addMember(form);
            editorLayout.addMember(saveButton);
            editorLayout.addMember(newButton);
            editorLayout.addMember(removeButton);
            editorLayout.setWidth(280);

            grid = new ListGrid();
            grid.setWidth(360);
            grid.setHeight(200);
            grid.setDataSource(datasource);
            grid.setAlternateRecordStyles(true);
            grid.setAutoFetchData(true);
            grid.setShowFilterEditor(true);
            grid.setSelectionType(SelectionStyle.SINGLE);
            grid.addSelectionChangedHandler(new SelectionChangedHandler() {
                public void onSelectionChanged(SelectionEvent selectionEvent) {
                    form.clearErrors(true);
                    form.editRecord(selectionEvent.getRecord());
                    saveButton.enable();
                }
            });
            ListGridField idField = new ListGridField("id", "Id", 30);
            idField.setAlign(Alignment.CENTER);
            ListGridField nameField = new ListGridField("name", "Name", 210);
            ListGridField dateField = new ListGridField("date", "Date", 80);
            grid.setFields(idField, nameField, dateField);

            addMember(grid);
            addMember(editorLayout);
        }


        public void setDatasource(DataSource datasource) {
            this.datasource = datasource;
            if (grid != null) {
                grid.setDataSource(datasource);
                form.setDataSource(datasource);
                saveButton.disable();
                grid.fetchData();
            }
        }

        public void selectSingleRecord(Record r) {
            grid.selectSingleRecord(r);
        }
    }
}
