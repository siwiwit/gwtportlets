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

package org.gwtportlets.portlet.smartgwt.server;

import org.gwtportlets.portlet.server.PageRequest;
import org.gwtportlets.portlet.server.WidgetDataProvider;
import org.gwtportlets.portlet.smartgwt.client.SmartPortletFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * The Data Provider which must be used for Smart Portlets.
 *
 * @param <T> The Smart Portlet Factory class
 */
public abstract class SmartWidgetDataProvider<T extends SmartPortletFactory> implements WidgetDataProvider<T> {
    private Map<String, DataProviderObject> map = new HashMap<String, DataProviderObject>();

    public void refresh(T f, PageRequest req) {
        switch (f.getOperationType()) {
            case SmartPortletFactory.OP_FETCH:
                executeFetch(f);
                break;
            case SmartPortletFactory.OP_ADD:
                executeAdd(f);
                break;
            case SmartPortletFactory.OP_UPDATE:
                executeUpdate(f);
                break;
            case SmartPortletFactory.OP_REMOVE:
                executeRemove(f);
                break;
        }
    }

    /**
     * Adds a data provider object to this data provider with the default data source id.
     *
     * Every data provider object will be called when a request with the mapped data source is received.
     * @param provider The provider to add.
     */
    public void addDataProviderObject(DataProviderObject provider) {
        addDataProviderObject(provider.getDataSourceId(), provider);
    }

    /**
     * Adds a data provider object to this data provider with the given data source id.
     *
     * Every data provider object will be called when a request with the mapped data source is received.
     * @param dataSourceId The data source id to map to the data provider object.
     * @param provider The provider to add.
     */
    public void addDataProviderObject(String dataSourceId, DataProviderObject provider) {
        map.put(dataSourceId, provider);
    }

    /**
     * Performs a data fetch operation.
     *
     * This method is called when the associated SmartPortlet sends a FETCH DSRequest.
     * The factory will have the following fields set: requestId, operationType, startRow
     * endRow, sort (optional) and criteria (optional). If you want to use paging, you
     * must set the totalRows to the total number of possible values for the current
     * request and return the entries from startRow (including) to endRow (excluding).
     *
     * @param f The portlet factory which is to transfer the data for the request.
     */
    public void executeFetch(T f) {
        String dataSourceId = f.getDataSourceId();
        if (dataSourceId != null) {
            DataProviderObject dataSourceProvider = map.get(dataSourceId);
            if (dataSourceProvider != null) {
                f.setData(dataSourceProvider.fetchData(f));
            }
        }
    }

    /**
     * Performs a data add operation.
     *
     * This method is called when the associated SmartPortlet sends an ADD DSRequest.
     * The portlet factory should set a field in the factory for the entry to add.
     * This method should add the given entry and return the added entry (including
     * any new or default values).
     *
     * @param f The portlet factory which is to transfer the data for the request.
     */
    public void executeAdd(T f) {
        String dataSourceId = f.getDataSourceId();
        if (dataSourceId != null) {
            DataProviderObject dataSourceProvider = map.get(dataSourceId);
            if (dataSourceProvider != null) {
                f.setSingleDto(dataSourceProvider.addData(f));
            }
        }
    }

    /**
     * Performs a data update operation.
     *
     * This method is called when the associated SmartPortlet sends an UPDATE DSRequest.
     * The portlet factory should set a field in the factory for the entry to update.
     * This method should update the given entry and return the updated entry.
     * The portlet factory could also provide the original entry (before edits) so that
     * this can be compared with the current entry to prevent data inconsistencies when
     * multiple sources attempt to update the same entry.
     * @param f The portlet factory which is to transfer the data for the request.
     */
    public void executeUpdate(T f) {
        String dataSourceId = f.getDataSourceId();
        if (dataSourceId != null) {
            DataProviderObject dataSourceProvider = map.get(dataSourceId);
            if (dataSourceProvider != null) {
                f.setSingleDto(dataSourceProvider.updateData(f));
            }
        }
    }

    /**
     * Performs a data remove operation.
     *
     * This method is called when the associated SmartPortlet sends a REMOVE DSRequest.
     * The portlet factory should set a field in the factory for the entry to remove.
     * This method should remove the given entry and optionally return the entry which
     * was removed. 
     * @param f The portlet factory which is to transfer the data for the request.
     */
    public void executeRemove(T f) {
        String dataSourceId = f.getDataSourceId();
        if (dataSourceId != null) {
            DataProviderObject dataSourceProvider = map.get(dataSourceId);
            if (dataSourceProvider != null) {
                f.setSingleDto(dataSourceProvider.removeData(f));
            }
        }
    }

}
