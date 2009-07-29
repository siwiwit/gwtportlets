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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * Information about a request for a 'page' i.e. history token.
 * Separates the name of the page from parameters like in a conventional
 * URL (e.g. 'product?id=123&amp;view=detailed').
 */
public class PageRequest {

    private String pageName;
    private final Map<String, String> paramMap = new HashMap<String, String>();
    private Map<String, Object> attributeMap;
    private boolean openPage;

    private ServletConfig servletConfig;
    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;

    public PageRequest(String historyToken) {
        int q = historyToken.indexOf('?');
        if (q >= 0) {
            pageName = historyToken.substring(0, q);
            ++q;
            for (;;) {
                int i = historyToken.indexOf('=', q);
                if (i <= q) {
                    break;
                }
                String key = historyToken.substring(q, i);
                q = i + 1;
                i = historyToken.indexOf('&', q);
                if (i < 0) {
                    i = historyToken.length();
                }
                paramMap.put(key, historyToken.substring(q, i));
                q = i + 1;
            }
        } else {
            pageName = historyToken;
        }
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Map<String, String> getParameterMap() {
        return paramMap;
    }

    public String toString() {
        return pageName + paramMap;
    }

    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    public void setServletConfig(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    public void setServletResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    /**
     * Set the value of parameter or remove it if value is null.
     */
    public void setParameter(String arg, String value) {
        if (value == null) {
            paramMap.remove(arg);
        } else {
            paramMap.put(arg, value);
        }
    }

    /**
     * Get the value of the parameter or null if not present.
     */
    public String getParameter(String param) {
        return paramMap.get(param);
    }

    /**
     * Get the value of the parameter returning def if no arg present.
     */
    public String getParameter(String param, String def) {
        String v = getParameter(param);
        return v == null ? def : v;
    }

    /**
     * Get the value of the parameter or false if not present or invalid.
     */
    public boolean getBooleanParameter(String param) {
        return "true".equals(getParameter(param));
    }

    /**
     * Get the value of the parameter returning def if no arg present.
     */
    public boolean getBooleanParameter(String param, boolean def) {
        String s = getParameter(param);
        return s != null && "true".equals(s) || def;
    }

    /**
     * Get the value of the parameter returning def if no arg present or if
     * invalid.
     */
    public int getIntParameter(String param, int def) {
        String s = paramMap.get(param);
        if (s != null) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return def;
    }

    /**
     * Get the value of the parameter returning def if no arg present or if
     * invalid.
     */
    public int getIntParameter(String param) {
        return getIntParameter(param, 0);
    }

    /**
     * Associate data with this request.
     */
    public void setAttribute(String key, Object value) {
        if (value == null) {
            removeAttribute(key);
        } else {
            if (attributeMap == null) {
                attributeMap = new HashMap<String, Object>();
            }
            attributeMap.put(key, value);
        }
    }

    /**
     * Get data from this request.
     */
    public Object getAttribute(String key) {
        return attributeMap == null ? null : attributeMap.get(key);
    }

    /**
     * Remove the attribute from this request.
     */
    public void removeAttribute(String key) {
        if (attributeMap != null) {
            attributeMap.remove(key);
        }
    }

    /**
     * Is a page being opened? WidgetDataProvider's can uses this information
     * to fetch data differently when a page is opened or a portlet is
     * refreshed (e.g. to not fetch data on the initial page open for a
     * slow portlet).
     */
    public boolean isOpenPage() {
        return openPage;
    }

    public void setOpenPage(boolean openPage) {
        this.openPage = openPage;
    }
}
