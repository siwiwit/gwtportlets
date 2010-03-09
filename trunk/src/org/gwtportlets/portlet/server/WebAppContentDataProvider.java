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

package org.gwtportlets.portlet.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gwtportlets.portlet.client.ui.WebAppContentPortlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
                  
/**
 * Displays any content served from our web application. This can
 * come from static HTML, JSP's etc etc. 
 */
public class WebAppContentDataProvider
        implements WidgetDataProvider<WebAppContentPortlet.Factory> {

    private static final Log log = LogFactory.getLog(WebAppContentDataProvider.class);

    public Class getWidgetFactoryClass() {
        return WebAppContentPortlet.Factory.class;
    }

    public void refresh(WebAppContentPortlet.Factory f, PageRequest req) {
        if (f.path == null || f.path.length() == 0) {
            f.html = null;
        } else {
            HttpServletRequest servletRequest = req.getServletRequest();
            String path = f.path.startsWith("/") ? f.path : "/" + f.path;
            RequestDispatcher rd = servletRequest.getRequestDispatcher(path);
            BufferedResponse res = new BufferedResponse(req.getServletResponse());
            try {
                rd.include(servletRequest, res);
                res.getWriter().flush();
                f.html = new String(res.toByteArray(), res.getCharacterEncoding());
            } catch (Exception e) {
                log.error("Error including '" + path + "': " + e, e);
                f.html = "Error including '" + path +
                        "'<br>(see server log for details)";
            }
        }
    }

    /**
     * Buffers all data written.
     */
    private static class BufferedResponse extends HttpServletResponseWrapper {

        private BufferedSOS out = new BufferedSOS();
        private PrintWriter pw = new PrintWriter(out);

        private BufferedResponse(HttpServletResponse wrapped) {
            super(wrapped);
        }

        public ServletOutputStream getOutputStream() throws IOException {
            return out;
        }

        public PrintWriter getWriter() throws IOException {
            return pw;
        }

        public byte[] toByteArray() {
            return out.toByteArray();
        }
    }

    /**
     * ServletOutputStream that writes to a ByteArrayOutputStream.
     */
    private static class BufferedSOS extends ServletOutputStream {

        private ByteArrayOutputStream bos = new ByteArrayOutputStream();

        public void write(int b) throws IOException {
            bos.write(b);
        }

        public void write(byte[] b) throws IOException {
            bos.write(b);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            bos.write(b, off, len);
        }

        public void flush() throws IOException {
            bos.flush();
        }

        public void close() throws IOException {
            bos.close();
        }

        public byte[] toByteArray() {
            return bos.toByteArray();
        }
    }

}
