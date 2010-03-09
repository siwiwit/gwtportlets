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

/**
 * A DTO to represent advanced filter types.
 */
public enum CriteriaTypeDto implements IsSerializable {
	ALL_EQUAL, //TODO: Implement usage for this filter
	BETWEEN,
	EQUAL,
	GREATER_THAN_OR_EQUAL,
	GREATER_THAN,
    LESS_THAN_OR_EQUAL,
    LESS_THAN,
	IN, //TODO: Implement usage for this filter
	IS_EMPTY, //TODO: Implement usage for this filter
	IS_NULL,

    // Logical operators
    AND,
	OR,
    NOT,

    // Sub string comparisons
    STARTS_WITH,
	ENDS_WITH,
    CONTAINS,

    // Field comparisons
	EQUAL_FIELD
}

