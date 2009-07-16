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

import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.util.FormBuilder;
import org.gwtportlets.portlet.client.event.EventManager;
import org.gwtportlets.portlet.client.event.CommandEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays a menu from an HTML template from the server.
 */
public class MenuPortlet extends WebAppContentPortlet {

    private Map subMenuMap = new HashMap(); // id -> String html
    private boolean vertical;
    private List visiblePopups = new ArrayList();

    public MenuPortlet() {
        setStyleName("portlet-menu");
        new Popper().startListening(getWidget());
    }

    protected HasHTML createWidget() {
        return new HTML();
    }

    public void setTemplate(String template) {
        subMenuMap.clear();
        HTML html = (HTML)getWidget();
        html.setText("");
        Element root = DOM.createDiv();
        DOM.setInnerHTML(root, template);
        int n = DOM.getChildCount(root);
        for (int i = n - 1; i > 0; i--) {
            Element c = DOM.getChild(root, i);
            String id = DOM.getElementProperty(c, "id");
            if (id != null && id.length() > 0
                    && DOM.getElementProperty(c, "tagName").equalsIgnoreCase("div")) {
                if (subMenuMap.containsKey(id)) {
                    throw new IllegalArgumentException("Duplicate id: '" +
                            id + "'");
                }
                subMenuMap.put(id, DOM.getInnerHTML(c));
                DOM.removeChild(root, c);
            }
        }
        html.setHTML(DOM.getInnerHTML(root));
    }

    /**
     * Is this a vertical menu? If true first level sub menus open to
     * the right (or left if no space) instead of below.
     */
    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void configure() {
        final TextBox path = new TextBox();
        path.setVisibleLength(30);
        path.setTitle("Any content served from the web application " +
                "(JSP pages, static HTML etc.)");
        path.setText(getPath());

        final CheckBox vertical = new CheckBox("Vertical menu bar");
        vertical.setTitle("Open first level sub menus to the right (true) or " +
                "below (false)");
        vertical.setValue(isVertical());

        FormBuilder b = new FormBuilder();
        b.label("Menu template path").field(path).endRow();
        b.field(vertical).colspan(2).endRow();

        final Dialog dlg = new Dialog();
        dlg.setText("Configure " + getWidgetName());
        dlg.setWidget(b.getForm());

        dlg.addButton(new CssButton("OK", new ClickHandler() {
            public void onClick(ClickEvent event) {
                dlg.hide();
                setPath(path.getText().trim());
                setVertical(vertical.getValue());
                refresh();
            }
        }));
        dlg.addCloseButton("Cancel");
        dlg.showNextTo(this);
    }

    /**
     * A menu item (link) that is not a sub menu or external link has been
     * clicked. Broadcast a CommandEvent so other widgets can react if needed.
     */
    protected void onMenuItemClick(Widget sender, Element e, String method) {
        EventManager.get().broadcast(new CommandEvent(this, method));
    }

    /**
     * Open a sub menu.
     */
    private boolean showSubMenuPopup(Popper popper, Widget sender, Element e,
            String method) {
        String s = (String)subMenuMap.get(method);
        if (s != null) {
            HTML w = createSubMenu(s);
            PopupPanel panel = new MenuPopup();
            new Popper(panel).startListening(w);
            addSubMenuToPopup(w, panel);
            popper.showPopup(panel, e, vertical || sender != getWidget(), 0);
            return true;
        }
        return false;
    }

    /**
     * Create a sub menu.
     */
    private HTML createSubMenu(String s) {
        HTML w = new HTML(s);
        w.setStyleName(getStylePrimaryName() + "-sub");
        return w;
    }

    /**
     * Add the sub menu to a popup.
     */
    private void addSubMenuToPopup(Widget subMenu, PopupPanel panel) {
        panel.add(new ShadowPanel(subMenu));
    }

    /**
     * Does e belong to this menu or one of its popups?
     */
    private boolean isMenuElement(Element e) {
        if (DOM.isOrHasChild(getElement(), e)) {
            return true;
        }
        for (int i = visiblePopups.size() - 1; i >= 0; i--) {
            Widget w = (Widget)visiblePopups.get(i);
            if (DOM.isOrHasChild(w.getElement(), e)) {
                return true;
            }
        }
        return false;
    }
    

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    /**
     * Manages our popups.
     */
    private class Popper extends CascadingPopupManager {

        private Popper() {
        }

        private Popper(PopupPanel parent) {
            super(parent);
        }

        protected void onMethodClick(Widget sender, Element e, String method) {
            if (!showSubMenuPopup(this, sender, e, method)) {
                hideAllPopups();
                onMenuItemClick(sender, e, method);
            }
        }

        protected void onMethodHover(Widget sender, Element e, String method) {
            if (!showSubMenuPopup(this, sender, e, method)) {
                hidePopup();
            }
        }

        protected void onHistoryTokenClick(String token) {
            hideAllPopups();
            super.onHistoryTokenClick(token);
        }
    }

    /**
     * One of our popups.
     */
    private class MenuPopup extends PopupPanel {

        public MenuPopup() {
            super(true, false);
            setStyleName("");
        }

        protected void onLoad() {
            visiblePopups.add(this);
        }

        protected void onUnload() {
            visiblePopups.remove(this);
        }

        public boolean onEventPreview(Event ev) {
            // hide us if the mouse moves away and is not over another one of
            // our popups or the menu itself
            if (DOM.eventGetType(ev) == Event.ONMOUSEMOVE) {
                Element target = DOM.eventGetTarget(ev);
                if (!isMenuElement(target)) {
                    hide();
                    return false;
                }
            }
            return super.onEventPreview(ev);
        }

    }
 
    public static class Factory extends WebAppContentPortlet.Factory {

        public boolean vertical;

        public Factory() {
        }

        public Factory(MenuPortlet p) {
            super(p);
            vertical = p.vertical;
        }

        public void refresh(WebAppContentPortlet p) {
            ((MenuPortlet)p).setVertical(vertical);
            super.refresh(p);
        }

        public WebAppContentPortlet createWidget() {
            return new MenuPortlet();
        }
    }
}
