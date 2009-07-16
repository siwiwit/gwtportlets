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

package org.gwtportlets.portlet.client.edit;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasFocus;
import org.gwtportlets.portlet.client.layout.LDOM;
import org.gwtportlets.portlet.client.ui.LayoutPanel;
import org.gwtportlets.portlet.client.util.FormBuilder;
import org.gwtportlets.portlet.client.util.Rectangle;

/**
 * Dialog to edit settings for a LayoutPanel.
 */
public class LayoutPanelDialog extends PageEditorDialog {

    private LayoutPanel panel;
    private LayoutPanel.Factory original;

    private CheckBox limitMaximize = new CheckBox("Limit size of maximizing child widgets");

    public LayoutPanelDialog(final LayoutPanel panel) {
        super(false, true);
        this.panel = panel;
        this.original = (LayoutPanel.Factory)panel.createWidgetFactory();

        setText("Edit Container");

        updateControls();

        limitMaximize.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                updatePanel();
            }
        });

        FormBuilder b = new FormBuilder();
        b.field(limitMaximize).endRow();

        addButton(createRevertButton());
        addButton(createCloseButton());

        setWidget(b.getForm());
    }

    protected void revert() {
        super.revert();
        original.refreshSettings(panel);
        updateControls();
    }

    /**
     * Position the dialog somewhere reasonable and show it.
     */
    public void display() {
        setVisible(false);
        show();
        Rectangle r = LDOM.getNextToPosition(this, panel.getElement(), true, 4);
        setPopupPosition(r.x, r.y);
        setVisible(true);
    }

    protected HasFocus getFirstFocusWidget() {
        return limitMaximize;
    }

    private void updateControls() {
        limitMaximize.setValue(panel.isLimitMaximize());
    }

    private void updatePanel() {
        setDirty(true);
        panel.setLimitMaximize(limitMaximize.getValue());
        fireChange();
    }

}