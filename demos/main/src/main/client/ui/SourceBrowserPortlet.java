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

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import org.gwtportlets.portlet.client.DoNotPersist;
import org.gwtportlets.portlet.client.DoNotSendToServer;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.util.FormBuilder;
import org.gwtportlets.portlet.client.ui.CssButton;
import org.gwtportlets.portlet.client.ui.LayoutPanel;
import org.gwtportlets.portlet.client.ui.Portlet;
import org.gwtportlets.portlet.client.ui.PortletFactory;
import org.gwtportlets.portlet.client.util.GenUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Browses source as produced by Java2HTML (http://www.java2html.com).
 */
public class SourceBrowserPortlet extends Portlet {

    private String filename;
    private String html;
    private ClassSuggestion[] classes;

    private TextBox classBox = new TextBox();
    private HTML javaHtml = new HTML();

    private List<String> history = new ArrayList<String>();
    private int historyPos;

    private Button back = new CssButton("Back", new ClickHandler() {
        public void onClick(ClickEvent event) {
            navigateTo(history.get(historyPos - 1));
        }
    }, "Navigate to the previous file");

    private Button forward = new CssButton("Fwd", new ClickHandler() {
        public void onClick(ClickEvent event) {
            navigateTo(history.get(historyPos + 1));
        }
    }, "Navigate to the next file");

    public SourceBrowserPortlet() {
        LayoutPanel panel = new LayoutPanel();
        initWidget(panel);

        Button classes = new CssButton("Classes", new ClickHandler() {
            public void onClick(ClickEvent event) {
                navigateTo("AllClasses.html");
            }
        }, "Show class list");

        Button packages = new CssButton("Packages", new ClickHandler() {
            public void onClick(ClickEvent event) {
                navigateTo("packages.html");
            }
        }, "Show package list");

        classBox.setVisibleLength(30);
        
        SuggestBox sb = new SuggestBox(new ClassSuggestOracle(), classBox);
        sb.setWidth("100%");
        sb.addEventHandler(new SuggestionHandler() {
            public void onSuggestionSelected(SuggestionEvent event) {
                ClassSuggestion s = (ClassSuggestion)event.getSelectedSuggestion();
                navigateTo(s.className);
            }
        });

        FormBuilder b = new FormBuilder();
        b.add(back).add(forward).field(sb).width("100%").add(classes).add(packages)
                .endRow();

        panel.add(b.getForm(), 22);
        panel.add(javaHtml);

        javaHtml.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                onJavaHtmlClick();
            }
        });
    }

    private void restore(Factory f) {
        filename = f.filename;
        html = f.html;
        if (f.classes != null) {
            classes = f.classes;
        }
        if (filename != null) {
            historyPos = history.indexOf(filename);
            if (historyPos < 0) {
                if (history.size() > 20) {
                    history.remove(0);
                }
                historyPos = history.size();
                history.add(filename);
            }
            String s = filename;
            if (s.endsWith(".index.html")) {
                s = s.substring(0, s.length() - 11);
            } else if (s.endsWith(".html")) {
                s = s.substring(0, s.length() - 5);
            } else {
                s = s.substring(s.lastIndexOf('.') + 1);
            }
            classBox.setText(s);
        }
        javaHtml.setHTML(html == null ? "" : html);
        updateControls();
    }

    public boolean isConfigureSupported() {
        return true;
    }

    public void configure() {
        String s = Window.prompt("Enter name of class to display:",
                filename == null ? "" : filename);
        if (s != null) {
            filename = s;
            refresh();
        }
    }

    /**
     * Pickup clicks on links in the Java HTML and navigate to the file being
     * clicked on by refreshing this portlet.
     */
    private void onJavaHtmlClick() {
        Event ev = DOM.eventGetCurrentEvent();
        if (ev == null) {
            return;
        }
        Element link;
        try {
            link = DOM.eventGetTarget(ev);
        } catch (Exception e) {
            return;
        }
        if (link != null && GenUtil.isLink(link)) {
            String base = GenUtil.getBaseUrl() + "src/";
            String href = URL.decodeComponent(
                    DOM.getElementProperty(link, "href"));
            if (href != null && href.startsWith(base)) {
                DOM.eventPreventDefault(ev);
                navigateTo(href.substring(base.length()));
            }
        }
    }

    private void navigateTo(String filename) {
        if (filename.equals(this.filename)) {
            return;
        }
        Factory f = new Factory(this);
        f.filename = filename;
        f.doNotSendClasses = classes != null;
        refresh(f, null);
    }

    private void updateControls() {
        back.setEnabled(historyPos > 0);
        forward.setEnabled(historyPos < history.size() - 1);
    }

    public WidgetFactory createWidgetFactory() {
        return new Factory(this);
    }

    private class ClassSuggestOracle extends SuggestOracle {

        public void requestSuggestions(Request req, Callback cb) {
            String q = req.getQuery().toLowerCase();
            List<ClassSuggestion> list = new ArrayList<ClassSuggestion>();
            for (ClassSuggestion s : classes) {
                if (s.simpleName.toLowerCase().startsWith(q)) {
                    list.add(s);
                }
            }
            cb.onSuggestionsReady(req, new Response(list));
        }

    }

    public static class ClassSuggestion
            implements SuggestOracle.Suggestion, Serializable, Comparable {

        public String className;
        public String simpleName;

        public ClassSuggestion(String className) {
            this.className = className;
            simpleName = className.substring(className.lastIndexOf('.') + 1);
        }

        public ClassSuggestion() {
        }

        public String getDisplayString() {
            return simpleName;
        }

        public String getReplacementString() {
            return simpleName;
        }

        public int compareTo(Object o) {
            return simpleName.compareTo(((ClassSuggestion)o).simpleName);
        }
    }

    public static class Factory extends PortletFactory<SourceBrowserPortlet> {

        public String filename;
        @DoNotSendToServer
        public String html;
        @DoNotSendToServer
        public ClassSuggestion[] classes;
        @DoNotPersist
        public boolean doNotSendClasses;

        public Factory() {
        }

        public Factory(SourceBrowserPortlet p) {
            super(p);
            filename = p.filename;
            html = p.html;
            classes = p.classes;
        }

        public void refresh(SourceBrowserPortlet p) {
            super.refresh(p);
            p.restore(this);
        }

        public SourceBrowserPortlet createWidget() {
            return new SourceBrowserPortlet();
        }
    }

}
