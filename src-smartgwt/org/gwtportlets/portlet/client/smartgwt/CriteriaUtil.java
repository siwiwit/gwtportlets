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

    public static Map<String, CriteriaTypeDto> getOperatorMap() {
        if (operatorMap == null) {
            operatorMap = new HashMap<String, CriteriaTypeDto>();
            operatorMap.put("and", CriteriaTypeDto.AND);
            operatorMap.put("or", CriteriaTypeDto.OR);
            operatorMap.put("not", CriteriaTypeDto.NOT);
            operatorMap.put("equals", CriteriaTypeDto.EQUAL);
            operatorMap.put("notequals", CriteriaTypeDto.NOT_EQUAL);
            operatorMap.put("lessthan", CriteriaTypeDto.LESS_THAN);
            operatorMap.put("lessorequal", CriteriaTypeDto.LESS_THAN_OR_EQUAL);
            operatorMap.put("greaterthan", CriteriaTypeDto.GREATER_THAN);
            operatorMap.put("greaterorequal", CriteriaTypeDto.GREATER_THAN_OR_EQUAL);
            operatorMap.put("icontains", CriteriaTypeDto.CONTAINS);
            operatorMap.put("istartswith", CriteriaTypeDto.STARTS_WITH);
            operatorMap.put("iendswith", CriteriaTypeDto.ENDS_WITH);
            operatorMap.put("inotcontains", CriteriaTypeDto.NOT_CONTAINS);
            operatorMap.put("inotstartswith", CriteriaTypeDto.NOT_STARTS_WITH);
            operatorMap.put("inotendswith", CriteriaTypeDto.NOT_ENDS_WITH);
            operatorMap.put("isnull", CriteriaTypeDto.IS_NULL);
            operatorMap.put("notnull", CriteriaTypeDto.IS_NOT_NULL);
            operatorMap.put("equalsfield", CriteriaTypeDto.EQUAL_FIELD);
            operatorMap.put("notequalfield", CriteriaTypeDto.NOT_EQUAL_FIELD);
            operatorMap.put("between", CriteriaTypeDto.BETWEEN);
        }
        return operatorMap;
    }

    public static DateTimeFormat getDateFormat() {
        if (dateFormat == null) {
            dateFormat = DateTimeFormat.getFormat(DATE_FORMAT);
        }
        return dateFormat;
    }

    public static CriteriaDto createCriteriaDto(DataSource dataSource, JavaScriptObject jsObj) {
        String[] properties = JSOHelper.getProperties(jsObj);

        if (properties == null || properties.length == 0) {
            return null;
        }
        // If the attribute operator is present we have either an AdvancedCriteria object or a criteria item
        String operator = JSOHelper.getAttribute(jsObj, "operator");
        // The fields are listed in the properties
        if(operator == null) {
            List<SimpleCriteriaDto> list = new ArrayList<SimpleCriteriaDto>();
            for (String p : properties) {
                String v = getTypedValue(dataSource, jsObj, p, p);
                if (v != null) {
                    list.add(new SimpleCriteriaDto(CriteriaTypeDto.CONTAINS, p, v));
                }
            }
            SimpleCriteriaDto arr[] = new SimpleCriteriaDto[list.size()];
            for (int i = 0, arrLength = arr.length; i < arrLength; i++) {
                arr[i] = list.get(i);
            }
            return new AdvancedCriteriaDto(CriteriaTypeDto.AND, arr);
        }
        Map<String, CriteriaTypeDto> operatorMap = getOperatorMap();
        CriteriaTypeDto operatorTypeDto = operatorMap.get(operator.toLowerCase());
        if (operatorTypeDto == null) {
            return null;
            //TODO: Do something about exceptions
            //throw new Exception("bad operator (" + operator + ") for criteria: '" + properties + "'");
        }

        if ("AdvancedCriteria".equalsIgnoreCase(JSOHelper.getAttribute(jsObj, "_constructor"))) {
            // Read the AdvancedCriteria object
            if (properties.length < 3) {
                //throw new Exception("bad AdvancedCriteria format: '");// + properties + "'");
                return null;
            }

            JavaScriptObject[] criteria = JSOHelper.getAttributeAsJavaScriptObjectArray(jsObj, "criteria");

            if (criteria == null) {
                //throw new Exception("bad operands for AdvancedCriteria: '");// + properties + "'");
                return null;
            }

            CriteriaDto[] criteriaDtoArray = new CriteriaDto[criteria.length];
            for (int i = 0; i < criteriaDtoArray.length; i++) {
                criteriaDtoArray[i] = createCriteriaDto(dataSource, criteria[i]);
            }

            return new AdvancedCriteriaDto(operatorTypeDto, criteriaDtoArray);
        }

        String fieldName = JSOHelper.getAttribute(jsObj, "fieldName");
        if (fieldName == null) {
            return new SimpleCriteriaDto(CriteriaTypeDto.EQUAL, properties[0], JSOHelper.getAttribute(jsObj, properties[0]));
        }
        if (operatorTypeDto == CriteriaTypeDto.BETWEEN) {
            return new SimpleCriteriaDto(CriteriaTypeDto.BETWEEN, fieldName,
                    getTypedValue(dataSource, jsObj, fieldName, "start"),
                    getTypedValue(dataSource, jsObj, fieldName, "end"));
        }

        String value = getTypedValue(dataSource, jsObj, fieldName, "value");
        switch (operatorTypeDto) {
            case NOT_CONTAINS:
            case NOT_STARTS_WITH:
            case NOT_ENDS_WITH:
                return new AdvancedCriteriaDto(CriteriaTypeDto.NOT, new SimpleCriteriaDto(operatorTypeDto, fieldName, value));
            case IS_NULL:
            case IS_NOT_NULL:
                return new SimpleCriteriaDto(operatorTypeDto, fieldName);
            default:
                return new SimpleCriteriaDto(operatorTypeDto, fieldName, value);
        }
    }

    protected static String getTypedValue(DataSource dataSource, JavaScriptObject jsObj, String fieldName, String valueFieldName) {
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

//    public static String getJsoAttributes(JavaScriptObject jso) {
//        String arr[] = JSOHelper.getProperties(jso);
//        StringBuilder b = new StringBuilder();
//        for (String name : arr) {
//            b.append(name).append(" = ").append(JSOHelper.getProperties)
//        }
//    }
}
