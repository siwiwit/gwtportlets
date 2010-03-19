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

package smartgwt.server;

import org.apache.log4j.Logger;
import org.gwtportlets.portlet.smartgwt.server.SmartFilter;
import org.gwtportlets.portlet.smartgwt.server.SmartWidgetDataProvider;
import smartgwt.client.data.CountryDto;
import smartgwt.client.data.TownDto;
import smartgwt.client.ui.CompoundEditorPortlet;
import smartgwt.server.data.TownDpo;

/**
 * The data provider for the Compound Editor portlet demo.
 *
 * @author Carl Crous
 */
public class CompoundEditorDataProvider extends SmartWidgetDataProvider<CompoundEditorPortlet.Factory> {
    private static final Logger log = Logger.getLogger(CompoundEditorDataProvider.class);
    private static final SmartFilter<TownDto> filter = new SmartFilter<TownDto>(CountryDto.class);

    public CompoundEditorDataProvider() {
        addDataProviderObject(new TownDpo());
    }

    public Class getWidgetFactoryClass() {
        return CompoundEditorPortlet.Factory.class;
    }
}