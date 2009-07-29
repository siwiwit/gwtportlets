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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sets HTTP response header so URI's containing .cache. are cached for 1 year
 * and URI's containing .nocache. are never cached. Add the following to
 * web.xml to use this filter:<br>
 *
 * <pre>
 *  &lt;filter&gt;
 *      &lt;filter-name&gt;cacheHeaderFilter&lt;/filter-name&gt;
 *      &lt;filter-class&gt;org.gwtportlets.portlet.server.CacheHeaderFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 *
 *  &lt;filter-mapping&gt;
 *      &lt;filter-name&gt;cacheHeaderFilter&lt;/filter-name&gt;
 *      &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 *  &lt;/filter-mapping&gt;
 * </pre>
 */
public class CacheHeaderFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain filterChain) throws IOException, ServletException {
        String uri = ((HttpServletRequest)req).getRequestURI();
        if (uri.contains(".cache.")) {
            ((HttpServletResponse)res).setDateHeader("Expires",
                    System.currentTimeMillis() + 31536000000L);
        } else if (uri.contains(".nocache.")) {
            ((HttpServletResponse)res).setHeader("Cache-Control", "no-cache");
        }
        filterChain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }

}
