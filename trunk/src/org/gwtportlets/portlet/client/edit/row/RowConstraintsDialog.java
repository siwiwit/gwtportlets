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

import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.layout.Container;
import org.gwtportlets.portlet.client.layout.LDOM;
import org.gwtportlets.portlet.client.util.Rectangle;
import org.gwtportlets.portlet.client.util.FormBuilder;
import org.gwtportlets.portlet.client.layout.RowLayout;
import org.gwtportlets.portlet.client.edit.PageEditorDialog;

/**
 * Dialog to edit row layout constraints.
 */
public class RowConstraintsDialog extends PageEditorDialog {

    private Container container;
    private Widget target;
    private RowLayout.Constraints original;

    private TextBox size = new TextBox();
    private TextBox weight = new TextBox();
    private TextBox maxSize = new TextBox();
    private ListBox overflow = new ListBox();

    public RowConstraintsDialog(final Container container, final Widget target) {
        super(false, true);
        this.container = container;
        this.target = target;
        original = new RowLayout.Constraints(getConstraints());

        setText("Edit Contraints");

        overflow.addItem(RowLayout.Constraints.HIDDEN);
        overflow.addItem(RowLayout.Constraints.AUTO);
        overflow.addItem(RowLayout.Constraints.SCROLL);
        overflow.addItem(RowLayout.Constraints.VISIBLE);

        updateControls();

        ChangeListener changeListener = new ChangeListener() {
            public void onChange(Widget sender) {
                updateConstaints();
            }
        };
        size.addChangeListener(changeListener);
        weight.addChangeListener(changeListener);
        maxSize.addChangeListener(changeListener);
        overflow.addChangeListener(changeListener);

        size.setVisibleLength(6);
        weight.setVisibleLength(6);
        maxSize.setVisibleLength(6);
        overflow.setWidth("80px");

        size.setTitle("Size in pixels or fraction of available space");
        weight.setTitle("Weighting (relative to other widgets) to allocate extra space");
        maxSize.setTitle("Maximum size in pixels (0 = unlimited)");
        overflow.setTitle("Scrollbars (auto, scroll) or not (hidden, visible)");

        FormBuilder b = new FormBuilder();
        b.label("Size").field(size).endRow();
        b.label("Weight").field(weight).endRow();
        b.label("Max size").field(maxSize).endRow();
        b.label("Overflow").field(overflow).endRow();
        
        addButton(createRevertButton());
        addButton(createCloseButton());

        setWidget(b.getForm());
    }

    protected void revert() {
        super.revert();
        container.setLayoutConstraints(target, original);
        container.layout();
        updateControls();
    }

    /**
     * Position the dialog somewhere reasonable and show it.
     */
    public void display() {
        setVisible(false);
        show();
        Rectangle r = LDOM.getNextToPosition(this, target.getElement(),
                getContainerLayout().isColumn(), 4);
        setPopupPosition(r.x, r.y);
        setVisible(true);
    }

    protected HasFocus getFirstFocusWidget() {
        return size;
    }

    private RowLayout getContainerLayout() {
        return (RowLayout)container.getLayout();
    }

    private RowLayout.Constraints getConstraints() {
        return (RowLayout.Constraints)container.getLayoutConstraints(target);
    }

    private void updateControls() {
        RowLayout.Constraints c = getConstraints();
        setText(size, c.getSize());
        setText(weight, c.getWeight());
        setText(maxSize, c.getMaxSize());
        String o = c.getOverflow();
        for (int i = overflow.getItemCount() - 1; i >= 0; i--) {
            if (overflow.getValue(i).equals(o)) {
                overflow.setSelectedIndex(i);
                break;
            }
        }
    }

    private void updateConstaints() {
        setDirty(true);
        RowLayout.Constraints c = getConstraints();
        c.setSize(getFloat(size, c.getSize()));
        c.setWeight(getFloat(weight, c.getWeight()));
        c.setMaxSize((int)getFloat(maxSize, c.getMaxSize()));
        c.setOverflow(overflow.getValue(overflow.getSelectedIndex()));
        container.setLayoutConstraints(target, c);
        container.layout();
        fireChange();
    }

}
