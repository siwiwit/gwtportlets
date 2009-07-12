/*
 * GWT Portlets Framework (http://www.gwtportlets.org/)
 * Copyright 2009 Business Systems Group (Africa)
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

package org.gwtportlets.portlet.rebind;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.user.rebind.SourceWriter;
import org.gwtportlets.portlet.client.*;
import org.gwtportlets.portlet.client.layout.ContainerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates WidgetFactoryHelperImpl implementation of WidgetFactoryHelper.
 */
public class WidgetFactoryHelperCreator {

    private final TreeLogger logger;
    private final TypeOracle typeOracle;
    private final SourceWriter sw;

    private final JClassType widgetFactory;
    private final JClassType containerFactory;
    private final Map<JClassType, Integer> widgetFactoryMap =
            new LinkedHashMap<JClassType, Integer>();

    private static final String CLEAR_DO_NOT_SEND_TO_SERVER_FIELDS =
            "clearDoNotSendToServerFields";

    private static final String WIDGET_FACTORY = WidgetFactory.class.getName();
    private static final String CONTAINER_FACTORY = ContainerFactory.class.getName();
    private static final String WIDGET_FACTORY_META_DATA =
            WidgetFactoryMetaData.class.getName();

    public WidgetFactoryHelperCreator(TreeLogger logger, TypeOracle typeOracle,
            SourceWriter sw) throws NotFoundException {
        this.logger = logger;
        this.typeOracle = typeOracle;
        this.sw = sw;

        widgetFactory = typeOracle.getType(WIDGET_FACTORY);
        containerFactory = typeOracle.getType(CONTAINER_FACTORY);

        int c = 0;
        for (JClassType t : typeOracle.getTypes()) {
            if (t.isClass() != null && !t.isAbstract()
                    && t.isDefaultInstantiable()
                    && t.isStatic()
                    && implementsWidgetFactory(t)) {
                widgetFactoryMap.put(t, c++);
            }
        }
    }

    private boolean implementsWidgetFactory(JClassType t) {
        for (JClassType i : t.getImplementedInterfaces()) {
            if (i == widgetFactory || i instanceof JParameterizedType
                    && ((JParameterizedType)i).getBaseType() == widgetFactory) {
                return true;
            }
        }
        JClassType s = t.getSuperclass();
        return s != null && s != typeOracle.getJavaLangObject()
                && implementsWidgetFactory(s);
    }

    public void generate() throws UnableToCompleteException {
        sw.indent();
        sw.println();
        printWidgetFactoryMap();
        printWidgetFactoryList();
        printClearDoNotSendToServerFields();
        printGetWidgetFactoryList();
        printCreateWidgetFactory();
        sw.commit(logger);
    }

    private void printWidgetFactoryMap() {
        sw.println("private static final java.util.HashMap<Class, Integer> " +
                "WIDGET_FACTORY_ID_MAP = new java.util.HashMap<Class, Integer>();");
        sw.println("static {");
        sw.indent();
        for (JClassType t : widgetFactoryMap.keySet()) {
            sw.println("WIDGET_FACTORY_ID_MAP.put(" + t.getQualifiedSourceName() + ".class, " +
                    widgetFactoryMap.get(t) + ");");    
        }
        sw.outdent();
        sw.println("}");
        sw.println();
        sw.println("private int getId(Object o) {");
        sw.indent();
        sw.println("Integer i = WIDGET_FACTORY_ID_MAP.get(o.getClass());");
        sw.println("return i == null ? -1 : i.intValue();");
        sw.outdent();
        sw.println("}");
        sw.println();
    }

    private void printWidgetFactoryList() {
        sw.println("private static final " + WIDGET_FACTORY_META_DATA +
                "[] WIDGET_FACTORY_LIST = new " + WIDGET_FACTORY_META_DATA +
                "[]{");
        sw.indent();
        for (JClassType t : widgetFactoryMap.keySet()) {
            WidgetInfo pmd = t.getAnnotation(WidgetInfo.class);
            StringBuffer s = new StringBuffer();
            s.append("new ").append(WIDGET_FACTORY_META_DATA).append("(");
            s.append(widgetFactoryMap.get(t)).append(", ");
            s.append('"').append(getWidgetFactoryName(t, pmd)).append("\", ");
            s.append('"').append(getWidgetFactoryCategory(t, pmd)).append("\", ");
            s.append('"').append(getWidgetFactoryDescription(t, pmd)).append("\", ");
            s.append(isContainer(t)).append(", ");
            s.append(t.getQualifiedSourceName()).append(".class),");
            sw.println(s.toString());
        }
        sw.outdent();
        sw.println("};");
        sw.println();
    }

    private String getWidgetFactoryName(JClassType t, WidgetInfo pmd) {
        String s = pmd == null ? "" : pmd.name();
        if (s.length() > 0) {
            return s;
        }
        s = t.getName();
        if (s.endsWith("Portlet.Factory")) {
            s = s.substring(0, s.length() - 15);
        } else if (s.endsWith(".Factory")) {
            s = s.substring(0, s.length() - 8);
        }
        return insertSpaces(s);
    }

