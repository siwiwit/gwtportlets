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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.util.JSOHelper;

import java.util.*;

/**
 * Util methods to create filtering objects.
 *
 * @author Carl Crous
 */
public class CriteriaUtil {
    public static final String DATE_FORMAT = "yyyyMMddHHmmssZ";
    private static DateTimeFormat dateFormat;
    private static Map<String, CriteriaTypeDto> operatorMap;
    private static Map<String, String> notOperatorMap;

    public static Map<String, CriteriaTypeDto> getOperatorMap() {
        if (operatorMap == null) {
            operatorMap = new HashMap<String, CriteriaTypeDto>();
            operatorMap.put("and", CriteriaTypeDto.AND);
            operatorMap.put("or", CriteriaTypeDto.OR);
            operatorMap.put("not", CriteriaTypeDto.NOT);
            operatorMap.put("equals", CriteriaTypeDto.EQUAL);
            operatorMap.put("lessthan", CriteriaTypeDto.LESS_THAN);
            operatorMap.put("lessorequal", CriteriaTypeDto.LESS_THAN_OR_EQUAL);
            operatorMap.put("greaterthan", CriteriaTypeDto.GREATER_THAN);
            operatorMap.put("greaterorequal", CriteriaTypeDto.GREATER_THAN_OR_EQUAL);
            operatorMap.put("icontains", CriteriaTypeDto.CONTAINS);
            operatorMap.put("istartswith", CriteriaTypeDto.STARTS_WITH);
            operatorMap.put("iendswith", CriteriaTypeDto.ENDS_WITH);
            operatorMap.put("isnull", CriteriaTypeDto.IS_NULL);
            operatorMap.put("equalsfield", CriteriaTypeDto.EQUAL_FIELD);
            operatorMap.put("between", CriteriaTypeDto.BETWEEN);
        }
        return operatorMap;
    }

    public static Map<String, String> getNotOperatorMap() {
        if (notOperatorMap == null) {
            notOperatorMap = new HashMap<String, String>();
            notOperatorMap.put("notequal", "equals");
            notOperatorMap.put("inotcontains", "icontains");
            notOperatorMap.put("inotstartswith", "istartswith");
            notOperatorMap.put("inotendswith", "iendswith");
            notOperatorMap.put("notnull", "isnull");
            notOperatorMap.put("notequalfield", "equalsfield");
        }
        return notOperatorMap;
    }

    public static DateTimeFormat getDateFormat() {
        if (dateFormat == null) {
            dateFormat = DateTimeFormat.getFormat(DATE_FORMAT);
        }
        return dateFormat;
    }

    public static CriteriaDto createCriteriaDto(DataSource dataSource, JavaScriptObject jsObj) throws CriteriaException {
        String[] properties = JSOHelper.getProperties(jsObj);

        if (properties == null || properties.length == 0) {
            return null;
        }
        // If the attribute operator is present we have either an AdvancedCriteria object or a criteria item
        String operator = JSOHelper.getAttribute(jsObj, "operator");
        // The fields are listed in the properties
        if(operator == null) {
            List<CriteriaDto> list = new ArrayList<CriteriaDto>();
            for (String p : properties) {
                String v = getTypedValue(dataSource, jsObj, p, p);
                if (v != null) {
                    list.add(new SimpleCriteriaDto(CriteriaTypeDto.CONTAINS, p, v));
                }
            }
            return new AdvancedCriteriaDto(CriteriaTypeDto.AND, list);
        }
        operator = operator.toLowerCase();
        Map<String, CriteriaTypeDto> operatorMap = getOperatorMap();
        CriteriaTypeDto operatorTypeDto = operatorMap.get(operator);
        boolean isNot = false;
        if (operatorTypeDto == null) {
            isNot = true;
            Map<String, String> notOperatorMap = getNotOperatorMap();
            String newOperator = notOperatorMap.get(operator);
            if (newOperator != null) {
                operatorTypeDto = operatorMap.get(newOperator.toLowerCase());
            }
        }
        if (operatorTypeDto == null) {
            throw new CriteriaException("Unsupported operator " + operator);
        }

        if ("AdvancedCriteria".equalsIgnoreCase(JSOHelper.getAttribute(jsObj, "_constructor"))) {
            // Read the AdvancedCriteria object
            JavaScriptObject[] criteria = JSOHelper.getAttributeAsJavaScriptObjectArray(jsObj, "criteria");
            if (criteria == null) {
                throw new CriteriaException("No criteria provided for Advanced Criteria");
            }
            List<CriteriaDto> criteriaDtoArray = new ArrayList<CriteriaDto>(criteria.length);
            for (int i = 0; i < criteria.length; i++) {
                criteriaDtoArray.add(createCriteriaDto(dataSource, criteria[i]));
            }
            return new AdvancedCriteriaDto(operatorTypeDto, criteriaDtoArray);
        }

        SimpleCriteriaDto dto;
        String fieldName = JSOHelper.getAttribute(jsObj, "fieldName");
        if (fieldName == null) {
            dto = new SimpleCriteriaDto(CriteriaTypeDto.EQUAL, properties[0],
                    JSOHelper.getAttribute(jsObj, properties[0]));
        } else if (operatorTypeDto == CriteriaTypeDto.BETWEEN) {
            dto = new SimpleCriteriaDto(CriteriaTypeDto.BETWEEN, fieldName,
                    getTypedValue(dataSource, jsObj, fieldName, "start"),
                    getTypedValue(dataSource, jsObj, fieldName, "end"));
        } else if (operatorTypeDto == CriteriaTypeDto.IS_NULL) {
            dto = new SimpleCriteriaDto(operatorTypeDto, fieldName);
        } else {
            String value = getTypedValue(dataSource, jsObj, fieldName, "value");
            dto = new SimpleCriteriaDto(operatorTypeDto, fieldName, value);
        }
        if (isNot) {
            return new AdvancedCriteriaDto(CriteriaTypeDto.NOT, dto);
        }
        return dto;
    }

    protected static String getTypedValue(DataSource dataSource, JavaScriptObject jsObj, String fieldName,
                                          String valueFieldName) {
    	DataSourceField[] fields = dataSource.getFields();
    	for (DataSourceField field : fields) {
    		if(field.getName().equalsIgnoreCase(fieldName)) {
    			// TODO: Add here other cases when other types are required
    			switch(field.getType()) {
	    			case DATE:
	    				// TODO: at the moment JSOHelper.getAttributeAsDate is not always working,
	    				// we try to get it as a date, if this throws an exception then
	    				// we simply get it as an object and compare its runtime class against java.util.Date (thanks Alius!)
	    				Object object;
						try {
							object = JSOHelper.getAttributeAsDate(jsObj, valueFieldName);
						}
						catch (Exception e) {
							object = JSOHelper.getAttributeAsObject(jsObj, valueFieldName);
						}

	    				if(object instanceof Date) {
                            return getDateFormat().format((Date)object);
                        }
                        break;
                    default:
                        return JSOHelper.getAttribute(jsObj, valueFieldName);
	    		}
	    		return null;
    		}
		}
    	return null;
    }

    public static class CriteriaException extends Exception {
        public CriteriaException() {
        }

        public CriteriaException(String message) {
            super(message);
        }

        public CriteriaException(String message, Throwable cause) {
            super(message, cause);
        }

        public CriteriaException(Throwable cause) {
            super(cause);
        }
    }
}
