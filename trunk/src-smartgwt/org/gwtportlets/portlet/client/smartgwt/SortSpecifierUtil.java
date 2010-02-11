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

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.types.SortDirection;


/**
 * Util methods to create sort specifier objects.
 *
 * @author Carl Crous
 */
public class SortSpecifierUtil {

    public static SortSpecifierDto[] createSortSpecifierDtoArray(DSRequest request) {
        // getSortBy fails if there is only a single sorting field.
        SortSpecifier sort[];
        try {
            sort = request.getSortBy();
        } catch (Exception e) {
            String sortString = request.getAttribute("sortBy");
            return createSortSpecifierDtoArray(sortString);
        }
        return createSortSpecifierDtoArray(sort);
    }

    public static SortSpecifierDto[] createSortSpecifierDtoArray(SortSpecifier sort[]) {
        if (sort == null) {
            return null;
        }
        SortSpecifierDto dtoArray[] = new SortSpecifierDto[sort.length];
        for (int i = 0, sortLength = sort.length; i < sortLength; i++) {
            SortSpecifier s = sort[i];
            dtoArray[i] = new SortSpecifierDto(s.getField(), s.getSortDirection().equals(SortDirection.ASCENDING));
        }
        return dtoArray;
    }

    public static SortSpecifierDto[] createSortSpecifierDtoArray(String sort) {
        if (sort == null || sort.length() == 0) {
            return null;
        }
        SortSpecifierDto dtoArray[] = new SortSpecifierDto[1];
        boolean ascending = true;
        if (sort.charAt(0) == '-') {
            ascending = false;
            sort = sort.substring(1);
        }
        dtoArray[0] = new SortSpecifierDto(sort, ascending);
        return dtoArray;
    }
}
