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

package main.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.DoNotSendToServer;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.util.FormBuilder;
import org.gwtportlets.portlet.client.layout.LDOM;
import org.gwtportlets.portlet.client.layout.PositionAware;
import org.gwtportlets.portlet.client.layout.HasMaximumSize;
import org.gwtportlets.portlet.client.ui.*;
import org.gwtportlets.portlet.client.util.Rectangle;

/**
 * Displays free and total memory on the server.
 */
public class FreeMemoryPortlet extends Portlet {

    public static String TYPE_GOM = "gom";
    public static String TYPE_PIE_3D = "p3";
    public static String TYPE_PIE = "p";

    private int freeK, totalK;
    private String chartType = TYPE_GOM;

    private Label label = new Label();
    private Chart chart = new Chart();

    public FreeMemoryPortlet() {
        LayoutPanel panel = new LayoutPanel();
        initWidget(panel);

        panel.add(chart);
        panel.add(label, 24);
        panel.layout();

        DOM.setStyleAttribute(label.getElement(), "textAlign", "center");
    }

    private void restore(Factory f) {
        this.freeK = f.freeK;
        this.totalK = f.totalK;
        this.chartType = f.chartType;
        label.setText("Free memory: " + freeK + " / " + totalK + " KB");
        chart.update();
    }

    public boolean isConfigureSupported() {
        return true;
    }

    public void configure() {
        final ListBox type = new ListBox();
        type.addItem("Google-o-meter", TYPE_GOM);
        type.addItem("Pie 3D", TYPE_PIE_3D);
        type.addItem("Pie", TYPE_PIE);
        selectChartType(type);

        type.addChangeListener(new ChangeListener() {
            public void onChange(Widget sender) {
                chartType = type.getValue(type.getSelectedIndex());
                chart.update();
                fireConfigChangeEvent();
            }
        });

        FormBuilder b = new FormBuilder();
        b.label("Chart type").field(type).endRow();

        final Dialog dlg = new Dialog();
        dlg.setText("Configure " + getWidgetName());
        dlg.setWidget(b.getForm());

        final WidgetFactory wf = createWidgetFactory();
        dlg.addButton(new CssButton("Revert", new ClickListener() {
            public void onClick(Widget sender) {
                wf.refresh(FreeMemoryPortlet.this);
                selectChartType(type);
            }
        }, "Undo changes"));
        dlg.addCloseButton();
        dlg.showNextTo(this);
    }

    private void selectChartType(ListBox type) {
        int sel = 0;
        for (int i = type.getItemCount() - 1; i >= 0; i--) {
            if (type.getValue(i).equals(chartType)) {
                sel = i;
                break;
            }
        }
        type.setSelectedIndex(sel);
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    /**
     * Google chart that automatically updates its URL when its size changes.
     * Limits its size as Google charts cannot exceed 300000 pixels.
     */
    private class Chart extends Image implements PositionAware, HasMaximumSize {

        public int getMaxWidth() {
            return 775;
        }

        public int getMaxHeight() {
            return getMaxWidth() / 2;
        }

        public void boundsUpdated() {
            update();
        }

        public void update() {
            Rectangle r = LDOM.getContentBounds(this);

            int freeP = (int)(freeK * 100.0 / totalK + 0.5);

            StringBuffer b = new StringBuffer();
            b.append("http://chart.apis.google.com/chart?cht=").append(chartType)
                    .append("&chs=").append(r.width).append('x').append(r.height)
                    .append("&chd=t:").append(freeP);
            if (!TYPE_GOM.equals(chartType)) {
                b.append(',').append(100 - freeP);
            }

            String url = b.toString();
            if (!url.equals(getUrl())) {
                setWidth(r.width + "px");
                setHeight(r.height + "px");
                setUrl(url);
            }
        }
    }

    public static class Factory extends PortletFactory<FreeMemoryPortlet> {

        @DoNotSendToServer
        public int freeK;
        @DoNotSendToServer
        public int totalK;
        public String chartType;

        public Factory() {
        }

        public Factory(FreeMemoryPortlet p) {
            super(p);
            freeK = p.freeK;
            totalK = p.totalK;
            chartType = p.chartType;
        }

        public void refresh(FreeMemoryPortlet p) {
            super.refresh(p);
            p.restore(this);
        }

        public FreeMemoryPortlet createWidget() {
            return new FreeMemoryPortlet();
        }
    }
}
