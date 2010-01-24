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

package smartgwt.client.ui;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.edit.row.RowConstraintsDialog;
import org.gwtportlets.portlet.client.layout.*;
import org.gwtportlets.portlet.client.ui.*;
import org.gwtportlets.portlet.client.ui.LayoutPanel;
import org.gwtportlets.portlet.client.util.FormBuilder;

/**
 * Interactive demo to show how to use RowLayout.
 */
public class RowLayoutPortlet extends Portlet {

    private LayoutPanel target = new LayoutPanel();
    private ListBox demoList = new ListBox();

    private static final NumberFormat NUM_FORMAT = NumberFormat.getFormat("0.0#");

    public RowLayoutPortlet() {
        LayoutPanel panel = new LayoutPanel();
        initWidget(panel);

        final CheckBox column = new CheckBox("Column");
        column.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                getTargetLayout().setColumn(column.getValue());
                target.layout();
            }
        });

        final TextBox spacing = new TextBox();
        spacing.setVisibleLength(4);
        spacing.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                try {
                    getTargetLayout().setSpacing(Integer.parseInt(spacing.getText()));
                } catch (NumberFormatException e) {
                    // ignore
                }
                target.layout();
            }
        });

        final Label bounds = new Label();
        target.addLayoutHandler(new LayoutHandler() {
            public void onLayoutUpdated(LayoutEvent layoutEvent) {
                bounds.setText(LDOM.getBounds(target).toString());
                column.setValue(getTargetLayout().isColumn());
                spacing.setText(Integer.toString(getTargetLayout().getSpacing()));
            }
        });

        demoList.addItem("Buttons & Body");
        demoList.addItem("Sidebar & Margin");
        demoList.addItem("Border Layout");
        demoList.setSelectedIndex(0);

        Button add = new CssButton("Add Widget", new ClickHandler() {
            public void onClick(ClickEvent event) {
                target.add(new Thing("Widget-" + (target.getWidgetCount() + 1)));
                target.layout();
            }
        }, "Add a new widget to the layout");

        final Button go = new CssButton("Go", new ClickHandler() {
            public void onClick(ClickEvent event) {
                go();
            }
        }, "Reset the layout to the selected state");

        demoList.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                go.click();
            }
        });

        FormBuilder b = new FormBuilder();
        b.add(add).label("Spacing").field(spacing).field(column).add("")
                .field(bounds).wrap().width("100%").add(demoList).add(go)
                .endRow();

        panel.add(b.getForm(), 22);
        panel.add(target, LayoutConstraints.HIDDEN);

        go();
    }

    private RowLayout getTargetLayout() {
        return (RowLayout)target.getLayout();
    }

    private void go() {
        target.clear();
        switch (demoList.getSelectedIndex()) {
            case 0: demoButtonsAndBody();       break;
            case 1: demoSidebarAndMargin();     break;
            case 2: demoBorder();               break;
        }
        target.layout();
    }

    private void demoButtonsAndBody() {
        target.setLayout(new RowLayout());
        target.add(new Thing("Buttons"), 50);
        target.add(new Thing("Body"));
    }

    private void demoSidebarAndMargin() {
        target.setLayout(new RowLayout(false));
        target.add(new Thing("Sidebar"), 0.2f);
        target.add(new Thing("Body"));
        target.add(new Thing("Margin"), 50);
    }

    private void demoBorder() {
        target.setLayout(new RowLayout());

        LayoutPanel inner = new LayoutPanel(false); // row
        inner.add(new Thing("West"), 0.2f);
        inner.add(new Thing("Center"));
        inner.add(new Thing("East"), 0.2f);

        target.add(new Thing("North"), 0.2f);
        target.add(inner, LayoutConstraints.HIDDEN);
        target.add(new Thing("South"), 0.2f);
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    /**
     * Widget that displays info o
103
	Extran its constraints.
     */
    private class Thing extends PositionAwareComposite {

        private Label size = new Label();
        private Label weight = new Label();
        private Label maxSize = new Label();
        private Label overflow = new Label();
        private Label actualSize = new Label();
        private Label extraSize = new Label();

        public Thing(String name) {
            FlowPanel p = new FlowPanel();
            initWidget(p);
            
            setStyleName("thing");

            Button edit = new CssButton("Edit", new ClickHandler() {
                public void onClick(ClickEvent event) {
                    new RowConstraintsDialog(getParentContainer(), Thing.this).display();
                }
            }, "Edit constraints");

            Button delete = new CssButton("Delete", new ClickHandler() {
                public void onClick(ClickEvent event) {
                    Container p = getParentContainer();
                    p.remove(Thing.this);
                    p.layout();
                }
            });

            FormBuilder b = new FormBuilder();
            b.add(edit, delete).colspan(4).endRow();
            b.field(new HTML("<b>" + name + "</b>")).endRow();
            b.label("Size").field(size)
                    .label("Weight").field(weight)
                    .label("Max size").field(maxSize).
                    label("Overflow").field(overflow).endRow();
            b.label("Actual").field(actualSize)
                    .label("Extra").field(extraSize).endRow();
            p.add(b.getForm());
        }

        public Container getParentContainer() {
            return (Container)getParent();
        }

        public void boundsUpdated() {
            RowLayout.Constraints c =
                    (RowLayout.Constraints)((Container)getParent()).getLayoutConstraints(this);
            size.setText(NUM_FORMAT.format(c.getSize()));
            weight.setText(NUM_FORMAT.format(c.getWeight()));
            maxSize.setText("" + c.getMaxSize());
            overflow.setText(c.getOverflow());
            actualSize.setText("" + c.getActualSize());
            extraSize.setText("" + c.getExtraSize());
        }
    }

    public static class Factory extends PortletFactory<RowLayoutPortlet> {

        public Factory() {
        }

        public Factory(RowLayoutPortlet p) {
            super(p);
        }

        public RowLayoutPortlet createWidget() {
            return new RowLayoutPortlet();
        }
    }

}
