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

package org.gwtportlets.portlet.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Extra information associated with a WidgetFactory.
 */
@Target(ElementType.TYPE)
@Documented
public @interface WidgetInfo {

    /**
     * The name. Defaults to a name derived from the simple name of the factory
     * class with any 'Portlet.Factory' or '.Factory' suffix removed.
     */
    String name() default "";

    /**
     * The category. Defaults to the last part of the package containing the
     * factory class with the first character capitalized.
     */
    String category() default "";

    /**
     * Short description (no default).
     */
    String description() default "";

}
