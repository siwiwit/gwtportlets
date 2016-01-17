>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<

## Chapter 5. Layouts ##

GWT Portlets provides an absolute positioning framework with pluggable layout managers (similar to Swing and other thick client GUI toolkits). This chapter explains how LayoutPanel and its default RowLayout work.

## 5.1. RowLayout ##

The default layout of a LayoutPanel is RowLayout. This flexible layout manager supports arranging widgets in a horizontal row (hence the name) or in a vertical column. In both cases the spacing between widgets in pixels is controlled by an int spacing property (default is 4 pixels). LayoutPanel uses the column mode by default.

The size of each widget is controlled by a RowLayout.Constraints instance with the following properties:

  * `float size` The size of the widget in pixels (e.g. 100.0) or the fraction (0.3 for 30%) of available space it should take up
  * `float weight` Weighting used to allocate extra space proportionally among widgets with weight > 0
  * `int maxSize` Maximum size of the widget in pixels or 0 for no limit
  * `String overflow` Value for overflow CSS style attribute to control scrollbars and clipping of the widgets content. Use one of the constants from LayoutConstraints: VISIBLE (don't clip), HIDDEN (clip), SCROLL (always show scrollbars) and AUTO (show scrollbars if needed but see warning below)

Once a widget has been positioned by a RowLayout the following additional read only properties are available:

  * `int actualSize` The actual size of the widget in pixels
  * `int extraSize` The extra space allocated to it in pixels (according to its weight)

In row mode the size sets the width of each widget and the height is the height of the container. In column mode the size sets the height of each widget and the width is the width of the container.

Widgets that implement HasMaximumSize are centered in the rectangle assigned to them if its width or height exceeds the maximums.

The interactive RowLayout demo provides an environment to experiment with RowLayout and its constraints.

## 5.2. Using LayoutPanel ##

Container (implemented by LayoutPanel) has several convenient add methods that make creating many popular layouts easier:

```
package org.gwtportlets.portlet.client.ui;
...
public  class LayoutPanel extends ComplexPanel implements Container {
...
    /** Add a widget with layout constraints. This does not redo the layout.  */
    public void add(Widget widget, LayoutConstraints constraints) {...}

    /** Add a widget with FloatLayoutConstraints. This does not redo the layout. */
    public void add(Widget widget, float constraints) {...}

    /** Add a widget with StringLayoutConstraints. This does not redo the layout. */
    public void add(Widget widget, String constraints) {...}
...
```

The first method adds the widget with the specified constraints. The second and third add the widget with float and string constraints. Layouts may use these constraints to create their own specific constraints (e.g. RowLayout.Constraints) in a reasonable way (typically by using the string or float as a constructor argument). In many cases it is possible to avoid creating RowLayout.Constraints instances.

The examples below are reproduced in the main demo.

This example creates a typical "buttons on top of scrolling body region" layout:

```
LayoutPanel p = new LayoutPanel(); // has RowLayout in column by default
p.add(buttons, 22); // new RowLayout.Contraints(22): size=22 weight=0 maxSize=0
                    // overflow=hidden
p.add(body); // new RowLayout.Constraints(): size=0 weight=1.0 maxSize=0 overflow=auto
p.layout();
```

This is example has a sidebar on the left using 20% of available space with auto scrollbars, a 20 pixel margin on the right without scrollbars and the rest of the space for the body with auto scrollbars:

```
LayoutPanel p = new LayoutPanel(false); // use RowLayout in row
p.add(sidebar, 0.2f); // new RowLayout.Contraints(0.2f): size=0.2 weight=0.0 maxSize=0
                      // overflow=auto
p.add(body); // new RowLayout.Constraints(): size=0 weight=1.0 maxSize=0 overflow=auto
p.add(margin, 20);  // new RowLayout.Contraints(20): size=20 weight=0.0 maxSize=0
                    // overflow=hidden
p.layout();
```

Here is "border layout":

```
LayoutPanel inner = new LayoutPanel(false); // row
inner.add(west, 0.2f);
inner.add(center);
inner.add(east, 0.2f);

LayoutPanel outer = new LayoutPanel(); // column
outer.add(north, 0.2f);
outer.add(inner, LayoutConstraints.HIDDEN); // avoid scrollbars in scrollbars
outer.add(south, 0.2f);
outer.layout();
```

These examples depend on the constructors for RowLayout.Constraints which are designed to make the common cases simple:

```
package org.gwtportlets.portlet.client.layout;
...
public class RowLayout implements Layout {
...
    public static class Constraints implements LayoutConstraints {
    ...
        /** Size=0.0, weight=1.0, MaxSize=0 */
        public Constraints(String overflow) {...}

        /** MaxSize=0, if size < 1.0 overflow=AUTO else overflow=HIDDEN */
        public Constraints(float size, float weight) {...}

        /** Weight=0.0, maxSize=0, if size < 1.0 overflow=AUTO else overflow=HIDDEN */
        public Constraints(float size) {...}

        /** Size 0.0, weight 1.0, maxSize=0, overflow=AUTO */
        public Constraints() {...}
...
```

## 5.3. Layout Tips ##

### 5.3.1. Scrollbars ###

Generally it is best to avoid placing regions with overflow=auto (i.e. scrollbars if needed) inside each other. In some older browsers scrollbars may appear when not needed when the outer region is made smaller.

### 5.3.2. Tables ###

Tables with padding around the outer TD elements end up taking up more space that what is assigned to them by the framework. This causes unnecessary scrollbars. Likewise a table with padding and width of 100% placed inside a DIV will get scrollbars. One solution is to avoid padding on TDs on the outer edges of the table. The FormBuilder class uses CSS styles on the first and last rows and columns in the table to achieve this effect.

This problem and the "narrow tables exploding" problem can also be solved by wrapping the table in a DIV (SimplePanel) as shown in this example:

```
LayoutPanel p = new LayoutPanel(); // widgets in a column
p.add(buttons, 22);
SimplePanel wrapper = new SimplePanel();
wrapper.add(table);
p.add(wrapper);
p.layout();
```

The width of wrapper is set to the width of the LayoutPanel and the table assumes its natural width inside it. Without the wrapper the table would be as wide as the LayoutPanel. This approach also avoids the problems with table TD padding and scrollbars.

### 5.3.3. Margins ###

The framework does not consider margins when laying out widgets. Use the layout spacing property and widget padding and borders to create space between widgets.

### 5.3.4. PositionAware interface ###

Widgets implementing PositionAware are notified by a call to boundsUpdated() when their position and/or size is changed. The Chart widget from the FreeMemoryPortlet uses this mechanism to size its Google Chart to fit the available space:

```
private class Chart extends Image implements PositionAware ... {
    public void boundsUpdated() {
        Rectangle r = LDOM.getContentBounds(this); // area inside our borders and padding
        // r.width, r.height == area available for chart
        ...
```

## 5.4. The LDOM Class ##

LayoutPanel and Layouts use static methods in LDOM to query and set position and size related properties for widgets and elements. The methods in this class take borders and padding into account when needed. There are different LDOMImpl implementations for different browsers.

The most important methods are shown in extract below:

```
package org.gwtportlets.portlet.client.layout;
...
public class LDOM {
...
    /**
     * Position the widget. If it implements {@link PositionAware} then it is notified
     * of this change. The width and height are adjusted to account for the
     * borders and padding of the widget if needed. Note that its margin is not
     * considered.
     */
    public static void setBounds(Widget w, int left, int top, int width, int height)
          {...}
    public static void setBounds(Widget w, Rectangle r) {...}

    /**
     * Get a bounding rectangle for w in browser client area coordinates.
     */
    public static Rectangle getBounds(Widget w) {...}

    /**
     * Get a bounding rectangle for the content area of w in browser client
     * area coordinates. This area excludes space used by borders and padding.
     */
    public static Rectangle getContentBounds(Widget w) {...}
...
```

>> [Introduction](Introduction.md) / [Portlets](Portlets.md) / [Concepts](Concepts.md) / [Application Structure](AppStructure.md) / [Layouts](Layouts.md) / [Page Editor](PageEditor.md) / [Widgets](Widgets.md) / [Themes](Themes.md) / [Spring](Spring.md) <<