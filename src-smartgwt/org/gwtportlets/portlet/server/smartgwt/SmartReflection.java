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

import org.gwtportlets.portlet.client.smartgwt.CriteriaUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides methods to get object values using reflection.
 * Use SmartReflection.instance(T.class) to get an instance.
 * @author Carl Crous
 */
public class SmartReflection<T> {
    private static Map<Class, SmartReflection> instanceMap;
    private static final DateFormat dateFormat = new SimpleDateFormat(CriteriaUtil.DATE_FORMAT);

    private Map<String,Field> fieldMap;
    private Map<String,Method> fieldGetterMap;

    /**
     * Provides an instance for a given class.
     * Instances of the same class will be the same object.
     * @param cls The class to get an instance for.
     * @param <T> The generic type of the given class.
     * @return The reflection instance.
     */
    public static <T> SmartReflection instance(Class<T> cls) {
        if (instanceMap == null) {
            instanceMap = new HashMap<Class, SmartReflection>();
        }
        SmartReflection<T> instance = instanceMap.get(cls);
        if (instance == null) {
            instance = new SmartReflection<T>();
            instanceMap.put(cls, instance);
        }
        return instance;
    }

    /**
     * The private constructor to prevent external instances.
     */
    private SmartReflection() {
        fieldMap = new HashMap<String, Field>();
        fieldGetterMap = new HashMap<String, Method>();
    }

    /**
     * Gets a comparable field value from an object.
     * @param obj The object to get the value for.
     * @param fieldName The field name of the object.
     * @return Returns the object's field value using it directly or from its getter method if the field is private.
     * @throws SmartReflectionException
     */
    public Comparable getFieldValue(T obj, String fieldName) throws SmartReflectionException {
        Class<T> cls = (Class<T>)obj.getClass();
        Field field = fieldMap.get(fieldName);
        Method method = fieldGetterMap.get(fieldName);

        Class fieldCls = null;
        Object fieldValue = null;

        if (field == null && method != null) {
            field = getPublicField(cls, fieldName);
        }
        if (field != null) {
            fieldCls = field.getClass();
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new SmartReflectionException("Could not access field \"" + fieldName + "\" even after checking its public.", e);
            }
        } else {
            // Use a getter method
            if (method == null) {
                method = getPublicFieldGetter(cls, fieldName);
            }
            if (method == null) {
                throw new SmartReflectionException("Could not access the field: \"" + fieldName + "\" or its getter");
            }
            fieldCls = method.getReturnType();
            try {
                fieldValue = method.invoke(obj);
            } catch (IllegalAccessException e) {
                throw new SmartReflectionException("Could not invoke method \"" + method.getName() + "\" even after checking its public.", e);
            } catch (InvocationTargetException e) {
                throw new SmartReflectionException("Could not invoke method \"" + method.getName() + "\"", e);
            }
        }
        if (fieldValue != null && !(fieldValue instanceof Comparable)) {
            throw new SmartReflectionException("The field \"" + fieldName + "\" with value \"" + fieldValue +
                    "\" is of type " + fieldCls.getName() + " is not an instance of Comparable");
        }
        return (Comparable)fieldValue;
    }

    /**
     * Parses a string for a class's field to the same type as the field.
     * @param cls The class which to check the field.
     * @param fieldName The field's name.
     * @param fieldStringValue The field's value as a string.
     * @return The field's value parsed as the same type as the field.
     * @throws SmartReflectionException
     */
    public Comparable parseFieldValue(Class<T> cls, String fieldName, String fieldStringValue) throws SmartReflectionException {
        Field field = fieldMap.get(fieldName);
        Method method = fieldGetterMap.get(fieldName);

        Class fieldCls = null;
        if (field == null && method != null) {
            field = getPublicField(cls, fieldName);
        }
        if (field != null) {
            fieldCls = field.getClass();
        } else {
            // Use a getter method
            if (method == null) {
                method = getPublicFieldGetter(cls, fieldName);
            }
            if (method == null) {
                throw new SmartReflectionException("Could not access the field: \"" + fieldName + "\" or its getter");
            }
            fieldCls = method.getReturnType();
        }

        if (fieldCls.equals(String.class)) {
            return fieldStringValue;
        }
        if (fieldCls.equals(Integer.class) || fieldCls.equals(Integer.TYPE)) {
            return Integer.valueOf(fieldStringValue);
        }
        if (fieldCls.equals(Double.class) || fieldCls.equals(Double.TYPE)) {
            return Double.valueOf(fieldStringValue);
        }
        if (fieldCls.equals(Float.class) || fieldCls.equals(Float.TYPE)) {
            return Float.valueOf(fieldStringValue);
        }
        if (fieldCls.equals(Date.class)) {
            try {
                return dateFormat.parse(fieldStringValue);
            } catch (ParseException e) {
                throw new SmartReflectionException("Could not parse date \"" + fieldStringValue + "\"", e);
            }
        }
        throw new SmartReflectionException("The type of field \"" + fieldName + "\" (" + fieldCls.getName() +
                ") has not been implemented");
    }

    /**
     * Gets a public reflection field of a class.
     * @param cls The class.
     * @param fieldName The field's name.
     * @return The field or null if it is not public or doesn't exist.
     */
    public Field getPublicField(Class<T> cls, String fieldName) {
        Field field = fieldMap.get(fieldName);
        if (field != null) {
            return field;
        }
        try {
            field = cls.getField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
        if (field != null && !Modifier.isPublic(field.getModifiers())) {
            return null;
        }
        fieldMap.put(fieldName, field);
        return field;
    }

    /**
     * Gets a public reflection method for a getter of a class.
     * @param cls The class.
     * @param fieldName The name of the field to get the getter of.
     * @return The field's getter method or null if it is not public or doesn't exist.
     */
    public Method getPublicFieldGetter(Class<T> cls, String fieldName) {
        Method method = fieldGetterMap.get(fieldName);
        if (method != null) {
            return method;
        }
        String fieldGetterName = "get" + Character.toUpperCase(fieldName.charAt(0));
        if (fieldName.length() > 1) {
            fieldGetterName += fieldName.substring(1);
        }
        try {
            method = cls.getMethod(fieldGetterName);
        } catch (NoSuchMethodException e) {
            return null;
        }
        if (method != null && !Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        fieldGetterMap.put(fieldName, method);
        return method;
    }

    /**
     * The exception type for reflection errors.
     */
    public static class SmartReflectionException extends SmartException {
        public SmartReflectionException() {
        }

        public SmartReflectionException(String message) {
            super(message);
        }

        public SmartReflectionException(String message, Throwable cause) {
            super(message, cause);
        }

        public SmartReflectionException(Throwable cause) {
            super(cause);
        }
    }
}
