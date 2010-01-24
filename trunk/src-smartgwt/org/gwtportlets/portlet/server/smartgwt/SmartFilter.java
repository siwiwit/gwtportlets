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

package org.gwtportlets.portlet.server.smartgwt;

import org.gwtportlets.portlet.client.smartgwt.AdvancedCriteriaDto;
import org.gwtportlets.portlet.client.smartgwt.CriteriaDto;
import org.gwtportlets.portlet.client.smartgwt.CriteriaTypeDto;
import org.gwtportlets.portlet.client.smartgwt.SimpleCriteriaDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Provides methods to apply a SmartGwt Criteria to a Collection object.
 * This uses reflection to retrieve field values and will if the given field is private it will
 * use its appropriately named getter method.
 *
 * @author Carl Crous
 */
public class SmartFilter<T> {
    private SmartReflection<T> reflection;

    /**
     * A new smart filter for the given class. This can be reused for a number of different filters on the same class.
     * @param cls The class (same as the generic type).
     */
    public SmartFilter(Class cls) {
        reflection = SmartReflection.instance(cls);
    }

    /**
     * Filters a collection of objects.
     * @param c The criteria to use in the filter.
     * @param from The collection to filter from.
     * @return A new ArrayList of the filtered objects.
     * @throws SmartFilterException
     * @throws SmartReflection.SmartReflectionException
     */
    public List<T> filter(CriteriaDto c, Collection<T> from)
            throws SmartFilterException, SmartReflection.SmartReflectionException {
        List<T> to = new ArrayList<T>();
        return (List<T>)filter(c, from, to);
    }

    /**
     * Filters a collection of objects.
     * @param c The criteria to use in the filter.
     * @param from The collection to filter from.
     * @param to The collection to insert the filtered objects into.
     * @return The to collection.
     * @throws SmartFilterException
     * @throws SmartReflection.SmartReflectionException
     */
    public Collection<T> filter(CriteriaDto c, Collection<T> from, Collection<T> to)
            throws SmartFilterException, SmartReflection.SmartReflectionException {
        for (T obj : from) {
            if (equals(c, obj)) {
                to.add(obj);
            }
        }
        return to;
    }

    /**
     * Determines if an object matches the given criteria.
     * @param c The criteria to use.
     * @param obj The object to match.
     * @return If the object matches.
     * @throws SmartFilterException
     * @throws SmartReflection.SmartReflectionException
     */
    public boolean equals(CriteriaDto c, T obj)
            throws SmartFilterException, SmartReflection.SmartReflectionException {
        if (c instanceof AdvancedCriteriaDto) {
            return equals((AdvancedCriteriaDto)c, obj);
        }
        if (c instanceof SimpleCriteriaDto) {
            return equals((SimpleCriteriaDto)c, obj);
        }
        return false;
    }

    /**
     * Determines if an object matches the given advanced criteria.
     * @param c The criteria to use.
     * @param obj The object to match.
     * @return If the object matches.
     * @throws SmartFilterException
     * @throws SmartReflection.SmartReflectionException
     */
    public boolean equals(AdvancedCriteriaDto c, T obj)
            throws SmartFilterException, SmartReflection.SmartReflectionException {
        CriteriaDto arr[] = c.getCriteriaArray();
        switch (c.getType()) {
            case AND:
                for (CriteriaDto dto : arr) {
                    if (!equals(dto, obj)) {
                        return false;
                    }
                }
                return true;
            case OR:
                for (CriteriaDto dto : arr) {
                    if (equals(dto, obj)) {
                        return true;
                    }
                }
                return false;
            case NOT:
                return arr.length > 0 && !equals(arr[0], obj);
        }
        throw new SmartFilterException("Advanced operator " + c.getType() + " is not implemented");
    }

