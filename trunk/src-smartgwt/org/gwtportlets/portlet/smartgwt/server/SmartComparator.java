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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gwtportlets.portlet.smartgwt.client.SortSpecifierDto;

import java.util.Comparator;

/**
 * Implements a comparator for a class which implements a Field Comparable.
 * @param <T> The class which is to be compared.
 * @author Carl Crous
 */
public class SmartComparator<T> implements Comparator<T> {
    private static final Log log = LogFactory.getLog(SmartComparator.class);
    private SortSpecifierDto[] sortArray;
    private SmartReflection<T> reflection;

    /**
     * Constructs the comparator from a sort specifier array.
     * @param cls The class which will be sorted.
     * @param sortArray The sort specification to sort by.
     */
    public SmartComparator(Class cls, SortSpecifierDto[] sortArray) {
        this.sortArray = sortArray;
        reflection = SmartReflection.instance(cls);
    }

    /**
     * Compares two Field Comparable objects.
     * @param o1 The one object.
     * @param o2 The other object.
     * @return The comparison.
     */
    public int compare(T o1, T o2) {
        for (SortSpecifierDto sort : sortArray) {
            Comparable v1, v2;
            try {
                v1 = reflection.getFieldValue(o1, sort.field);
                v2 = reflection.getFieldValue(o2, sort.field);
            } catch (SmartReflection.SmartReflectionException e) {
                log.error("Could not values for field \"" + sort.field + "\" for sorting comparison", e);
                return 0;
            }
            int value;
            if (v1 == null && v2 == null) {
                continue;
            }
            if (v1 == null) {
                value = -1;
            } else if (v2 == null) {
                value = 1;
            } else {
                value = v1.compareTo(v2);
            }
            if (!sort.isAscending) {
                value = -value;
            }
            if (value != 0) {
                return value;
            }
        }
        return 0;
    }
}
