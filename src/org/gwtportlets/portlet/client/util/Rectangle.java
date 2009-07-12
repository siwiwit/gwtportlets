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

package org.gwtportlets.portlet.client.util;

/**
 * A x, y coordinate, width and height.
 */
public class Rectangle {

    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle() {
    }

    public Rectangle(Rectangle r) {
        x = r.x;
        y = r.y;
        width = r.width;
        height = r.height;
    }

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String toString() {
        return width + " x " + height + " @ " + x + ", " + y;
    }

    /**
     * Are left and top contained within this rectangle?
     */
    public boolean contains(int left, int top) {
        return left >= x && top >= y && left < x + width && top < y + height;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Rectangle) {
            Rectangle r = (Rectangle)obj;
            return x == r.x && y == r.y
                    && width == r.width && height == r.height;
        }
        return false;
    }

    public int hashCode() {
        return x + y * 17 + width * 23 + height * 31;
    }
}
