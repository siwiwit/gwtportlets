>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<

## Chapter 1. Introduction ##

GWT Portlets (http://code.google.com/p/gwtportlets/) is a free open source web framework for building GWT (Google Web Toolkit) applications. It defines a very simple & productive, yet powerful programming model to build good looking, modular GWT applications.

![http://gwtportlets.googlecode.com/svn/wiki/doc/img/what_you_get.gif](http://gwtportlets.googlecode.com/svn/wiki/doc/img/what_you_get.gif)

**Figure 1.1. Overview Of Functionality**

The programming model is somewhat similar to writing JSR168 portlets for a portal server (Liferay, JBoss Portal etc.). The "portal" is your application built using the GWT Portlets framework as a library. Application functionality is developed as loosely coupled Portlets each with an optional server side DataProvider.

Every Portlet knows how to externalize its state into a serializable PortletFactory subclass (momento / DTO / factory pattern) making important functionality possible:

  * CRUD operations are handled by a single GWT RPC for all Portlets
  * The layout of Portlets on a "page" can be represented as a tree of WidgetFactory's (an interface implemented by PortletFactory)
  * Trees of WidgetFactory's can be serialized and marshalled to/from XML on the server, to store GUI layouts (or "pages") in XML page files

Other important features of the framework are listed below:

  * Pages can be edited in the browser at runtime (by developers and/or users) using the framework layout editor
  * Portlets are positioned absolutely so can use scrolling regions
  * Portlets are configurable, indicate when they are busy loading for automatic "loading spinner" display and can be maximized
  * Themed widgets including a styled dialog box, a CSS styled button replacement, small toolbuttons and a HTML template driven menu

GWT Portlets is implemented in Java code and does not wrap any external Javascript libraries. It does not impose any server side framework (e.g. Spring or J2EE) but is designed to work well in conjunction with such frameworks.

## 1.1. Contents & Usage ##

The download distribution includes binaries, source code, demos and documentation. Everything required to use and build the framework is included. Some files required to build this manual have been excluded to reduce the size of the distribution i.e. the documentation must be built from a subversion checkout.

The framework is packaged as `gwt-portlets.jar` and this file must be on the classpath at compile and runtime (e.g. in WEB-INF/lib). It depends on Log4j (log4j-**.jar) and optionally XStream (xstream-**.jar, xpp3\_min-**.jar and xmlpull**.jar). These jars and their licenses are included in the download bundle (`lib` directory).

Add the following line to the GWT module file for your application to use GWT Portlets:

`<inherits name="org.gwtportlets.portlet.Portlets"/>`

## 1.2. System Requirements ##

The GWT Portlets framework has the following system requirements:

  * JDK 1.5 or newer
  * Google Web Toolkit 1.7 or newer
  * Apache Ant 1.7 or newer is required to run the demo applications

Currently it will work with GWT 1.6.x but our development and testing is done on GWT 1.7.

## 1.3. Demo Applications ##

The demos (there is only one at present) are standalone applications with their own Ant build files. Each is in a separate directory under `demos`.

To run the demos you need to:

  * Edit `build.properties` to match your environment (set your GWT installation directory etc.)
  * Run `ant` (no arguements) at a command prompt from the root of the distribution. This will copy `gwt-portlets.jar`, other required jars and `build.properties` to each of the demos. Each demo will now be standalone (you can copy the whole directory elsewhere to start a project)

Change to `demos/main` and run `ant -p` to list targets. Running `ant` (no arguements) will launch the demo in hosted mode.

## 1.4. Packages ##

The framework package layout follows standard GWT conventions:

| org.gwtportlets.portlet.client | Client side classes that are compiled to Javascript to run in the browser |
|:-------------------------------|:--------------------------------------------------------------------------|
| org.gwtportlets.portlet.client.edit | The in-browser layout editor                                              |
| org.gwtportlets.portlet.client.event | Application event broadcast support                                       |
| org.gwtportlets.portlet.client.impl | Browser specific classes and interfaces for compile time generated code   |
| org.gwtportlets.portlet.client.layout | Layout strategies                                                         |
| org.gwtportlets.portlet.client.ui | Widget library                                                            |
| org.gwtportlets.portlet.client.util | Miscelaneous utility classes                                              |
| org.gwtportlets.portlet.rebind | GWT compile time code generators                                          |
| org.gwtportlets.portlet.public | Stylesheets and images                                                    |
| org.gwtportlets.portlet.server | Server side support                                                       |

>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<