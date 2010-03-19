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

import org.gwtportlets.portlet.smartgwt.client.DataTransferObject;
import org.gwtportlets.portlet.smartgwt.client.SmartPortletFactory;

import java.util.List;

/**
 * An interface to provide data for DTOs.
 * @author Carl Crous
 */
public interface DataProviderObject {
    /**
     * Returns the default data source id for this provider.
     * @return The Datsource Id
     */
    public String getDataSourceId();

    /**
     * Performs a fetch operation.
     *
     * The data in the portlet factory will be empty. The StartRow and EndRow can be used to implement
     * a limited fetch operation (you will have to set the TotalRows if these are used). The criteria and
     * sort specifier objects may also be provided to manipulate the data which should be fetched.
     * @param f The portlet factory which contains the details of the operation.
     * @return The list of DTOs which were fetched.
     */
    public List<DataTransferObject> fetchData(SmartPortletFactory f);

    /**
     * Performs an update operation.
     *
     * The first element in the data list will be the updated DTO. The second element will be the original
     * DTO before the update. This can be used to prevent concurrent writes.
     * @param f The portlet factory which contains the details of the operation.
     * @return The DTO which was updated or null if an error occurred.
     */
    public DataTransferObject updateData(SmartPortletFactory f);

    /**
     * Performs an add operation.
     *
     * The first (and only) element in the data list will be the DTO to add.
     * @param f The portlet factory which contains the details of the operation.
     * @return The new DTO which was added or null if an error occurred.
     */
    public DataTransferObject addData(SmartPortletFactory f);

    /**
     * Performs a delete operation.
     *
     * The first (and only) element in the data list will be the DTO to delete.
     * @param f The portlet factory which contains the details of the operation.
     * @return The DTO which was removed (optional) or null.
     */
    public DataTransferObject removeData(SmartPortletFactory f);
}
