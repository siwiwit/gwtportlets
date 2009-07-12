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

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.impl.WidgetFactoryHelper;

import java.io.PrintWriter;

/**
 * Generates WidgetFactoryHelperImpl to replace WidgetFactoryHelper.
 */
public class WidgetFactoryHelperGenerator extends Generator {

    public String generate(TreeLogger logger, GeneratorContext ctx,
            String typeName) throws UnableToCompleteException {
        if (!WidgetFactoryHelper.class.getName().equals(typeName)) {
            logger.log(TreeLogger.ERROR, "Expected '" +
                    WidgetFactory.class.getName() + "'", null);            
            throw new UnableToCompleteException();
        }

        TypeOracle typeOracle = ctx.getTypeOracle();
        JClassType type = typeOracle.findType(typeName);
        if (type == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
                    + typeName + "'", null);
            throw new UnableToCompleteException();
        }

        String packageName = type.getPackage().getName();
        String simpleName = type.getSimpleSourceName() + "Impl";
        ClassSourceFileComposerFactory composer =
                new ClassSourceFileComposerFactory(packageName, simpleName);
        composer.addImplementedInterface(type.getName());
        PrintWriter pw = ctx.tryCreate(logger, packageName, simpleName);
        if (pw != null) {
            SourceWriter sw = composer.createSourceWriter(ctx, pw);
            try {
                WidgetFactoryHelperCreator c = new WidgetFactoryHelperCreator(
                        logger, typeOracle, sw);
                c.generate();
            } catch (NotFoundException e) {
                logger.log(TreeLogger.ERROR, e.toString(), e);
                throw new UnableToCompleteException();
            }
        }

        return packageName + "." + simpleName;
    }

}
