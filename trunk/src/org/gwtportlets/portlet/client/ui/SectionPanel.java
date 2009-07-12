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

package org.gwtportlets.portlet.client.ui;

import org.gwtportlets.portlet.client.event.AppEventListener;
import org.gwtportlets.portlet.client.event.WidgetChangeEvent;
import org.gwtportlets.portlet.client.event.EventManager;
import org.gwtportlets.portlet.client.layout.Container;
import org.gwtportlets.portlet.client.layout.RowLayout;
import org.gwtportlets.portlet.client.layout.LayoutUtil;
import org.gwtportlets.portlet.client.layout.ContainerFactory;
import org.gwtportlets.portlet.client.util.FormBuilder;
import org.gwtportlets.portlet.client.WidgetFactory;
import com.google.gwt.user.client.ui.*;

import java.util.EventObject;

/**
 * Container with a simple title. Delegates to a LayoutPanel to hold its
 * children.
 */
public class SectionPanel extends ContainerPortlet implements AppEventListener {

    private String titleText = "Title";
    private boolean titleTextAuto = true;

    private Portlet portlet;

    private final LayoutPanel outer = new LayoutPanel(new RowLayout(true, 0));
    private final RefreshPanel main = new RefreshPanel();
    private SimplePanel header = new SimplePanel();
    private Label titleLabel = new Label(titleText);

    public SectionPanel() {
        initWidget(outer);

        header.add(titleLabel);

        outer.add(header);
        outer.add(main);

        setStyleName("portlet-section");
    }

    public SectionPanel(Widget child) {
        this();
        add(child);
    }    

    protected void onLoad() {
        update();
    }

    protected Container getDelegate() {
        return main;
    }

    public void setStyleName(String style) {
        super.setStyleName(style);
        header.setStyleName(style + "-header");
        titleLabel.setStyleName(style + "-title");
        main.setStyleName(style + "-main");
        Theme.get().updateSectionPanel(this);
    }

    public void setDimensions(int headerHeight) {
        outer.setLayoutConstraints(header,
                new RowLayout.Constraints(headerHeight));
    }

    public void onAppEvent(EventObject ev) {
        if (ev instanceof WidgetChangeEvent
                && (refreshHelper != null || ev.getSource() == portlet)) {
            update();
        }
    }

    private void updateTitle(String s) {
        if (!titleLabel.getText().equals(s)) {
            titleLabel.setText(s);
            EventManager.get().broadcastUp(this, new WidgetChangeEvent(this));
        }
    }

    /**
     * Update our title.
     */
    public void update() {
        portlet = LayoutUtil.findPortletChild(main);
        if (titleTextAuto && portlet != null) {
            updateTitle(portlet.getWidgetTitle());
        } else {
            updateTitle(titleText);
        }
    }

    /**
     * Get the child portlet used for our title, flags and so on.
     */
    protected Portlet getPortlet() {
        return portlet;
    }

    public String getWidgetTitle() {
        return titleLabel.getText();
    }

    public String getWidgetName() {
        return "Section";
    }

    public boolean isConfigureSupported() {
        return true;
    }

    public void configure() {
        CfgDialog dlg = new CfgDialog(this);
        dlg.showNextTo(this);
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    public static class Factory extends ContainerFactory<SectionPanel> {

        private String title;
        private boolean titleAuto;

        public Factory() {
        }

        public Factory(SectionPanel cp) {
            super(cp);
            title = cp.titleText;
            titleAuto = cp.titleTextAuto;
        }

        public void refresh(SectionPanel p) {
            refreshSettings(p);
            super.refresh(p);
        }

        public void refreshSettings(SectionPanel p) {
            p.titleText = title;
            p.titleTextAuto = titleAuto;
        }

        public SectionPanel createWidget() {
            return new SectionPanel();
        }
    }

    /**
     * Configures a SectionPanel.
     */
    public static class CfgDialog extends Dialog {

        private final SectionPanel panel;

        private TextBox titleText = new TextBox();
        private CheckBox titleTextAuto = new CheckBox(
                "Automatically use title from content");

        public CfgDialog(SectionPanel p) {
            this.panel = p;

            setText("Configure Section");

            titleText.addChangeListener(new ChangeListener() {
                public void onChange(Widget sender) {
                    updatePanel();
                }
            });

            titleTextAuto.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    updatePanel();
                }
            });

            FormBuilder b = new FormBuilder();
            b.label("Title").field(titleText).endRow();
            b.field(titleTextAuto).colspan(2).endRow();

            setWidget(b.getForm());

            final Factory original = new Factory(panel);
            addButton(new CssButton("Revert", new ClickListener() {
                public void onClick(Widget sender) {
                    original.refreshSettings(panel);
                    panel.update();
                    updateControls();
                }
            }, "Discard changes and restore original values"));

            addCloseButton();

            updateControls();
        }

        private void updatePanel() {
            panel.titleText = titleText.getText();
            panel.titleTextAuto = titleTextAuto.isChecked();
            panel.update();
        }

        private void updateControls() {
            titleText.setText(panel.titleText);
            titleTextAuto.setChecked(panel.titleTextAuto);
        }
    }

}
