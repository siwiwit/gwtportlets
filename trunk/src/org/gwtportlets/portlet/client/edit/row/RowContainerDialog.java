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

package org.gwtportlets.portlet.client.edit.row;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.edit.PageEditorDialog;
import org.gwtportlets.portlet.client.layout.Container;
import org.gwtportlets.portlet.client.layout.LDOM;
import org.gwtportlets.portlet.client.layout.RowLayout;
import org.gwtportlets.portlet.client.util.FormBuilder;
import org.gwtportlets.portlet.client.util.Rectangle;

/**
 * Dialog to edit settings for a container with row layout.
 */
public class RowContainerDialog extends PageEditorDialog {

    private Container container;

    private CheckBox column = new CheckBox("Layout in column");
    private TextBox spacing = new TextBox();

    private final boolean originalColumn;
    private final int originalSpacing;

    public RowContainerDialog(final Container container) {
        super(false, true);
        this.container = container;

        final RowLayout layout = getContainerLayout();
        originalColumn = layout.isColumn();
        originalSpacing = layout.getSpacing();

        setText("Edit Row/Column");

        updateControls();

        spacing.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                updateContainer();
            }
        });

        column.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                updateContainer();
            }
        });

        spacing.setVisibleLength(6);

        column.setTitle("Layout widgets in a row or column");
        spacing.setTitle("Space between widgets in pixels");

        FormBuilder b = new FormBuilder();
        b.label("Spacing").field(spacing).endRow();
        b.field(column).colspan(2).endRow();

        addButton(createRevertButton());
        addButton(createCloseButton());

        setWidget(b.getForm());
    }

    protected void revert() {
        super.revert();
        RowLayout layout = getContainerLayout();
        layout.setColumn(originalColumn);
        layout.setSpacing(originalSpacing);
        container.layout();
        updateControls();
    }

    /**
     * Position the dialog somewhere reasonable and show it.
     */
    public void display() {
        setVisible(false);
        show();
        Rectangle r = LDOM.getNextToPosition(this, container.getElement(),
                getContainerLayout().isColumn(), 4);
        setPopupPosition(r.x, r.y);
        setVisible(true);
    }

    protected Focusable getFirstFocusWidget() {
        return spacing;
    }

    private RowLayout getContainerLayout() {
        return (RowLayout)container.getLayout();
    }

    private void updateControls() {
        RowLayout layout = getContainerLayout();
        column.setValue(layout.isColumn());
        setText(spacing, layout.getSpacing());
    }

    private void updateContainer() {
        setDirty(true);
        RowLayout layout = getContainerLayout();
        layout.setColumn(column.getValue());
        layout.setSpacing((int)getFloat(spacing, layout.getSpacing()));
        container.layout();
    }

}