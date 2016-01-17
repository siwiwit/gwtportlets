>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<

## Chapter 4. Application Structure ##

This chapter describes how to pull the different parts of the framework together to create a complete application using one of the the bundled demos (demos/main in the distribution) as an example.

## 4.1. Classes ##

The main client side classes (main.client package) are shown in the class diagram below (blue classes are part of the GWT portlets framework, yellow classes are part of GWT):

![http://gwtportlets.googlecode.com/svn/wiki/doc/img/demo_client_classes.gif](http://gwtportlets.googlecode.com/svn/wiki/doc/img/demo_client_classes.gif)

**Figure 4.1. Main Demo Client Side Classes**

  * `Demo` The EntryPoint (main) class
  * `DemoService` The service interface (Async version not shown)
  * `DemoPage` DTO used to transfer WidgetFactory trees (pages) and related information from the server to client. A real application might include information on the currently logged on user etc.
  * `DemoPageEditor` The page editor for in browser layout editing, knows how to save pages to the server
  * `ClientAreaPanel` (framework) Container to fill the whole browser client area and layout child containers

The server side classes (main.server package):

![http://gwtportlets.googlecode.com/svn/wiki/doc/img/demo_server_classes.gif](http://gwtportlets.googlecode.com/svn/wiki/doc/img/demo_server_classes.gif)

**Figure 4.2. Main Demo Server Side Classes**

  * `DemoServiceImpl` GWT RemoteServiceServlet implementing demo.client.DemoService methods
  * `DemoPageProvider` Loads and saves "pages" (widget and portlet layouts) of the application from xml files and refreshes portlets
  * `WidgetFactoryXmlIO` (framework) Used to convert WidgetFactory trees to/from XML using XStream
  * `PageRequest` (framework) Analogous to an HttpServletRequest. It parses the history token into a page name and parameters and provides a store for per-request attributes (to share data between WidgetDataProviders) and the actual servlet request, response and context

The following (abridged) sequence diagram shows how the EntryPoint class for the demo application bootstraps the GUI from onModuleLoad():

![http://gwtportlets.googlecode.com/svn/wiki/doc/img/get_root_page_seq.gif](http://gwtportlets.googlecode.com/svn/wiki/doc/img/get_root_page_seq.gif)

**Figure 4.3. Main Demo Application Bootstrap Process**

The DemoPage DTO contains a WidgetFactory tree for the root page and for the page loaded for the history token. It a real application it would also contain information about the currently logged on user and so on. The rootWidgetFactory is used to create a tree of Widgets that is added to the ClientAreaPanel. The root page tree should contain a PagePortlet (and other widgets and portlets).

```
public class DemoPage implements Serializable {
    public String pageName;
    public WidgetFactory widgetFactory;
    public WidgetFactory rootWidgetFactory;
    public boolean canEditPage; // is page editable?
}
```

The PagePortlet portlet displays the current page by listening for PageChangeEvents and swapping out a new tree of widgets in a content area on page changes. If the page is editable then an edit button is displayed that launches the online layout editor on click. After the ClientAreaPanel has been populated with the root page, a PageChageEvent containing the widgetFactory tree (page) for the history token is broadcast to all the widgets in the tree. This is picked up by the PagePortlet.

```
<!-- Part of root.xml from the demo -->
<LayoutPanel styleName="" limitMaximize="false">
  ...
  <PagePortlet styleName="portlet-page"
        appTitle="GWT Portlets Demo"
        prefix="GWT Portlets Demo: "/>
  ...
```

Each time the history token changes (onHistoryChanged()), Demo calls DemoServiceImpl.getPage(historyToken) and the returned DemoPage DTO contains the page for the history token widgetFactory (rootWidgetFactory is null). A PageChangeEvent is broadcast and the PagePortlet displays the new page.

## 4.2. Event Broadcasting ##

The BroadcastManager singleton supports broadcasting of application wide events to widget trees. Only widgets that implement BroadcastListener receive broadcast events. This mechanism makes it easy for "global" events to be communicated to all parts of the GUI in a decoupled way.

```
package org.gwtportlets.portlet.client.event;
...
public class BroadcastManager {
...
    /**
     * Send the object to all widgets from the RootPanel down.
     */
    public void broadcast(Object ev) {...}

    /**
     * Send the object up to the logical parent of w (and its logical parent
     * and so on) until a widget with no logical parent is reached.
     */
    public void broadcastUp(Widget w, Object ev) {...}
...
```

For example the Demo application broadcasts a PageChangeEvent when the history token changes and a new page is loaded from the server. The PagePortlet uses this event to display the new page. It in turn broadcasts a PageTitleChangeEvent when it receives a WidgetChangeEvent from the widget on the page supplying the title. The PageTitlePortlet updates its title bar when it receives the event.

```
package org.gwtportlets.portlet.client.ui;
...
public class PagePortlet extends ContainerPortlet implements BroadcastListener {
...
    public void onBroadcast(Object ev) {
        if (ev instanceof PageChangeEvent) {
            onPageChange((PageChangeEvent)ev);
        } else if (ev instanceof WidgetChangeEvent
                && ((WidgetChangeEvent)ev).getSource() == titlePortlet) {
            updateTitle();
        }
    }
...
    private void updateTitle() {
        ...
        BroadcastManager.get().broadcast(new PageTitleChangeEvent(this, title));
    }
...
```

```
package org.gwtportlets.portlet.client.ui;
...
public class PageTitlePortlet extends Portlet implements BroadcastListener {
    private Label label = new Label("Page Title");
    ...
    public void onBroadcast(Object ev) {
        if (ev instanceof PageTitleChangeEvent) {
            label.setText(((PageTitleChangeEvent)ev).getPageTitle());
        }
    }
...
```

All Portlets must call boardcastUp with a WidgetChangeEvent when their title or flags change. This happens automatically when a Portlet refreshes itself. TitlePanel uses this event to show or hide a loading spinner, to update its title bar and to decide which buttons to display (configure, refresh etc.).

Applications can broadcast their own objects instead of having to couple components together directly (e.g. a LoggedOnUserPortlet might display a name and role from a LoggedOnEvent).

The BroadcastManager also supports non-widget listeners for broadcast events:

```
package org.gwtportlets.portlet.client.event;
...
public class BroadcastManager {
...
    /**
     * Add listener to be notified on calls to broadcast before the
     * event is dispatched to the widget tree.
     */
    public void addObjectBroadcastListener(BroadcastListener l) {...}
    public void removeObjectBroadcastListener(BroadcastListener l) {..}
...
```

## 4.3. The WidgetRefreshHook Singleton ##

The framework does not have its own service interface. Instead it relies a WidgetRefreshHook singleton to refresh a Portlet or Widget with new data from the server. This is normally set in onModuleLoad and just invokes a service method:

```
package main.client;
...
public class Demo implements EntryPoint {
    ...
    public void onModuleLoad() {
        WidgetRefreshHook.App.set(new WidgetRefreshHook() {
            public void refresh(Widget w, WidgetFactory wf,
                    AsyncCallback<WidgetFactory> cb) {
                DemoService.App.get().refresh(History.getToken(), wf, cb);
            }
            public void onRefreshCallFailure(Widget w, Throwable caught) {
                Window.alert("Refresh failed: " + caught);
            }
        });
        ...
```

The implementation of the service method delegates to the PageProvider to refresh the WidgetFactory tree. This simple mechanism makes it possible to refresh any portlet (actually any tree of Widgets implementing WidgetFactoryProvider) with new data without having to a new service method and code a specific async call.

```
package main.server;
...
public class DemoServiceImpl extends RemoteServiceServlet
        implements DemoService {
    private DemoPageProvider pageProvider;
    ...
    public WidgetFactory refresh(String historyToken, WidgetFactory wf) {
        pageProvider.refresh(createPageRequest(historyToken), wf);
        return wf;
    }

    private PageRequest createPageRequest(String historyToken) {
        PageRequest req = new PageRequest(historyToken);
        req.setServletConfig(getServletConfig());
        req.setServletRequest(getThreadLocalRequest());
        req.setServletResponse(getThreadLocalResponse());
        return req;
    }
...
```

## 4.4. WidgetFactory Trees and Data Providers ##

The Demo application service implementation refreshes WidgetFactory trees by delegating to the DemoPageProvider as show above. The code that handles the refresh is in the PageProvider base class and is the same code that runs after a page XML file has been read:

```
package org.gwtportlets.portlet.server;
...
public abstract class PageProvider {
...
    /** Refresh the data in the widget factory tree starting at top. */
    public void refresh(final PageRequest req, final WidgetFactory top) {
        top.accept(new WidgetFactoryVisitor() {
            public boolean visit(WidgetFactory wf) {
                WidgetDataProvider p = findWidgetDataProvider(wf);
                if (p != null) {
                    try {
                        p.refresh(wf, req);
                    } catch (Exception e) {
                        handleRefreshException(req, top, wf, e);
                    }
                }
                return true;
            }
        });
    }

    public WidgetDataProvider findWidgetDataProvider(WidgetFactory wf) {
        return providerMap.get(wf.getClass());
    }

    public void add(WidgetDataProvider p) {
        Class<? extends WidgetFactory> key = p.getWidgetFactoryClass();
        if (key == null) {
            throw new IllegalArgumentException("null not supported");
        }
        providerMap.put(key, p);
    }
...
```

Each WidgetFactory in the tree is visited and refreshed by a WidgetDataProvider. The provider is found by a simple map lookup using the class of the factory as the key. Note that WidgetDataProvider implementations need to be thread safe.

>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<