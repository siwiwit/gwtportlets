GWT Portlets is a free open source web framework for building GWT (Google Web Toolkit) applications. It defines a very simple & productive, yet powerful programming model to build good looking, modular GWT applications.

The programming model is somewhat similar to writing JSR168 portlets for a portal server (Liferay, JBoss Portal etc.). The "portal" is your application built using the GWT Portlets framework as a library. Application functionality is developed as loosely coupled Portlets each with an optional server side DataProvider.

![![](http://gwtportlets.googlecode.com/svn/wiki/img/screenshot1_thumb2.gif)](http://gwtportlets.googlecode.com/svn/wiki/img/screenshot1.gif)

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

## Quick Start ##

The main demo is [available online](http://095-beta.latest.gwtportletdemo.appspot.com/) (running on the Google App Engine) for the really impatient.

_Note that you edit pages in the demo by clicking the spanner in the bottom right hand corner of the browser client area._

For the slightly less impatient:

  1. Download and unzip the latest distribution
  1. Edit build.properties to match your environment (set your GWT installation directory etc.)
  1. Run ant (no arguements) at a command prompt from the root of the distribution. This will copy gwt-portlets.jar, other required jars and build.properties to each of the demos. Each demo will now be a standalone application i.e. you can copy the whole directory elsewhere to start a project
  1. Change to demos/main and run "ant" (no arguements) to launch the demo in hosted mode.

The following software packages need to be installed:

  * JDK 1.6.0 or newer
  * GWT 1.7.0 or newer (GWT 1.6.4 will also work but we test with 1.7.0)
  * Apache Ant 1.7.0 or newer

## Documentation ##

The GWT Portlets framework user manual is included in the download distribution (PDF and single HTML page). It is also available online in several formats:

  * [On This Wiki](Introduction.md)
  * [PDF](http://gwtportlets.googlecode.com/svn/wiki/doc/manual.pdf)
  * [Single HTML page](http://gwtportlets.googlecode.com/svn/wiki/doc/manual.html)

## Credits ##

GWT Portlets was developed and released as open source by [Business Systems Group (Africa)](http://www.bsg.co.za/web/guest/software_solutions).

[![](http://gwtportlets.googlecode.com/svn/wiki/img/bsg_logo_small.gif)](http://www.bsg.co.za/web/guest/software_solutions)