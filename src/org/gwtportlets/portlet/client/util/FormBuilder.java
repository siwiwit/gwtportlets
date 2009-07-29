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

package org.gwtportlets.portlet.client.util;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

/**
 * Helps to layout widgets on a form by keeping track of row and column
 * counters. Styles the first and last row and column differently so the
 * stylesheet can keep consistent spacing between the cells without having
 * spacing around the outermost cells of the table. This make it easier to
 * get nested tables to line up and so on.
 */
public class FormBuilder {

    private final FlexTable form = createForm();
    private final FlexTable.FlexCellFormatter cellFormatter =
            form.getFlexCellFormatter();

    private final String stylePrefix;
    private final String styleFirst;
    private final String styleLast;
    private final String styleOdd;
    private final String styleEven;

    private int row;
    private int col = -1;
    private int colspan;
    private int rowCols;
    private int columns;

    private static final String STYLE_LABEL = "label";
    private static final String STYLE_FIELD = "field";
    private static final String STYLE_CAPTION = "caption";

    public FormBuilder(String style) {
        stylePrefix = style + "-";
        styleFirst = stylePrefix + "first";
        styleLast = stylePrefix + "last";
        styleOdd = stylePrefix + "odd";
        styleEven = stylePrefix + "even";

        form.setStyleName(style);
        form.setCellPadding(0);
        form.setCellSpacing(0);
        form.setBorderWidth(0);
    }

    public FormBuilder() {
        this("portlet-form");
    }

    /**
     * Override this to create a FlexTable subclass if you need to.
     */
    protected FlexTable createForm() {
        return new FlexTable();
    }

    public FlexTable getForm() {
        return form;
    }

    /**
     * Get the form wrapped in a DIV.
     */
    public SimplePanel getFormInPanel() {
        SimplePanel p = new SimplePanel();
        p.add(getForm());
        return p;
    }

    /**
     * Add a field label.
     */
    public FormBuilder label(String text) {
        add(text, stylePrefix + STYLE_LABEL);
        return this;
    }

    /**
     * Add a field label.
     */
    public FormBuilder label(Widget widget) {
        add(widget, stylePrefix + STYLE_LABEL);
        return this;
    }

    /**
     * Add an html label.
     */
    public FormBuilder htmlLabel(String html) {
        addCell(stylePrefix + STYLE_LABEL);
        form.setHTML(row, col, html);
        return this;
    }

    /**
     * Add a field.
     */
    public FormBuilder field(Widget widget) {
        add(widget, stylePrefix + STYLE_FIELD);
        return this;
    }

    /**
     * Add a caption.
     */
    public FormBuilder caption(String txt) {
        add(txt, stylePrefix + STYLE_CAPTION);
        return this;
    }

    /**
     * Add a caption.
     */
    public FormBuilder caption(Widget widget) {
        add(widget, stylePrefix + STYLE_CAPTION);
        return this;
    }

    /**
     * Add a HTML caption.
     */
    public FormBuilder htmlCaption(String html) {
        addCell(stylePrefix + STYLE_CAPTION);
        form.setHTML(row, col, html);
        return this;
    }

    /**
     * Add a field.
     */
    public FormBuilder field(String txt) {
        add(txt, stylePrefix + STYLE_FIELD);
        return this;
    }

    /**
     * Add a html field.
     */
    public FormBuilder htmlField(String html) {
        addCell(stylePrefix + STYLE_FIELD);
        form.setHTML(row, col, html);
        return this;
    }

    /**
     * Add text with no TD style.
     */
    public FormBuilder add(String txt) {
        add(txt, null);
        return this;
    }

    /**
     * Add widget with no TD style.
     */
    public FormBuilder add(Widget widget) {
        add(widget, null);
        return this;
    }

    /**
     * Add html with no TD style.
     */
    public FormBuilder html(String html) {
        addCell(null);
        form.setHTML(row, col, html);
        return this;
    }

    /**
     * Use a FormBuilder to construct a nested table and put this in the
     * TD with no TD style. Null entries in widgets are ignored. If there
     * are no widgets then a &amp;nbsp; is added instead.
     */
    public FormBuilder add(Widget... widgets) {
        FormBuilder b = new FormBuilder(form.getStyleName());
        int c = 0;
        for (Widget w : widgets) {
            if (w != null) {
                b.add(w);
                ++c;
            }
        }
        if (c > 0) {
            b.endRow();
            add(b.getForm());
        } else {
            add("&nbsp");
        }
        return this;
    }

