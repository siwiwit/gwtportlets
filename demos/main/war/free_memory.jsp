<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    int freeK = (int)(Runtime.getRuntime().freeMemory() >> 10);
    int totK = (int)(Runtime.getRuntime().totalMemory() >> 10);
    int freeP = (int)(freeK * 100.0 / totK + 0.5);
    String chartUrl = "http://chart.apis.google.com/chart?cht=gom&chd=t:" +
            freeP + "&chs=200x100";

%>

<p>This is a WebAppContentPortlet displaying HTML provided by a JSP page.</p>

<p><img src="<%=chartUrl%>" alt="Google Chart" width="200" height="100"></p>

<p>Free memory: <%=freeK%> / <%=totK%> KB </p>
