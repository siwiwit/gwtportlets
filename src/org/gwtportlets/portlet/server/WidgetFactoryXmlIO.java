/*
 * GWT Portlets Framework (http://code.google.com/p/gwtportlets/)
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

package org.gwtportlets.portlet.server;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import org.gwtportlets.portlet.client.DoNotPersist;
import org.gwtportlets.portlet.client.DoNotSendToServer;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.layout.DeckLayout;
import org.gwtportlets.portlet.client.layout.RowLayout;
import org.gwtportlets.portlet.client.ui.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

/**
 * Uses XStream to convert WidgetFactory's to/from XML. Add aliases and so
 * on to customize the XML output.
 */
public class WidgetFactoryXmlIO {

    protected final XStream xs = createXStream();

    private static final String FACTORY = "Factory";

    public WidgetFactoryXmlIO() {
        xs.useAttributeFor(Integer.TYPE);
        xs.useAttributeFor(Float.TYPE);
        xs.useAttributeFor(Double.TYPE);
        xs.useAttributeFor(Boolean.TYPE);
        xs.useAttributeFor(String.class);

        alias(RowLayout.Constraints.class);
        alias(RowLayout.Factory.class);
        alias(DeckLayout.Factory.class);

        alias(LayoutPanel.Factory.class);
        alias(TitlePanel.Factory.class);
        alias(SectionPanel.Factory.class);
        alias(OverlayPanel.Factory.class);
        alias(RefreshPanel.Factory.class);
        alias(FixedSizePanel.Factory.class);
        alias(PlaceholderPortlet.Factory.class);
        alias(SpacerPortlet.Factory.class);
        alias(PagePortlet.Factory.class);
        alias(PageTitlePortlet.Factory.class);
        alias(WebAppContentPortlet.Factory.class);
        alias(MenuPortlet.Factory.class);
    }

    protected XStream createXStream() {
        return new XStream(new PureJavaReflectionProvider());
    }

    public XStream getXStream() {
        return xs;
    }

    /**
     * Serialize f to XML.
     */
    public String toXML(WidgetFactory f) {
        return xs.toXML(f);
    }

    /**
     * Serialize f to XML stream.
     */
    public void toXML(WidgetFactory f, OutputStream out) {
        xs.toXML(f, out);
    }

    /**
     * Deserialize f from XML stream.
     */
    public WidgetFactory fromXML(InputStream in) {
        return (WidgetFactory) xs.fromXML(in);
    }

    /**
     * Add an alias for cls. The alias is the simple name of the class (i.e.
     * without package) with the following modifications:<br>
     * <p/>
     * <li>Any 'Factory' suffix is removed
     * <li>Any '$' is replaced with '-' (inner class names)
     * <p/>
     * Also omits fields annotated with
     * {@link org.gwtportlets.portlet.client.DoNotPersist} or
     * {@link org.gwtportlets.portlet.client.DoNotSendToServer}.
     *
     * @see #omitDoNotPersistFields(Class)
     */
    public void alias(Class cls) {
        String s = cls.getName();
        int i = s.lastIndexOf('.') + 1;
        int j = s.length();
        if (s.endsWith(FACTORY)) {
            j -= FACTORY.length();
        }
        if (s.charAt(j - 1) == '$') {
            --j;
        }
        s = s.substring(i, j).replace("$", "-");
        xs.alias(s, cls);
        omitDoNotPersistFields(cls);
    }

    /**
     * Get xstream to omit all public fields of cls and its superclasses
     * that have been annoted with
     * {@link org.gwtportlets.portlet.client.DoNotPersist} or
     * {@link org.gwtportlets.portlet.client.DoNotSendToServer}.
     */
    public void omitDoNotPersistFields(Class cls) {
        Field[] a = cls.getFields();
        for (int i = 0; i < a.length; i++) {
            Field f = a[i];
            if (f.getAnnotation(DoNotPersist.class) != null
                    || f.getAnnotation(DoNotSendToServer.class) != null) {
                xs.omitField(f.getDeclaringClass(), f.getName());
            }
        }
    }

}
