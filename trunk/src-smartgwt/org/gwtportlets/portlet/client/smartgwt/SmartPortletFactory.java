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

package org.gwtportlets.portlet.client.smartgwt;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import org.gwtportlets.portlet.client.ui.Portlet;
import org.gwtportlets.portlet.client.ui.PortletFactory;

/**
 * The portlet factory which must be used in Smart Portlets.
 *
 * @param <T> The portlet class.
 * @author Carl Crous
 */
public abstract class SmartPortletFactory<T extends SmartPortlet> extends PortletFactory<T> {
    /** The operation type for a fetch request. */
    public static final int OP_FETCH = 1;
    /** The operation type for an add request. */
    public static final int OP_ADD = 2;
    /** The operation type for an update request. */
    public static final int OP_UPDATE = 3;
    /** The operation type for a remove request. */
    public static final int OP_REMOVE = 4;

    /** The unique identifier for the current request which this portlet factory is handling. */
    protected String requestId;
    /** The operation type for the current request. */
    protected int operationType;
    /** The requested starting row (including) for a FETCH request. */
    protected int startRow;
    /** The requested ending row (excluding) for a FETCH request. */
    protected int endRow;
    /**
     * The total number of rows which can be queried.
     * This must be set by the Portlet Data Provider in a FETCH response.
     */
    protected int totalRows;
    /** The sort specifiers which must be used to sort the data for a FETCH request. */
    protected SortSpecifierDto sort[];
    /** The advanced filtering criteria. This can be if client side filtering is also performed. */
    protected CriteriaDto criteria;

    protected SmartPortletFactory() {
        super();
    }

    protected SmartPortletFactory(Portlet p) {
        super(p);
    }

    /**
     * Initialises the data in this factory for a FETCH request.
     * @param p The portlet which generated the request.
     * @param request The request details.
     */
    public void executeFetch(SmartPortletDataSource dataSource, DSRequest request) {
        operationType = OP_FETCH;

        startRow = request.getStartRow();
        endRow = request.getEndRow();
        this.sort = SortSpecifierUtil.createSortSpecifierDtoArray(request);
        Criteria criteria = request.getCriteria();
        if (criteria != null) {
            this.criteria = CriteriaUtil.createCriteriaDto(dataSource, criteria.getJsObj());
        }
    }

    /**
     * Initialises the data in this factory for an ADD request.
     * @param dataSource The data source which the request is for.
     * @param request The request details.
     */
    public void executeAdd(SmartPortletDataSource dataSource, DSRequest request) {
        operationType = OP_ADD;
    }

    /**
     * Initialises the data in this factory for an UPDATE request.
     * @param dataSource The data source which the request is for.
     * @param request The request details.
     */
    public void executeUpdate(SmartPortletDataSource dataSource, DSRequest request) {
        operationType = OP_UPDATE;
    }

    /**
     * Initialises the data in this factory for a REMOVE request.
     * @param dataSource The data source which the request is for.
     * @param request The request details.
     */
    public void executeRemove(SmartPortletDataSource dataSource, DSRequest request) {
        operationType = OP_REMOVE;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public SortSpecifierDto[] getSort() {
        return sort;
    }

    public void setSort(SortSpecifierDto[] sort) {
        this.sort = sort;
    }

    public CriteriaDto getCriteria() {
        return criteria;
    }

    public void setCriteria(CriteriaDto criteria) {
        this.criteria = criteria;
    }
}
