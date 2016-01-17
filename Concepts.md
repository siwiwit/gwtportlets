>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<

## Chapter 3. Other Concepts ##

This chapter describes the key concepts and ideas used by the framework.

## 3.1. The Open Page Process ##

Page layouts are loaded when the history token changes e.g. the user clicks on an internal link or bookmark. The history token is used to select a page file to load as shown in the following diagram:

![http://gwtportlets.googlecode.com/svn/wiki/doc/img/open_page_process.gif](http://gwtportlets.googlecode.com/svn/wiki/doc/img/open_page_process.gif)

**Figure 3.1. Open Page Process**

## 3.2. The Portlet Refresh Process ##

All portlets can be refreshed by calling `Portlet.refresh()`. This might be triggered by a user clicking the refresh button on a TitlePanel containing a Portlet or by the Portlet itself. The Portlet is asked to provide a PortletFactory instance containing its state and this is sent to the server to be updated as shown in the following diagram:

![http://gwtportlets.googlecode.com/svn/wiki/doc/img/refresh_process.gif](http://gwtportlets.googlecode.com/svn/wiki/doc/img/refresh_process.gif)

**Figure 3.2. Portlet Refresh Process**

## 3.3. WidgetFactory Trees ##

Container extends WidgetFactoryProvider so all container implementations support externalizing their state into a serializable WidgetFactory subclass that can restore that state (like Portlets). A tree of Containers and Widgets implementing WidgetFactoryProvider (e.g. Portlets) can be externalized into a tree of WidgetFactories and recreated with a few lines of code:

```
Container root = ...; // tree of Containers and Portlets

// get tree of WidgetFactories
WidgetFactory wf = root.createWidgetFactory();

// create a copy of the original tree (root)
Widget w = wf.createWidget();
wf.refresh(w);
```

Because WidgetFactories are serializable it is easy to transfer WidgetFactory trees between the client and server. This mechanism is also used to implement undo and redo and other features in the layout editor and to store layouts in XML files.

The WidgetFactory interface also supports the visitor pattern for easy traversal of factory trees (e.g. when populating a tree with data from database on the server).

```
WidgetFactory wf = ...;
// visit is invoked for each factory in the tree
wf.accept(new WidgetFactoryVisitor(){
    public boolean visit(WidgetFactory wf) {...}
});
```

## 3.4. Page Files ##

The framework supports the conversion of WidgetFactory trees to/from XML using XStream<sup>[[#ftn.d0e371 1]]</sup>. Note there is no dependency on XStream i.e. other technologies can be used to persist WidgetFactory trees. However XStream is fast and produces human readable and editable XML with a minimum of configuration.

Here is part of the hello\_world page from the demo:

```
<LayoutPanel styleName="" limitMaximize="false">
  <widgets>
    <LayoutPanel styleName="" limitMaximize="false">
      <widgets>
        <LayoutPanel styleName="" limitMaximize="false">
          <widgets>
            <TitlePanel styleName="portlet-title" title="Title" titleAuto="true"
                    refresh="true"
                    configure="true" edit="false" maximize="true" limitMaximize="true">
              <widgets>
                <HelloWorldPortlet styleName=""/>
              </widgets>
              <constraints>
                <RowLayout-Constraints size="0.0" weight="1.0" maxSize="0"
                    overflow="hidden"/>
              </constraints>
...
</LayoutPanel>
```

Storing the layout of an application GUI in XML files on the server has several advantages over hardcoding it into the Javascript:

  * The application has looser coupling i.e. the EntryPoint class does not have to "know about" all of its GUI components
  * All factories on a page can be populated with data in a single page fetch async call
  * The layout can be customized for different installations or themes

The XStream support is provided by WidgetFactoryXmlIO. This class uses XStream's alias support to avoid putting fully qualified class names into the XML files. Note that if you rename a Portlet (other than moving it to a new package) you will need to update your page files. Some simple heuristics are used to convert a fully qualified class name into a user friendly alias as described below:

```
package org.gwtportlets.portlet.server;
...
public class WidgetFactoryXmlIO {
...
    /**
     * Add an alias for cls. The alias is the simple name of the class (i.e.
     * without package) with the following modifications:<br>
     * <li>Any 'Factory' suffix is removed
     * <li>Any '$' is replaced with '-' (inner class names)
     * Also omits fields annotated with DoNotPersist or DoNotSendToServer.
     */
    public void alias(Class cls) {...}
...
```

It aliases all the framework classes in its constructor. Applications will usually use Spring or a similar framework to discover all of the PortletFactory's and alias them at initialisation time.

As mentioned in the Javadoc comment in the code fragment, fields annotated with DoNotPersist or DoNotSendToServer are not included in the XML. This is useful when fields in a PortletFactory are used only on the client side (DoNotSendToServer) or are used to pass information back to the server (DoNotPersist) that should not not end up in page XML files.

## 3.5. Containers & Layouts ##

The framework provides support for absolute positioning of widgets within each other and the browser viewport. Widgets (e.g. Portlets) are arranged using constraints and layout managers in a manner similar to Swing and AWT. This approach makes it possible to create scrolling regions and to build a browser based "desktop style" application. Static (i.e. normal browser flow) positioning can still be used for the "contents" of widgets and is often easier than trying to control the position and size of every button and label using absolute positioning.

The Container interface extends GWTs concept of a "Panel" to add support for pluggable layout managers and layout constraints:

```
package org.gwtportlets.portlet.client.layout;
...
public interface Container extends HasWidgets, IndexedPanel, WidgetFactoryProvider,
          PositionAware {
    ...
    public Layout getLayout();
    public void setLayout(Layout layout);
    public LayoutConstraints getLayoutConstraints(Widget widget);
    public void setLayoutConstraints(Widget widget, LayoutConstraints constraints);

    /**
     * Redo this containers layout. Note that containers do not automatically
     * call layout() when widgets are added/removed etc. Only resizing the
     * container triggers automatic layout.
     */
    public void layout();
    ...
}
```

Note that Container extends PositionAware. PositionAware widgets are notified when their position and/or size may have been updated by a call to the boundsUpdated() method from the interface. Containers redo their layouts and reposition their children in response to a boundsUpdated() call.

Two layouts are included with the framework:

  * `RowLayout` Lays out widgets in a row or a column with using minimum sizes and weights to make use of available space. Nested containers using RowLayout can be used to construct "border layout" and others
  * `DeckLayout` Places widgets on top of each other. Useful for creating "tab panels" and for putting AJAX loading pizzas above other widgets etc.

LayoutPanel is a general purpose Container implementation used by many of the Widgets in the framework. It is covered in detail on this page and here is a small sample:

```
LayoutPanel panel = new LayoutPanel(); // defaults to RowLayout in a column
panel.add(chart); // use all free space and include scrollbars if needed
panel.add(label, 24); // 24 pixels high, no scrollbars (overflow is hidden)
panel.layout(); // adding widgets does not automatically redo the layout
```


---


<sup>[[#d0e371 1]] </sup>http://xstream.codehaus.org/

>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<