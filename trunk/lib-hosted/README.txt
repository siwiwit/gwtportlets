The JSP compiler (Jasper) used by GTW hosted mode in GWT 1.6.4+ does not
support JSP files using JDK 1.5 features (foreach etc.). This directory
includes a version of Jasper that can be configured in web.xml to allow 1.5
level source. It needs to be on the classpath ahead of gwt-dev-*.jar when
hosted mode is used without the -noserver option.

Please see build.xml and web.xml from demos/main for an example.

This fix was found here:

http://code.google.com/p/raisercostin/wiki/GwtEclipsePluginDebug
