<%@ page import="main.server.DemoPageProvider" %>
<%@ page import="org.gwtportlets.portlet.server.PageRequest" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String pageName = request.getParameter("page");
    String pageXml;
    if (pageName == null) {
        pageXml = "Use page parameter to choose page to display";
    } else {
        DemoPageProvider pageProvider = (DemoPageProvider)
                config.getServletContext().getAttribute("pageProvider");
        PageRequest pageRequest = new PageRequest(pageName);
        pageRequest.setServletRequest(request);
        pageXml = pageProvider.getPageXML(pageRequest);
        if (pageXml == null) {
            pageXml = "Page '" + pageName + "' not found";
        }
        pageXml = pageXml.replace("<", "&lt;").replace(">", "&gt;");
    }
%>

<pre style="font-family: monospace"><%=pageXml%></pre>
