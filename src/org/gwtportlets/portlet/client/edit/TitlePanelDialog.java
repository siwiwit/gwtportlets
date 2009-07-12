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

import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.layout.LDOM;
import org.gwtportlets.portlet.client.util.Rectangle;
import org.gwtportlets.portlet.client.util.FormBuilder;
import org.gwtportlets.portlet.client.edit.PageEditorDialog;
import org.gwtportlets.portlet.client.ui.TitlePanel;

/**
 * Dialog to edit settings for a TitlePanel.
 */
public class TitlePanelDialog extends PageEditorDialog {

    private TitlePanel titlePanel;
    private TitlePanel.Factory original;

    private TextBox titleText = new TextBox();
    private CheckBox titleTextAuto = new CheckBox("Automatically use title from content");
    private CheckBox refreshEnabled = new CheckBox("Refresh button");
    private CheckBox configureEnabled = new CheckBox("Configure button (if supported by content)");
    private CheckBox maximizeEnabled = new CheckBox("Maxmimize button");
    private CheckBox limitMaximize = new CheckBox("Limit size of maximizing child widgets");
    private CheckBox editEnabled = new CheckBox("User may select contained widget using configure");

    public TitlePanelDialog(final TitlePanel titlePanel) {
        super(false, true);
        this.titlePanel = titlePanel;
        this.original = (TitlePanel.Factory)titlePanel.createWidgetFactory();

        setText("Edit Title");

        updateControls();

        ChangeListener changeListener = new ChangeListener() {
            public void onChange(Widget sender) {
                updateCaption();
            }
        };
        titleText.addChangeListener(changeListener);

        ClickListener clickListener = new ClickListener() {
            public void onClick(Widget sender) {
                updateCaption();
            }
        };
        titleTextAuto.addClickListener(clickListener);
        refreshEnabled.addClickListener(clickListener);
        configureEnabled.addClickListener(clickListener);
        maximizeEnabled.addClickListener(clickListener);
        limitMaximize.addClickListener(clickListener);
        editEnabled.addClickListener(clickListener);

        titleText.setVisibleLength(30);
        titleText.setTitle("Enter title for caption");

        FormBuilder b = new FormBuilder();
        b.label("Title").field(titleText).endRow();
        b.field(titleTextAuto).colspan(2).endRow();
        b.field(refreshEnabled).colspan(2).endRow();
        b.field(configureEnabled).colspan(2).endRow();
        b.field(editEnabled).colspan(2).endRow();
        b.field(maximizeEnabled).colspan(2).endRow();
        b.field(limitMaximize).colspan(2).endRow();

        addButton(createRevertButton());
        addButton(createCloseButton());

        setWidget(b.getForm());
    }

    protected void revert() {
        super.revert();
        original.refreshSettings(titlePanel);
        titlePanel.update();
        updateControls();
    }

    /**
     * Position the dialog somewhere reasonable and show it.
     */
    public void display() {
        setVisible(false);
        show();
        Rectangle r = LDOM.getNextToPosition(this, titlePanel.getElement(),
                true, 4);
        setPopupPosition(r.x, r.y);
        setVisible(true);
    }

    protected HasFocus getFirstFocusWidget() {
        return titleText;
    }

    private void updateControls() {
        titleText.setText(titlePanel.getTitleText());
        titleTextAuto.setChecked(titlePanel.isTitleTextAuto());
        refreshEnabled.setChecked(titlePanel.isRefreshEnabled());
        configureEnabled.setChecked(titlePanel.isConfigureEnabled());
        editEnabled.setChecked(titlePanel.isEditEnabled());
        maximizeEnabled.setChecked(titlePanel.isMaximizeEnabled());
        limitMaximize.setChecked(titlePanel.isLimitMaximize());
    }

    private void updateCaption() {
        setDirty(true);
        titlePanel.setTitleText(titleText.getText());
        titlePanel.setTitleTextAuto(titleTextAuto.isChecked());
        titlePanel.setRefreshEnabled(refreshEnabled.isChecked());
        titlePanel.setConfigureEnabled(configureEnabled.isChecked());
        titlePanel.setEditEnabled(editEnabled.isChecked());
        titlePanel.setMaximizeEnabled(maximizeEnabled.isChecked());
        titlePanel.setLimitMaximize(limitMaximize.isChecked());
        titlePanel.update();
        fireChange();
    }

}