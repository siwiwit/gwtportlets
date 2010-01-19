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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A DTO to represent simple filters.
 *
 * Advanced filtering support was made possible with the use of Roberto Mozzicato's
 * GwtCriterion demo in the SmartGwt forums.
 * @see http://forums.smartclient.com/showthread.php?t=4814
 *
 * @author Carl Crous
 */
public class SimpleCriteriaDto extends CriteriaDto implements IsSerializable {

	protected String[] parameters;

    public SimpleCriteriaDto(){

    }
    
	public SimpleCriteriaDto(CriteriaTypeDto type, String... parameters) {
		this.type = type;
		this.parameters = parameters;
	}


    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }


}