    /**
     * Add a widget and use styleName for the TD.
     */
    protected void add(Widget widget, String styleName) {
        addCell(styleName);
        form.setWidget(row, col, widget);
    }

    /**
     * Add text and use styleName for the TD.
     */
    protected void add(String text, String styleName) {
        addCell(styleName);
        form.setText(row, col, text);
    }

    private void addCell(String styleName) {
        String style;
        if (col == -1) {
            if (row > 0) { // previous row is no longer last
                form.getRowFormatter().removeStyleName(row - 1, styleLast);
            }
            style = styleName == null ? styleFirst : styleFirst + " " + styleName;
        } else {
            rowCols += colspan;
            style = styleName;
        }
        ++col;
        if (style != null) {
            cellFormatter.setStyleName(row, col, style);
        }
        colspan = 1;
    }

    /**
     * Set the colspan of the most recently added label or field.
     */
    public FormBuilder colspan(int cols) {
        cellFormatter.setColSpan(row, col, cols);
        colspan = cols;
        return this;
    }

    /**
     * Add a style to the TD for the most recently added label or field.
     */
    public FormBuilder style(String styleName) {
        cellFormatter.addStyleName(row, col, styleName);
        return this;
    }

    /**
     * Left align the current TD.
     */
    public FormBuilder left() {
        cellFormatter.addStyleName(row, col, stylePrefix + "left");
        return this;
    }

    /**
     * Center align the current TD.
     */
    public FormBuilder center() {
        cellFormatter.addStyleName(row, col, stylePrefix + "center");
        return this;
    }

    /**
     * Right align the current TD.
     */
    public FormBuilder right() {
        cellFormatter.addStyleName(row, col, stylePrefix + "right");
        return this;
    }

    /**
     * Wrap text in the current TD.
     */
    public FormBuilder wrap() {
        cellFormatter.addStyleName(row, col, stylePrefix + "wrap");
        return this;
    }

    /**
     * Set the title attribute on the current TD.
     */
    public FormBuilder title(String text) {
        DOM.setElementAttribute(cellFormatter.getElement(row, col),
                "title", text);
        return this;
    }

    /**
     * Set the width attribute on the current TD.
     */
    public FormBuilder width(String width) {
        DOM.setElementAttribute(cellFormatter.getElement(row, col),
                "width", width);
        return this;
    }

    /**
     * Set the height attribute on the current TD.
     */
    public FormBuilder height(String height) {
        DOM.setElementAttribute(cellFormatter.getElement(row, col),
                "height", height);
        return this;
    }

    /**
     * Finish the current row.
     */
    public FormBuilder endRow() {
        if (col < 0) {
            add("");    // no cells so put in one for the row
        }
        if (rowCols < columns - 1) {
            colspan(columns - rowCols);
        } else {
            columns = rowCols + colspan;
        }
        if (col >= 0) {
            form.getCellFormatter().addStyleName(row, col, styleLast);
        }

        String style = (row & 1) == 1 ? styleOdd : styleEven;
        if (row == 0) {
            style = styleFirst + " " + style;
        }
        form.getRowFormatter().setStyleName(row, style + " " + styleLast);
        ++row;
        rowCols += colspan;
        if (rowCols > columns) {
            columns = rowCols;
        }
        rowCols = colspan = 0;
        col = -1;
        return this;
    }

    /**
     * Set vertical alignment of the current TD.
     */
    public FormBuilder valign(HasVerticalAlignment.VerticalAlignmentConstant align) {
        cellFormatter.setVerticalAlignment(row, col, align);
        return this;
    }

    /**
     * Set horizontal alignment of the current TD.
     */
    public FormBuilder halign(HasHorizontalAlignment.HorizontalAlignmentConstant align) {
        cellFormatter.setHorizontalAlignment(row, col, align);
        return this;
    }

    /**
     * Get the current row number.
     */
    public int getRow() {
        return row;
    }

    /**
     * Get the current column number. This will be -1 if there is nothing
     * in the current row.
     */
    public int getCol() {
        return col;
    }

    /**
     * Get the colspan of the most recently added cell.
     *
     * @see #colspan(int) 
     */
    public int getColspan() {
        return colspan;
    }

    /**
     * <p>Set the number of columns in each TR. When {@link #endRow()} is
     * called the last cell is given a colspan to take up any remaining
     * columns. The columns value is automatically increased should the number
     * of columns in a TR exceed it.</p>
     *
     * <p>This feature is useful to avoid having to call {@link #colspan(int)}
     * for many rows on a form because one of the last rows has extra columns.
     * </p>
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getColumns() {
        return columns;
    }
}
