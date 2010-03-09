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

package main.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.ui.PortletFactory;
import org.gwtportlets.portlet.server.PageProvider;
import org.gwtportlets.portlet.server.PageRequest;
import org.gwtportlets.portlet.server.WidgetDataProvider;
import org.gwtportlets.portlet.server.WidgetFactoryXmlIO;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.Modifier;

/**
 * Loads our pages to/from XML files. This implementation checks for the
 * existance of a $user.dir/web/WEB-INF/pages directory. If this is found then
 * pages are loaded and saved to files under that directory. Otherwise pages are
 * read from the session and WEB-INF/pages with changes stored in the session.
 *
 * This means that someone running the demo on their own machine and starting
 * Tomcat from the project root directory will be able to make persistent
 * changes to pages.
 */
public class DemoPageProvider extends PageProvider {

    private Log log = LogFactory.getLog(getClass());

    private WidgetFactoryXmlIO xmlIO = new WidgetFactoryXmlIO();
    private File pageDir;
    private boolean useSession;
    
    public DemoPageProvider(ServletContext sc) {
        pageDir = new File(System.getProperty("user.dir", ""), "war/WEB-INF/pages");
        if (!pageDir.isDirectory()) {
            pageDir = new File(sc.getRealPath("WEB-INF/pages"));
            useSession = true;
            log.info("Saving pages to session: " + pageDir);
        } else {
            log.info("Loading and saving pages from " + pageDir);            
        }

        // discover our classes (in a real app they would be Spring beans or
        // something similar)
        ClassFinder cf = new ClassFinder(sc, "main");

        // find all PortletFactory's and alias them so fully qualified class
        // names don't end up in the XML
        for (Class cls : cf.findClasses("$Factory", PortletFactory.class)) {
            log.info("Portlet factory: " + cls.getName());
            xmlIO.alias(cls);
        }

        // create an instance of each DataProvider
        for (Class cls : cf.findClasses("DataProvider", WidgetDataProvider.class)) {
            int mod = cls.getModifiers();
            if (!Modifier.isAbstract(mod) && !Modifier.isInterface(mod)) {
                log.info("Data provider: " + cls.getName());
                try {
                    add((WidgetDataProvider)cls.newInstance());
                } catch (Exception e) {
                    log.error(e, e);
                }
            }
        }
    }

    protected WidgetFactory createWidgetFactory(PageRequest req) {
        InputStream in = null;
        try {
            in = openPageStream(req);
            if (in == null) {
                return null;
            }
            return xmlIO.fromXML(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close(in);
        }
    }

    private InputStream openPageStream(PageRequest req) throws IOException {
        String filename = getPageFilename(req.getPageName());
        if (useSession) {
            HttpSession session = req.getServletRequest().getSession(false);
            if (session != null) {
                byte[] data = (byte[])session.getAttribute(filename);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        }
        File f = new File(pageDir, filename);
        if (!f.isFile()) {
            return null;
        }
        return new FileInputStream(f);
    }

    private String getPageFilename(String pageName) {
        return pageName + ".xml";
    }

    protected void handleRefreshException(PageRequest req, WidgetFactory top,
            WidgetFactory wf, Exception e) {
        log.error(e, e);
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        }
        throw new RuntimeException(e);
    }

    /**
     * Update the page layout.
     */
    public void savePage(String pageName, WidgetFactory wf,
            HttpServletRequest servletRequest) {
        String filename = getPageFilename(pageName);
        log.info("Saving page '" + pageName + "' to " + filename +
                (useSession ? " in session" : " in " + pageDir));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        xmlIO.toXML(wf, bos);
        if (useSession) {
            servletRequest.getSession(true).setAttribute(filename, bos.toByteArray());
        } else {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(new File(pageDir, filename));
                fos.write(bos.toByteArray());
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                close(fos);
            }
        }
    }

    /**
     * Get the XML for the page or null if page is not found.
     */
    public String getPageXML(PageRequest req) throws IOException {
        InputStream in = null;
        char[] buf = new char[4096];
        StringBuffer xml = new StringBuffer();
        try {
            in = openPageStream(req);
            if (in == null) {
                return null;
            }
            InputStreamReader r = new InputStreamReader(in);
            for (;;) {
                int sz = r.read(buf);
                if (sz < 0) {
                    break;
                }
                xml.append(buf, 0, sz);
            }
        } finally {
            close(in);
        }
        return xml.toString();
    }

    private void close(Closeable o) {
        if (o != null) {
            try {
                o.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

}
