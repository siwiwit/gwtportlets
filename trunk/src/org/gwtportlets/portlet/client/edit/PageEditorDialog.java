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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.ui.CssButton;
import org.gwtportlets.portlet.client.ui.Dialog;

/**
 * Base class for layout editor related dialogs. Has standard style and
 * some helper methods for getting data in/out of text boxes and so on.
 */
public class PageEditorDialog extends Dialog
        implements SourcesChangeEvents {

    public static final String STYLE_PORTLET_ED_DIALOG = "portlet-ed-dialog";
    public static final String STYLE_ERROR = "portlet-ed-error";

    private boolean dirty;
    private ChangeListenerCollection changeListeners;

    public PageEditorDialog(boolean autoHide, boolean modal) {
        super(autoHide, modal);
        addStyleName(STYLE_PORTLET_ED_DIALOG);
        setMaximizeVisible(false);
    }

    public PageEditorDialog() {
        this(false, true);
    }

    protected Button createCloseButton() {
        Button close = new CssButton("Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        close.setTitle("Close the dialog");
        return close;
    }

    protected Button createRevertButton() {
        Button revert = new CssButton("Revert", new ClickHandler() {
            public void onClick(ClickEvent event) {
                revert();
            }
        });
        revert.setTitle("Discard changes and restore original values");
        return revert;
    }

    /**
     * Position the dialog somewhere reasonable and show it. This
     * implementation just does 'show'.
     */
    public void display() {
        show();
    }

    /**
     * Undo changes made by the dialog.
     */
    protected void revert() {
        setDirty(false);
    }

    protected void onLoad() {
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                HasFocus w = getFirstFocusWidget();
                if (w != null) {
                    w.setFocus(true);
                }
            }
        });
    }

    /**
     * Get the widget that should receive focus when the dialog is displayed
     * or null if none.
     */
    protected HasFocus getFirstFocusWidget() {
        return null;
    }

    /**
     * Set the value to i, clearing any error style.
     */
    protected void setText(TextBox textBox, float f) {
        setText(textBox, Float.toString(f));
    }

    /**
     * Set the value to i, clearing any error style.
     */
    protected void setText(TextBox textBox, int i) {
        setText(textBox, Integer.toString(i));
    }

    /**
     * Set the value to s, clearing any error style.
     */
    protected void setText(TextBox textBox, String s) {
        textBox.setText(s);
        textBox.removeStyleName(STYLE_ERROR);
    }

    /**
     * Get the value of the text box as a Float or return old if it is invalid.
     */
    protected float getFloat(TextBox textBox, float old) {
        try {
            float f = Float.parseFloat(textBox.getText());
            if (f >= 0.0f) {
                textBox.removeStyleName(STYLE_ERROR);
                return f;
            }
        } catch (NumberFormatException e) {
            // handled below
        }
        textBox.addStyleName(STYLE_ERROR);
        return old;
    }

    public boolean isDirty() {
        return dirty;
    }

    /**
     * Call this with true whenever changes are made to the layout. This
     * is for proper functioning of undo.
     */
    protected void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void addChangeListener(ChangeListener listener) {
        if (changeListeners == null) {
            changeListeners = new ChangeListenerCollection();
        }
        changeListeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        if (changeListeners != null) {
            changeListeners.remove(listener);
        }
    }

    /**
     * Notify listeners that we have changed whatever we are editing.
     */
    protected void fireChange() {
        if (changeListeners != null) {
            changeListeners.fireChange(this);
        }
    }

}
