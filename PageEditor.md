>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<

## Chapter 6. The Page Editor ##

The framework supports editing of pages (actually any tree of Containers and Portlets) in the browser. The application using the framework controls how the page editor is launched and what happens when the user "saves" a page or tree. The demo application uses the PagePortlet to display pages and this portlet displays a spanner icon on the bottom right hand corner of the client area for editable pages.

The appropriate code fragments from the demo are shown below:

```
package demo.client;
...
public class Demo implements EntryPoint ... {
...
    // The pageEditor is responsible for editing and saving pages (extends PageEditor)
    private DemoPageEditor pageEditor = new DemoPageEditor();
...
    private void onPageChange(final DemoPage p) {
        ...
        // The page change event knows how to edit the current page
        PageChangeEvent pce = new PageChangeEvent(this) {
            public void editPage(Container container) {
                ...
                pageEditor.startEditing(getPageName(), container);
            }
        };
        pce.setPageName(p.pageName);
        pce.setEditable(p.canEditPage);
        pce.setWidgetFactory(p.widgetFactory);

        // Send the event to every AppEventListener in the container tree.
        // The PagePortlet uses this event to change the widget tree in the
        // 'content area' of the application and to display the gear icon
        // for editable pages
        BroadcastManager.get().broadcast(pce);
    }
...
```

The PagePortlet calls editPage on the PageChangeEvent when the user clicks the gear icon for an editable page and the demo starts editing using its PageEditor subclass:

```
package demo.client;
...
public class DemoPageEditor extends PageEditor {
...
    protected void savePage(WidgetFactory wf, AsyncCallback callback) {
        DemoService.App.get().savePage(getPageName(), wf,
                new AsyncCallback() {
            public void onFailure(Throwable caught) {
                Window.alert("Oops " + caught);
            }
            public void onSuccess(Object result) {
                Window.alert("Saved");
            }
        });
    }
}
```

The only thing required from a PageEditor subclass is a savePage method. Override other methods if you need to customize the editor further.

## 6.1. Using The Editor ##

The following screenshot (taken from a modifled version of the demo home page) shows the major components of the editor:

![http://gwtportlets.googlecode.com/svn/wiki/doc/img/page_editor1.gif](http://gwtportlets.googlecode.com/svn/wiki/doc/img/page_editor1.gif)

**Figure 6.1. Layout Editor Screenshot**

The Dialog titled "Layout Editor" displays a button for each level in the Container tree being edited ("1", "2" etc.) with the selected level highlighted.

The image below shows how levels in the editor correspond to levels in the container tree. You can click the buttons or use the mouse wheel to change levels. There is also a context sensitive message explaining what do ("Drag widgets to move or resize") and a save button. Closing this dialog stops editing.

![http://gwtportlets.googlecode.com/svn/wiki/doc/img/page_editor_tree.gif](http://gwtportlets.googlecode.com/svn/wiki/doc/img/page_editor_tree.gif)

**Figure 6.2. Page Editor Container Tree Levels**

Each widget on the current level on the tree is outlined by a blue rectangle. These rectangles can be dragged around to move widgets to different positions at the same level in the tree. The thick edge of the rectangle resizes the widget and left click opens a context sensitive menu (not all the options listed below are visible in the screenshot):

  * Edit Constraints... Opens a dialog to edit layout constraints for the widget
  * Pickup Pickup the widget and click to drop it somewhere else. Change levels (use the mouse wheel) while "holding" a widget to move widgets between levels in the tree
  * Delete... Delete the widget
  * Replace With > Replace the widget with a different widget. If the new widget is a Container then the widget is placed inside the container. This is very useful for "splitting" the space occupied by a widget into a row or column and for putting widgets inside TitlePanel's
  * Style > Set the CSS style for the widget. The list of styles can be changed by overriding PageEditor.getStyleNamesFor(Widget)
  * Edit... Edit settings for the widget that was clicked (in this case a TitlePanel)
  * Configure... Configure the Portlet that was clicked (if it supports configure e.g. FreeMemoryPortlet)
  * Parent Container > Edit settiings for the parent container of the clicked widget
  * Undo Undo the last action
  * Redo Redo the last undone action
  * Add > Select a new widget or container and click to drop it

Don't forget to click Save to save changes.

>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<