    /**
     * Determines if an object matches the given simple criteria.
     * @param c The criteria to use.
     * @param obj The object to match.
     * @return If the object matches.
     * @throws SmartFilterException
     * @throws SmartReflection.SmartReflectionException
     */
    public boolean equals(SimpleCriteriaDto c, T obj)
            throws SmartFilterException, SmartReflection.SmartReflectionException {
        String arr[] = c.getParameters();
        CriteriaTypeDto t = c.getType();
        if (arr.length == 1) {
            Comparable value = reflection.getFieldValue(obj, arr[0]);
            switch (t) {
                case IS_NULL:
                    return value == null;
                case IS_NOT_NULL:
                    return value != null;
            }
        } else if (arr.length == 2) {
            Comparable value = reflection.getFieldValue(obj, arr[0]);
            if (t == CriteriaTypeDto.EQUAL_FIELD || t == CriteriaTypeDto.NOT_EQUAL_FIELD) {
                Comparable valueComparison = reflection.getFieldValue(obj, arr[1]);
                boolean b = value == valueComparison ||
                        (value != null && valueComparison != null && value.compareTo(valueComparison) == 0);
                if (t == CriteriaTypeDto.EQUAL_FIELD) {
                    return b;
                }
                return !b;
            }

            Comparable valueComparison = reflection.parseFieldValue((Class<T>)obj.getClass(), arr[0], arr[1]);
            boolean not = false;
            boolean bothNotNull = value != null && valueComparison != null;
            boolean bothNull = value == null && valueComparison == null;
            switch (t) {
                case BETWEEN:
                    Comparable valueComparison2 = reflection.parseFieldValue((Class<T>)obj.getClass(), arr[0], arr[2]);
                    return bothNotNull && valueComparison2 != null &&
                            value.compareTo(valueComparison) >= 0 && value.compareTo(valueComparison2) <= 0;
                case NOT_EQUAL:
                    not = true;
                case EQUAL:
                    return not ^ (bothNull || (bothNotNull && value.compareTo(valueComparison) == 0));
                case LESS_THAN:
                    return bothNotNull && value.compareTo(valueComparison) < 0;
                case LESS_THAN_OR_EQUAL:
                    return bothNotNull && value.compareTo(valueComparison) <= 0;
                case GREATER_THAN:
                    return bothNotNull && value.compareTo(valueComparison) > 0;
                case GREATER_THAN_OR_EQUAL:
                    return bothNotNull && value.compareTo(valueComparison) >= 0;
            }
            String valueString = (value instanceof String) ?
                    (String)value : value.toString();
            String valueComparisonString = (valueComparison instanceof String) ?
                    (String)valueComparison : valueComparison.toString();
            switch (t) {
                case NOT_CONTAINS:
                    not = true;
                case CONTAINS:
                    return not ^ (bothNotNull && valueString.contains(valueComparisonString));
                case NOT_STARTS_WITH:
                    not = true;
                case STARTS_WITH:
                    return not ^ (bothNotNull && valueString.startsWith(valueComparisonString));
                case NOT_ENDS_WITH:
                    not = true;
                case ENDS_WITH:
                    return not ^ (bothNotNull && valueString.endsWith(valueComparisonString));
                case NOT_EQUAL_FIELD:
                    not = true;
                case EQUAL_FIELD:
                    return not ^ (bothNotNull && valueString.startsWith(valueComparisonString));
            }
        }
        //TODO: Implement usage for the filters: ALL_EQUAL, IN, IS_EMPTY, IS_NOT_EMPTY
        throw new SmartFilterException("Simple operator " + t + " with " + arr.length +
                " parameters is not implemented");
    }

    /**
     * The exception type used if there is a filtering error.
     */
    public static class SmartFilterException extends SmartException {
        public SmartFilterException() {
        }

        public SmartFilterException(String message) {
            super(message);
        }

        public SmartFilterException(String message, Throwable cause) {
            super(message, cause);
        }

        public SmartFilterException(Throwable cause) {
            super(cause);
        }
    }
}
