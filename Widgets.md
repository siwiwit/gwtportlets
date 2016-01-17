>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<

## Chapter 7. UI Widgets ##

The framework includes useful portlets and the widgets needed for its own UI (to avoid having to depend on other GWT libraries). The aim of the GWT Portlets framework is to make it easier to produce modular, decoupled business applications using GWT, not to create a widget library.

## 7.1. Dialog and CssButton ##

Dialog is a replacement for the standard GWT dialog box. It includes a title bar with maximize/restore and close buttons, content area, button bar, is styled using CSS and image sprites, is themable, prevents the application from receiving events (even mouse overs and so on) when modal, triggers close when escape is presssed and absolutely positions its contents.

![http://gwtportlets.googlecode.com/svn/wiki/doc/img/dialog.gif](http://gwtportlets.googlecode.com/svn/wiki/doc/img/dialog.gif)

**Figure 7.1. Dialog and CssButton**

CssButton is a Button subclass styled using a CSS background image sprite. It is lightweight (rendered using a single BUTTON element) and supports rollover. It selects different background sprites based on the width of the button avoiding scaling effects.

Here is a code fragment taken from FreeMemoryPortlet that creates its configuration dialog:

```
package demo.client.ui;
...
public class FreeMemoryPortlet extends Portlet {
...
    public void configure() {
        final ListBox type = ...
        FormBuilder b = new FormBuilder();
        b.label("Chart type").field(type).endRow();

        final Dialog dlg = new Dialog();
        dlg.setText("Configure " + getWidgetName());
        dlg.setWidget(b.getForm());
        dlg.addButton(new CssButton("Revert", new ClickHandler() {
            public void onClick(ClickEvent ev) {...}
        }, "Undo changes"));
        dlg.addCloseButton();
        dlg.showNextTo(this);
    }
...
```

The content area of the dialog can be populated with a single widget by calling setWidget (like a standard GWT DialogBox) or multiple widgets can be added (getContent().add(Widget,...)). The setWidget method wraps widgets with a TABLE element in a SimplePanel (DIV) styled to add 4px padding.

The body of the Dialog is a RefreshPanel. This will display a AJAX loading pizza inside the dialog if it contains a Portlet and the portlet is refreshed.

The showNextTo method will position the dialog next to another widget. If there is more space to the right or left then the dialog will be positioned there, otherwise it is placed below or above. It will center the dialog if there is not enough space anywhere.

## 7.2. FormBuilder ##

FormBuilder is not actually a widget itself, it creates a form (labels and fields etc.) based on a GWT FlexTable

![http://gwtportlets.googlecode.com/svn/wiki/doc/img/formbuilder1.gif](http://gwtportlets.googlecode.com/svn/wiki/doc/img/formbuilder1.gif)

**Figure 7.2. FormBuilder**

It keeps track of the current row and column in the table and creates labels and fields using standard styles. Some methods add new cells and others operate on the most recently added cell as shown in the following code fragment:

```
CheckBox column = new CheckBox("Layout in column");
TextBox spacing = new TextBox();

FormBuilder b = new FormBuilder();
b.label("Spacing").field(spacing).endRow();
b.field(column).colspan(2).endRow(); // checkbox spans 2 columns
FlexTable form = b.getForm();
```

FormBuilder styles the table so that the spacing between TDs inside is even (default is 4px) but the outer TDs (first row, last row, first column and last column) do not have any padding on the outside. This makes it easier to nest forms, to maintain consistent spacing and avoid problems with tables and spacing inside scrolling regions.

## 7.3. TitlePanel ##

TitlePanel is a Portlet and Container that contains other portlets. Each of the portlets in the home page of the demo is contained in a TitlePanel. It provides a title bar, refresh, configure and maximize buttons and displays an AJAX loading pizza when the first Portlet it contains is refreshing.

![http://gwtportlets.googlecode.com/svn/wiki/doc/img/title_panel_dialog.gif](http://gwtportlets.googlecode.com/svn/wiki/doc/img/title_panel_dialog.gif)

**Figure 7.3. TitlePanel**

The dialog above is displayed by clicking a TitlePanel in the page editor and selecting Edit Title... from the popup menu. The options are as follows:

  * Automatically use title from content Display the title provide by the first portlet in the panel or use the title captured here
  * Refresh button Display a refresh button in the title bar that refreshes the contents of the panel
  * Configure button Display a configure button if the first portlet in the panel supports configure. This invokes the portlets configure() method
  * Maxmimize button Display a maximize button that will expand the TitlePanel up to the next maximize limit point (typically whole page content area)
  * Limit size of maximizing child widgets Contained widgets with maximize support (e.g. nested TitlePanel's) will maximize to the boundarys of this TitlePanel before getting bigger
  * User may select contained widget using configure Users may select a different Portlet to go into this TitlePanel using the configure button. If the currently contained Portlet supports configure then the user is prompted to replace it with something else or configure it

## 7.4. WebAppContentPortlet ##

The WebAppContentPortlet displays any content served from the web application including JSP pages, static HTML, servlets and so on. It is configured with the path to the content as shown in the main demo (demos/main).

## 7.5. MenuPortlet ##

The MenuPortlet displays a lightweight HTML menu generated from an HTML template served from the web application (a JSP page, static HTML etc.). It is configured with the path to the template as shown in the main demo (demos/main)

The template for the demo application (menu.html) is shown below:

```
<a href="#home">Home</a>
<a href="#portlets()">Portlets</a>
<a href="#commands()">Commands</a>

<div id="portlets">
    <a href="#hello_world">Hello World</a>
    <a href="#free_memory">Free Memory</a>
    <a href="#web_app_content">Web App Content</a>
    <a href="#row_layout">Row Layout</a>
</div>

<div id="commands">
    <a href="#command_demo">Command Demo</a>
    <a href="#command1(arg1,arg2)">Command1</a>
    <a href="#command2()">Command2</a>
    <a href="#command3()">Command3</a>
</div>
```

  * Menu items are normal links and can contain history tokens ('`#home`'), external links and commands ('`#command1(arg1,arg2)`'). Note that other HTML elements may be present
  * Submenus are identified by top level DIVs with id attributes e.g. `<div id="portlets">`
  * Menu items that activate sub menus have a matching href attribute and round brackets e.g. `<a href="#portlets()">Portlets</a>`
  * History token links with round brackets that do not activate sub menus broadcast a CommandEvent to all widgets when clicked

The CommandDemoPortlet on command demo page displays the most recent CommandEvent received.

## 7.6. PagePortlet ##

PagePortlet listens for PageChangeEvent's and displays the widgets for the new page in its "content" area. If the page is editable then an edit button is displayed that launches the page editor on click.

It updates the browser window title and broadcasts a PageTitleChangeEvent when the title of the first Portlet on the page changes. The application title and prefix used to construct the browser window title can be configured by selecting Configure... for on the PagePortlet in the PageEditor.

It is styled `portlet-page` by default.

## 7.7. PageTitlePortlet ##

PageTitlePortlet listens for PageTitleChangeEvent's and updates a title label. It is styled `portlet-page-title` by default.

## 7.8. ToolButton ##

ToolButton displays a small icon defined by a CSS background image sprite with rollover and disabled support.

```
ToolButton edit = new ToolButton(ToolButton.CONFIGURE, "Edit Page", new ClickListener() {
    public void onClick(Widget sender) {...}
});
```

## 7.9. ShadowPanel ##

ShadowPanel adds a fuzzy shadow to a single widget. The look of the shadow is controlled by the theme.

```
Widget w = ...
ShadowPanel sp = new ShadowPanel(w);
```

>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<