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

package main.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import main.client.DemoPage;
import main.client.DemoService;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.ui.LayoutPanel;
import org.gwtportlets.portlet.client.ui.PagePortlet;
import org.gwtportlets.portlet.server.PageRequest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Implementation of our remote service interface.
 */
public class DemoServiceImpl extends RemoteServiceServlet
        implements DemoService {

    private DemoPageProvider pageProvider;

    public void init() throws ServletException {
        super.init();
        ServletContext sc = getServletContext();
        sc.setAttribute("pageProvider", pageProvider = new DemoPageProvider(sc));
    }

    public DemoPage getRootPage(String historyToken) {
        DemoPage p = getPage(historyToken);
        PageRequest req = createPageRequest(historyToken);
        req.setPageName("root");
        p.rootWidgetFactory = pageProvider.openPage(req);
        if (p.rootWidgetFactory == null) {
            // create an initial root page containing just a PagePortlet
            LayoutPanel.Factory f = new LayoutPanel.Factory();
            f.widgets = new WidgetFactory[]{new PagePortlet.Factory()};
            p.rootWidgetFactory = f;
            pageProvider.savePage("root", f, getThreadLocalRequest());
        }
        return p;
    }

    private PageRequest createPageRequest(String historyToken) {
        PageRequest req = new PageRequest(historyToken);
        req.setServletConfig(getServletConfig());
        req.setServletRequest(getThreadLocalRequest());
        req.setServletResponse(getThreadLocalResponse());
        return req;
    }

    public DemoPage getPage(String historyToken) {
        PageRequest req = createPageRequest(historyToken);
        if (req.getPageName().length() == 0) {
            req.setPageName("home");
        }
        DemoPage p = new DemoPage();
        p.pageName = req.getPageName();
        p.widgetFactory = pageProvider.openPage(req);
        p.canEditPage = true;
        return p;
    }

    public void savePage(String pageName, WidgetFactory factory) {
        pageProvider.savePage(pageName, factory, getThreadLocalRequest());
    }

    public WidgetFactory refresh(String historyToken, WidgetFactory wf) {
        pageProvider.refresh(createPageRequest(historyToken), wf);
        return wf;
    }
    
    protected void doUnexpectedFailure(Throwable e) {
        e.printStackTrace();
        super.doUnexpectedFailure(e);
    }

}