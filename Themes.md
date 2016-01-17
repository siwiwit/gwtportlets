>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<

## Chapter 8. Theme Support ##

The framework themes its widgets using a singleton Theme instance, theme CSS files and Javascript maps. Several themes are bundled with the framework. The CSS for BlueGradient is included in gwt-portlets.css as it is the default theme. The selected theme stored in a cookie.

## 8.1. The Theme Singleton ##

The Theme class supports querying available themes and changing the theme:

```
package org.gwtportlets.portlet.client.ui;
...
public class Theme {
    /** Get the singleton Theme instance. */
    public static Theme get() {...}

    /** Get the name of the currently selected theme. */
    public String getCurrentTheme() {...}

    /** Get the names of the available themes.  */
    public String[] getThemes() {...}

    /**
     * Change to a different theme or NOP if the theme is already active.
     * Expects to find a gwt-portlets-<name>.css stylesheet (unless the default
     * 'BlueGradient' theme is selected) and an optional
     * gwt_portlets_<name> Javascript object with overrides for dimensions
     * of dialog headers and whatnot. <b>NB: This method reloads the
     * application if the theme is changed.</b>
     */
    public void changeTheme(String name) {...}
...
```

The demo includes a ThemeListPortlet to display and change the theme.

The list of available themes may be changed by defining a Javascript object in your bootstrap HTML file:

```
<script type="text/javascript">
var gwt_portlets = {
    themes: "BlueGradient, LightBlue, MyTheme"
};
</script>
```

## 8.2. Creating Themes ##

Defining a new theme that has the same dimensions for UI elements (e.g. dialog title bar height) involves creating just a CSS file. Here is `gwt-portlets-LightBlue.css`:

```
.portlet-dialog-header-bg,
.portlet-dialog-footer-bg {
    background-image: url( "img/portlet-dialog-blue.png" );
}

.portlet-dialog-sides-bg {
    background-image: url( "img/portlet-dialog-sides-blue.png" );
}

.portlet-title-header-bg {
    background-image: url( "img/portlet-caption-blue.gif" );
}

.portlet-dialog-content {
    background-color: #dae7f6;
}

.portlet-dialog-content-body {
    border: 1px solid #99bbe8;
}

.portlet-dialog-buttonbar {
    background-color: #dae7f6;
}

.portlet-title-body {
    border-left: 1px solid #99bbe8;
    border-right: 1px solid #99bbe8;
    border-bottom: 1px solid #99bbe8;
}
```

If your theme changes the dimensions of things (e.g. dialog title bar height) then you also need to define a Javascript object (typically in your bootstrap HTML file):

```
<script type="text/javascript">
var gwt_portlets_MyTheme = {
    titleBarHeight: 20;
    titleBarLeftWidth : 6;
    ...
};
</script>
```

The framework widgets ask the Theme singleton to apply the selected theme, typically when their style is set:

```
public class Dialog extends PopupPanel {
...
    public void setStyleName(String style) {
        ...
        Theme.get().updateDialog(this);
        ...
```

```
public class Theme {
...
    /** Configure the dimensions of the dialog. */
    public void updateDialog(Dialog dlg) {
        EdgeRow header = dlg.getHeader();
        header.setDimensions(0, dialogHeaderHeight, dialogHeaderLeftWidth,
            dialogHeaderRightWidth);
        ...
```

If you want your own widgets to use the same mechanism you may need to extend Theme and call Theme.set(Theme instance) in onModuleLoad.

>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<