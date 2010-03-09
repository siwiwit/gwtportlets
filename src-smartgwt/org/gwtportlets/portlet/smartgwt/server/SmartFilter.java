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

import org.gwtportlets.portlet.smartgwt.client.AdvancedCriteriaDto;
import org.gwtportlets.portlet.smartgwt.client.CriteriaDto;
import org.gwtportlets.portlet.smartgwt.client.CriteriaTypeDto;
import org.gwtportlets.portlet.smartgwt.client.SimpleCriteriaDto;

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
            if (t == CriteriaTypeDto.IS_NULL) {
                return value == null;
            }
        } else if (arr.length == 2) {
            Comparable value = reflection.getFieldValue(obj, arr[0]);
            if (t == CriteriaTypeDto.EQUAL_FIELD) {
                Comparable valueComparison = reflection.getFieldValue(obj, arr[1]);
                return value == valueComparison ||
                        (value != null && valueComparison != null && value.compareTo(valueComparison) == 0);
            }

            Comparable valueComparison = reflection.parseFieldValue((Class<T>)obj.getClass(), arr[0], arr[1]);
            boolean bothNotNull = value != null && valueComparison != null;
            boolean bothNull = value == null && valueComparison == null;
            switch (t) {
                case EQUAL:
                    return bothNull || (bothNotNull && value.compareTo(valueComparison) == 0);
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
                case CONTAINS:
                    return bothNotNull && valueString.contains(valueComparisonString);
                case STARTS_WITH:
                    return bothNotNull && valueString.startsWith(valueComparisonString);
                case ENDS_WITH:
                    return bothNotNull && valueString.endsWith(valueComparisonString);
            }
        } else if (arr.length == 3) {
            if (t == CriteriaTypeDto.BETWEEN) {
                Comparable value = reflection.getFieldValue(obj, arr[0]);
                Comparable valueComparison = reflection.parseFieldValue((Class<T>)obj.getClass(), arr[0], arr[1]);
                Comparable valueComparison2 = reflection.parseFieldValue((Class<T>)obj.getClass(), arr[0], arr[2]);
                return value != null && valueComparison != null && valueComparison2 != null &&
                        value.compareTo(valueComparison) >= 0 && value.compareTo(valueComparison2) <= 0;
            }
        }
        //TODO: Implement usage for the filters: ALL_EQUAL, IN, IS_EMPTY, IS_NOT_EMPTY
        throw new SmartFilterException("Simple operator " + t + " with " + arr.length +
                " parameters is not implemented");
    }

    public String getEqualsCriteriaValue(CriteriaDto c, String field) {
        SimpleCriteriaDto dto = getCriteriaForField(c, CriteriaTypeDto.EQUAL, field);
        if (dto != null) {
            String arr[] = dto.getParameters();
            if (arr.length == 2) {
                return arr[1];
            }
        }
        return null;
    }

    public SimpleCriteriaDto getCriteriaForField(CriteriaDto c, CriteriaTypeDto type, String field) {
        if (c instanceof AdvancedCriteriaDto) {
            return getCriteriaForField((AdvancedCriteriaDto)c, type, field);
        }
        if (c instanceof SimpleCriteriaDto) {
            return getCriteriaForField((SimpleCriteriaDto)c, type, field);
        }
        return null;
    }

    private SimpleCriteriaDto getCriteriaForField(SimpleCriteriaDto c, CriteriaTypeDto type, String field) {
        String arr[] = c.getParameters();
        if (c.getType() == type && arr.length > 0 && arr[0].equals(field)) {
            return c;
        }
        return null;
    }

    private SimpleCriteriaDto getCriteriaForField(AdvancedCriteriaDto c, CriteriaTypeDto type, String field) {
        CriteriaDto arr[] = c.getCriteriaArray();
        for (CriteriaDto anArr : arr) {
            SimpleCriteriaDto dto = getCriteriaForField(anArr, type, field);
            if (dto != null) {
                return dto;
            }
        }
        return null;
    }

    public void walk(CriteriaDto c, FilterWalker walker) throws SmartFilterException {
        walker.begin();
        walk(c, walker, null, 0, 0);
        walker.end();
    }

    public void walk(CriteriaDto c, FilterWalker walker, Object obj, int index, int length) throws SmartFilterException {
        walker.visit(obj, index, length, c.getType());
        if (c instanceof AdvancedCriteriaDto) {
            walk((AdvancedCriteriaDto)c, walker, obj, index, length);
        }
        if (c instanceof SimpleCriteriaDto) {
            walk((SimpleCriteriaDto)c, walker, obj, index, length);
        }
    }

    public void walk(AdvancedCriteriaDto c, FilterWalker walker, Object obj, int index, int length) throws SmartFilterException {
        CriteriaDto arr[] = c.getCriteriaArray();
        CriteriaTypeDto t = c.getType();
        Object childObj = walker.visitAdvancedBefore(t);
        for (int i = 0; i < arr.length; i++) {
            CriteriaDto dto = arr[i];
            walk(dto, walker, childObj, i, arr.length);
        }
        walker.visitAdvancedAfter(childObj, index, length, t);
    }

    public void walk(SimpleCriteriaDto c, FilterWalker walker, Object obj, int index, int length) throws SmartFilterException {
        CriteriaTypeDto t = c.getType();
        String arr[] = c.getParameters();
        switch (arr.length) {
            case 1:
                walker.visitUnary(obj, index, length, t, arr[0]);
                break;
            case 2:
                walker.visitBinary(obj, index, length, t, arr[0], arr[1]);
                break;
            case 3:
                walker.visitTrinary(obj, index, length, t, arr[0], arr[1], arr[2]);
                break;
        }
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

    public static interface FilterWalker<T> {
        public void begin();
        public T visitAdvancedBefore(CriteriaTypeDto type) throws SmartFilterException;
        public void visitAdvancedAfter(T obj, int index, int length, CriteriaTypeDto type) throws SmartFilterException;
        public void visit(T obj, int index, int length, CriteriaTypeDto type) throws SmartFilterException;
        public void visitUnary(T obj, int index, int length, CriteriaTypeDto type, String field) throws SmartFilterException;
        public void visitBinary(T obj, int index, int length, CriteriaTypeDto type, String field, String value) throws SmartFilterException;
        public void visitTrinary(T obj, int index, int length, CriteriaTypeDto type, String field, String value1, String value2) throws SmartFilterException;
        public void end();
    }

    public static class SqlFilterWalker implements FilterWalker<String> {
        public static final SqlFilterWalker MYSQL_FILTER_WALKER =
            new SqlFilterWalker("[", "]", "'", "'", "and", "or", "is null", "!", "=", "<", "<=", ">", ">=", "like", "%");

        public static final SqlFilterWalker APP_ENGINE_FILTER_WALKER =
            new SqlFilterWalker("", "", "'", "'", "&&", "||", "is null", "!", "==", "<", "<=", ">", ">=", "==", "");

        private StringBuilder sql;

        private final String fieldQuoteLeft;
        private final String fieldQuoteRight;
        private final String stringQuoteLeft;
        private final String stringQuoteRight;
        private final String andOp;
        private final String orOp;
        private final String notOp;
        private final String isNullOp;
        private final String equalsOp;
        private final String lessThanOp;
        private final String lessThanEqualsOp;
        private final String greaterThanOp;
        private final String greaterThanEqualsOp;
        private final String stringLikeOp;
        private final String stringWildCard;

        public SqlFilterWalker(String fieldQuoteLeft, String fieldQuoteRight, String stringQuoteLeft, String stringQuoteRight, String andOp, String orOp, String notOp, String nullOp, String equalsOp, String lessThanOp, String lessThanEqualsOp, String greaterThanOp, String greaterThanEqualsOp, String stringLikeOp, String stringWildCard) {
            this.fieldQuoteLeft = fieldQuoteLeft;
            this.fieldQuoteRight = fieldQuoteRight;
            this.stringQuoteLeft = stringQuoteLeft;
            this.stringQuoteRight = stringQuoteRight;
            this.andOp = andOp;
            this.orOp = orOp;
            this.notOp = notOp;
            this.isNullOp = nullOp;
            this.equalsOp = equalsOp;
            this.lessThanOp = lessThanOp;
            this.lessThanEqualsOp = lessThanEqualsOp;
            this.greaterThanOp = greaterThanOp;
            this.greaterThanEqualsOp = greaterThanEqualsOp;
            this.stringLikeOp = stringLikeOp;
            this.stringWildCard = stringWildCard;
        }

        public String toString() {
            return sql.toString();
        }

        public void begin() {
            sql = new StringBuilder();
        }

        public void end() {
        }

        public String visitAdvancedBefore(CriteriaTypeDto type) throws SmartFilterException {
            switch (type) {
                case AND:
                    sql.append("(");
                    return andOp;
                case OR:
                    sql.append("(");
                    return orOp;
                case NOT:
                    sql.append(notOp).append("(");
                    return notOp;
            }
            throw new SmartFilterException("Unsupported advanced filter type " + type);
        }

        public void visitAdvancedAfter(String str, int index, int length, CriteriaTypeDto type) throws SmartFilterException {
            sql.append(") ");
        }

        public void visit(String str, int index, int length, CriteriaTypeDto type) throws SmartFilterException {
            if (str != null && index > 0) {
                sql.append(" ").append(str).append(" ");
            }
        }

        public void visitUnary(String str, int index, int length, CriteriaTypeDto type,
                               String field) throws SmartFilterException {
            if (type == CriteriaTypeDto.IS_NULL) {
                sql.append("(").append(getQuotedField(field)).append(" ").append(isNullOp).append(")");
                return;
            }
            throw new SmartFilterException("Unsupported Unary operation type " + type);
        }

        public void visitBinary(String str, int index, int length, CriteriaTypeDto type,
                                String field, String value) throws SmartFilterException {
            sql.append(getQuotedField(field)).append(" ");
            switch (type) {
                case EQUAL:
                    sql.append(equalsOp).append(" ").append(getQuotedValue(value));
                    return;
                case LESS_THAN:
                    sql.append(lessThanOp).append(" ").append(getQuotedValue(value));
                    return;
                case LESS_THAN_OR_EQUAL:
                    sql.append(lessThanEqualsOp).append(" ").append(getQuotedValue(value));
                    return;
                case GREATER_THAN:
                    sql.append(greaterThanOp).append(" ").append(getQuotedValue(value));
                    return;
                case GREATER_THAN_OR_EQUAL:
                    sql.append(greaterThanEqualsOp).append(" ").append(getQuotedValue(value));
                    return;
                case EQUAL_FIELD:
                    sql.append(equalsOp).append(" ").append(getQuotedField(value));
                    return;
                case CONTAINS:
                    sql.append(stringLikeOp).append(" ").append(getQuotedValue(stringWildCard + value + stringWildCard));
                    return;
                case STARTS_WITH:
                    sql.append(stringLikeOp).append(" ").append(getQuotedValue(value + stringWildCard));
                    return;
                case ENDS_WITH:
                    sql.append(stringLikeOp).append(" ").append(getQuotedValue(stringWildCard + value));
                    return;
            }

            throw new SmartFilterException("Unsupported Binary operation type " + type);
        }

        public void visitTrinary(String str, int index, int length, CriteriaTypeDto type,
                                 String field, String value1, String value2) throws SmartFilterException {
            if (type == CriteriaTypeDto.BETWEEN) {
                sql.append("(").append(getQuotedField(field)).append(greaterThanEqualsOp).append(getQuotedValue(value1))
                        .append(" ").append(andOp).append(" ").append(getQuotedField(field)).append(greaterThanEqualsOp)
                        .append(getQuotedValue(value2)).append(")");
            }
            throw new SmartFilterException("Unsupported Trinary operation type " + type);
        }

        public String getQuotedField(String field) {
            return fieldQuoteLeft + field + fieldQuoteRight;
        }

        public String getQuotedValue(String value) {
            return stringQuoteLeft + value + stringQuoteRight;
        }
    }
}
