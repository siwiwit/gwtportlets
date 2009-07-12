/*
 * GWT Portlets Framework (http://www.gwtportlets.org/)
 * Copyright 2009 Business Systems Group (Africa)
 *
 * This file is part of GWT Portlets.
 *
 * GWT Portlets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GWT Portlets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GWT Portlets.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gwtportlets.portlet.client.layout;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * Arranges widgets in a single row or column. Constraints define the
 * width or height of each widget.
 */
public class RowLayout implements Layout {

    private boolean column;
    private int spacing;

    /**
     * If column is true widgets are arranged in a column (vertically),
     * otherwise they are arranged in a row (horizontally). The widgets
     * have spacing pixels of space between them.
     */
    public RowLayout(boolean column, int spacing) {
        this.column = column;
        this.spacing = spacing;
    }

    /**
     * Arrange widgets in a vertical column with 4 pixels between them.
     */
    public RowLayout() {
        this(true);
    }

    public RowLayout(boolean column) {
        this(column, 4);
    }

    public boolean isColumn() {
        return column;
    }

    public void setColumn(boolean column) {
        this.column = column;
    }

    public int getSpacing() {
        return spacing;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public String getDescription() {
        return column ? "Column" : "Row";
    }

    public void layoutWidgets(Container container,
            int contentLeft, int contentTop, int width, int height) {
        int wc = container.getWidgetCount();
        if (wc == 0) {
            return;
        }
        Constraints[] cons = ensureConstraints(container);

        // 1st pass: set the initial size of each widget and calc total size
        int avail = (column ? height : width) - (wc - 1) * spacing;
        int allocated = 0;
        float totWeights = 0.0f;
        for (int i = 0; i < wc; i++) {
            Constraints c = cons[i];
            c.extraSize = 0;
            if (c.size < 1.0f) {
                c.actualSize = (int)(c.size * avail);
            } else {
                c.actualSize = (int)c.size;
            }
            if (c.maxSize > 0 && c.actualSize > c.maxSize) {
                c.actualSize = c.maxSize;
            }
            allocated += c.actualSize;
            totWeights += c.weight;
        }
        avail -= allocated;
        
        if (avail > 0 && totWeights > 0.0f) {

            // 2nd pass: allocate any extra space by weight to the
            // widgets with maxSize > actualSize first so allocations that
            // are 'clipped' are available to widgets with no maxSize
            allocated = 0;
            float totWeights2 = 0.0f;
            int last = -1;
            for (int i = 0; i < wc; i++) {
                Constraints c = cons[i];
                if (c.maxSize == 0) {
                    totWeights2 += c.weight;
                    last = i;
                } else {
                    int diff = c.maxSize - c.actualSize;
                    if (c.weight > 0.0f && diff > 0) {
                        int share = (int)(c.weight * avail / totWeights);
                        if (share > diff) {
                            share = diff;
                        }
                        c.actualSize += share;
                        c.extraSize += share;
                        allocated += share;
                    }
                }
            }
            avail -= allocated;

            if (avail > 0 && totWeights2 > 0.0f) {
                // 3rd pass: allocate remaining space to widgets with no max size
                allocated = 0;
                for (int i = 0; i < wc; i++) {
                    Constraints c = cons[i];
                    if (c.maxSize == 0 && c.weight > 0.0f) {
                        int share = (int)(c.weight * avail / totWeights2);
                        c.actualSize += share;
                        c.extraSize += share;
                        allocated += share;
                    }
                }

                if (avail > allocated && last >= 0) {
                    // give any extra pixels to the last widget with a weight
                    // and no maxSize
                    int share = avail - allocated;
                    if (share > 0) {
                        Constraints c = cons[last];
                        c.actualSize += share;
                        c.extraSize += share;
                    }
                }
            }
        }

        // position each widget according to actualSize
        int pos = column ? contentTop : contentLeft;
        for (int i = 0; i < wc; i++) {
            Constraints c = cons[i];
            int size = c.actualSize;
            Widget w = container.getWidget(i);
            if (w instanceof HasMaximumSize) { // center in area allocated to it
                HasMaximumSize f = (HasMaximumSize)w;
                int fw = Math.min(f.getMaxWidth(), column ? width : size);
                int fh = Math.min(f.getMaxHeight(), column ? size : height);
                if (column) {
                    LDOM.setBounds(w,
                            contentLeft + (width - fw) / 2,
                            pos + (size - fh) / 2,
                            fw, fh);
                } else {
                    LDOM.setBounds(w,
                            pos + (size - fw) / 2,
                            contentTop + (height - fh) / 2, 
                            fw, fh);
                }
            } else if (column) {
                LDOM.setBounds(w, contentLeft, pos, width, size);
            } else {
                LDOM.setBounds(w, pos, contentTop, size, height);
            }
            String o = c.getOverflow();
            DOM.setStyleAttribute(w.getElement(), "overflow",
                    Constraints.CSS.equals(o) ? "" : o);
            pos += size + spacing;
        }
    }

    private Constraints[] ensureConstraints(Container container) {
        int wc = container.getWidgetCount();
        Constraints[] cons = new Constraints[wc];
        for (int i = 0; i < wc; i++) {
            Widget w = container.getWidget(i);
            Object o = container.getLayoutConstraints(w);
            if (o instanceof Constraints) {
                cons[i] = (Constraints)o;
            } else {
                Constraints c;
                if (o instanceof FloatLayoutConstraints) {
                    c = new Constraints(((FloatLayoutConstraints)o).value);
                } else if (o instanceof StringLayoutConstraints) {
                    c = new Constraints(((StringLayoutConstraints)o).value);
                } else {
                    c = new Constraints();
                }
                container.setLayoutConstraints(w, cons[i] = c);
            }
        }
        return cons;
    }

    public LayoutConstraints convertConstraints(LayoutConstraints layoutConstraints) {
        if (layoutConstraints instanceof Constraints) {
            return new Constraints((Constraints)layoutConstraints);
        }
        return null;
    }

    public LayoutFactory createLayoutFactory() {
        return new Factory(this);
    }

    /**
     * Our layout constraints:<br>
     * <li>size: Size in pixels or fraction of the available space (0.2 = 20%)
     * <li>weight: Weighting relative to other widgets to use to allocate extra space
     * <li>maxSize: Max size in pixels
     */
    public static class Constraints implements LayoutConstraints {

        public static final String CSS = "css";

        private float size;
        private float weight;
        private int maxSize;
        private String overflow = HIDDEN;

        private transient int actualSize;
        private transient int extraSize;

        public Constraints(float size, float weight, int maxSize, String overflow) {
            setSize(size);
            setWeight(weight);
            setMaxSize(maxSize);
            setOverflow(overflow);
        }

        public Constraints(Constraints src) {
            this(src.getSize(), src.getWeight(), src.getMaxSize(), src.getOverflow());
        }

        /** Overflow=HIDDEN */
        public Constraints(float size, float weight, int maxSize) {
            this(size, weight, maxSize, HIDDEN);
        }

        /** MaxSize=0 */
        public Constraints(float size, float weight, String overflow) {
            this(size, weight, 0, overflow);
        }

        /** Size=0.0, weight=1.0, MaxSize=0 */
        public Constraints(String overflow) {
            this(0.0f, 1.0f, 0, overflow);
        }

        /** MaxSize=0, if size < 1.0 overflow=AUTO else overflow=HIDDEN */
        public Constraints(float size, float weight) {
            this(size, weight, 0, size < 1.0f ? AUTO : HIDDEN);
        }

        /** Weight=0.0, maxSize=0, if size < 1.0 overflow=AUTO else overflow=HIDDEN */
        public Constraints(float size) {
            this(size, 0.0f, 0, size < 1.0f ? AUTO : HIDDEN);
        }

        /** Size 0.0, weight 1.0, maxSize=0, overflow=AUTO */
        public Constraints() {
            this(0.0f, 1.0f, 0, AUTO);
        }

        public LayoutConstraints copy() {
            return new Constraints(this);
        }

        public float getSize() {
            return size;
        }

        public void setSize(float size) {
            if (size < 0.0f) {
                throw new IllegalArgumentException("size must be >= 0: " + size);
            }
            this.size = size;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            if (weight < 0.0f) {
                throw new IllegalArgumentException("weight must be >= 0: " + weight);
            }
            this.weight = weight;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            if (maxSize < 0) {
                throw new IllegalArgumentException("maxSize must be >= 0: " + maxSize);
            }
            this.maxSize = maxSize;
        }

        public int getActualSize() {
            return actualSize;
        }

        public int getExtraSize() {
            return extraSize;
        }

        public String getOverflow() {
            return overflow;
        }

        public void setOverflow(String overflow) {
            this.overflow = overflow;
        }

        public String toString() {
            return "size=" + size + " weight=" + weight +
                    " maxSize=" + maxSize + " overflow=" + overflow +
                    " actualSize=" + actualSize + " extraSize=" + extraSize;
        }
    }

    /**
     * Our factory class.
     */
    public static class Factory implements LayoutFactory {

        private boolean column;
        private int spacing;

        public Factory() {
        }

        public Factory(RowLayout layout) {
            this.column = layout.isColumn();
            this.spacing = layout.getSpacing();
        }

        public Layout createLayout() {
            return new RowLayout(column, spacing);
        }
    }

}