    private String getWidgetFactoryCategory(JClassType t, WidgetInfo pmd) {
        String s = pmd == null ? "" : pmd.category();
        if (s.length() > 0) {
            return s;
        }
        s = t.getPackage().getName();
        if (s.endsWith(".client.ui")) {
            s = s.substring(0, s.length() - 10);
        }
        int j = s.lastIndexOf('.');
        if (j >= 0) {
            s = s.substring(j + 1);
        }
        try {
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        } catch (IndexOutOfBoundsException e) {
            return s;
        }
    }

    private String getWidgetFactoryDescription(JClassType t,
            WidgetInfo pmd) {
        return pmd == null ? "" : pmd.description();
    }

    private boolean isContainer(JClassType t) {
        JClassType o = typeOracle.getJavaLangObject();
        for (JClassType i = t.getSuperclass(); i != o; i = i.getSuperclass()) {
            if (i == containerFactory) {
                return true;
            }
        }
        return false;
    }

    private void printClearDoNotSendToServerFields() {
        sw.println("public void " + CLEAR_DO_NOT_SEND_TO_SERVER_FIELDS + "(" +
                WIDGET_FACTORY + " wf) {");
        sw.indent();
        sw.println("switch (getId(wf)) {");
        sw.indent();
        for (JClassType t : widgetFactoryMap.keySet()) {
            List<String> lines = new ArrayList<String>();
            findDoNotSendToServerFields(t, lines);
            boolean m = hasClearDoNotSendToServerFieldsMethod(t);
            if (m || !lines.isEmpty()) {
                printCase(t);
                if (m) {
                    sw.println("((" + t.getQualifiedSourceName() + ")wf)." +
                            CLEAR_DO_NOT_SEND_TO_SERVER_FIELDS + "();");
                }
                for (String line : lines) {
                    sw.println(line);
                }
                sw.println("break;");
                sw.outdent();
            }
        }
        sw.println("default:");
        sw.outdent();
        sw.println("};");
        sw.outdent();
        sw.println("}");
        sw.println();
    }

    private void printCase(JClassType t) {
        sw.println("case " + widgetFactoryMap.get(t) + ":");
        sw.indent();
    }

    private void findDoNotSendToServerFields(JClassType t, List<String> ans) {
        JClassType sup = t.getSuperclass();
        if (sup != typeOracle.getJavaLangObject()) {
            findDoNotSendToServerFields(sup, ans);
        }
        for (JField f : t.getFields()) {
            if (f.isPublic() && !f.isStatic() && !f.isTransient() && !f.isFinal()
                    && f.isAnnotationPresent(DoNotSendToServer.class)) {
                StringBuffer s = new StringBuffer();
                s.append("((").append(t.getQualifiedSourceName())
                        .append(")wf).").append(f.getName()).append(" = ");
                JType ft = f.getType();
                if (ft.isPrimitive() != null) {
                    if (ft == JPrimitiveType.BOOLEAN) {
                        s.append("false;");
                    } else {
                        s.append("0;");
                    }
                } else {
                    s.append("null;");
                }
                ans.add(s.toString());
            }
        }
    }

    private boolean hasClearDoNotSendToServerFieldsMethod(JClassType t) {
        JMethod m = t.findMethod(CLEAR_DO_NOT_SEND_TO_SERVER_FIELDS, new JType[0]);
        if (m != null) {
            return true;
        }
        JClassType sup = t.getSuperclass();
        return sup != typeOracle.getJavaLangObject()
                && hasClearDoNotSendToServerFieldsMethod(sup);
    }

    private void printGetWidgetFactoryList() {
        sw.println("public " + WIDGET_FACTORY_META_DATA + "[] getWidgetFactoryList() {");
        sw.indent();
        sw.println("return WIDGET_FACTORY_LIST;");
        sw.outdent();
        sw.println("}");
        sw.println();
    }

    private void printCreateWidgetFactory() {
        sw.println("public " + WIDGET_FACTORY + " createWidgetFactory(int index) {");
        sw.indent();
        sw.println("switch (index) {");
        sw.indent();
        for (JClassType t : widgetFactoryMap.keySet()) {
            sw.println("case " + widgetFactoryMap.get(t) + ": return new " +
                    t.getQualifiedSourceName() + "();");
        }
        sw.outdent();
        sw.println("};");
        sw.println("throw new IllegalArgumentException(\"Invalid index: \" + index + \"\");");
        sw.outdent();
        sw.println("}");
        sw.println();
    }

    /**
     * Insert a space before each new capital letter in s.
     */
    private String insertSpaces(String s) {
        int n = s.length();
        StringBuffer b = new StringBuffer(n + 3);
        boolean caps = true;
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                if (!caps) {
                    b.append(' ');
                    caps = true;
                }
            } else {
                caps =false;
            }
            b.append(c);
        }
        return b.toString();
    }

}