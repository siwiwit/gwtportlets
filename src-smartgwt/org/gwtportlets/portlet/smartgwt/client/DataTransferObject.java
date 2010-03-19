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

package org.gwtportlets.portlet.smartgwt.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.smartgwt.client.data.Record;

/**
 * Describes an object which is to be used to transfer data between the client and server.
 * @author Carl Crous
 */
public interface DataTransferObject extends IsSerializable {
    /**
     * Copies the record in the widget framework into this DTO.
     * @param from The record to copy into this DTO.
     */
    public void copyFromRecord(Record from);

    /**
     * Copies the data in the DTO into a record in the widget framework.
     * @param from The record to copy this DTO into. 
     */
    public void copyToRecord(Record to);
}
