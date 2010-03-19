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

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * A DTO base type to represent simple and advanced filters.
 *
 * Advanced filtering support was made possible with the use of Roberto Mozzicato's
 * GwtCriterion demo in the SmartGwt forums.
 * @see http://forums.smartclient.com/showthread.php?t=4814
 *
 * @author Carl Crous
 */
public class CriteriaDto implements IsSerializable {
    protected CriteriaTypeDto type;

    public CriteriaDto() {
    }

    public CriteriaTypeDto getType() {
        return type;
    }

    public void setType(CriteriaTypeDto type) {
        this.type = type;
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        toString(b, "");
        return b.toString();
    }

    private void toString(StringBuilder b, String prefix) {
        b.append(prefix).append(type).append("\n");
        prefix += " ";
        if (this instanceof AdvancedCriteriaDto) {
            AdvancedCriteriaDto advancedCriteriaDto = (AdvancedCriteriaDto)this;
            for (CriteriaDto s : advancedCriteriaDto.criteriaList) {
                s.toString(b, prefix);
            }
        } else if (this instanceof SimpleCriteriaDto) {
            SimpleCriteriaDto simpleCriteriaDto = (SimpleCriteriaDto)this;
            if (simpleCriteriaDto.parameters.length > 0) {
                b.append(prefix).append(simpleCriteriaDto.parameters[0]).append(": ");
                for (int i = 1, parametersLength = simpleCriteriaDto.parameters.length; i < parametersLength; i++) {
                    b.append(simpleCriteriaDto.parameters[i]);
                    if (i+1 < parametersLength) {
                        b.append(", ");
                    }
                }
                b.append("\n");
            }
        } else {
            b.append(prefix).append(getClass().getName()).append(": ").append(toString()).append("\n");
        }
    }

}
