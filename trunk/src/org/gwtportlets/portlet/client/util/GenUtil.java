/*
 * GWT Portlets Framework (http://code.google.com/p/gwtportlets/)
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

import java.util.Comparator;

/**
 * General purpose utility methods.
 */
public class GenUtil {

    /**
     * Returns {@link com.google.gwt.core.client.GWT#getModuleBaseURL} but
     * using https protocol if not already https and useHttps is true.
     */
    public static String getModuleBaseURL(boolean useHttps) {
        String url = GWT.getModuleBaseURL();
        if (useHttps && url.startsWith("http:")) {
            StringBuffer s = new StringBuffer();
            s.append("https:");
            s.append(url.substring(5));
            int i = s.indexOf(":8080");
            if (i >= 0) {
                s.replace(i, i + 5, ":8443");
            }
            i = s.indexOf(":80");
            if (i >= 0) {
                s.delete(i, 3);
            }
            return s.toString();
        }
        return url;
    }

    /**
     * Get the target history token if event is for an internal link or
     * null otherwise.
     */
    public static String getTargetHistoryToken(Event event) {
        Element link;
        try {
            link = DOM.eventGetTarget(event);
        } catch (Exception e) {
            return null;
        }
        if (link != null && isLink(link)) {
            String base = getBaseUrl() + "#";
            String href = null;
            if (DOM.getElementProperty(link, "tagName").equalsIgnoreCase("a")) {
                href = URL.decodeComponent(DOM.getElementProperty(link, "href"));
            } else if (DOM.getElementProperty(link, "tagName").equalsIgnoreCase("img")) {
                href = URL.decodeComponent(DOM.getElementProperty(DOM.getParent(link), "href"));
            }
            if (href != null && href.startsWith(base)) {
                return href.substring(base.length());
            }
        }
        return null;
    }

    /**
     * Is element a hyperlink?
     */
    public static boolean isLink(Element element) {
        return DOM.getElementProperty(element, "tagName").equalsIgnoreCase("a") ||
                DOM.getElementProperty(element, "tagName").equalsIgnoreCase("img");
    }

    /**
     * Get the our base URL e.g. https://www.acme.com/myapp with any history
     * token type stuff stripped off.
     */
    public static String getBaseUrl() {
        String s = getDocUrl();
        int i = s.indexOf('#');
        return i < 0 ? s : s.substring(0, i);
    }

    private static native String getDocUrl() /*-{
        return $doc.URL;
    }-*/;

    /**
     * Merge sort the array. This is a stable sort i.e. equal elements will
     * not be re-ordered. Cut and pasted from java.util.Arrays in the JDK.
     * The GWT Collections.sort uses a non-stable sort which is a pain when
     * sorting multiple columns on grids etc.
     */
    public static void mergeSort(Object[] a, Comparator c) {
        int n = a.length;
        Object[] aux = new Object[n];
        for (int i = n - 1; i >= 0; i--) {
            aux[i] = a[i];
        }
        mergeSort(aux, a, 0, n, 0, c);
    }

    private static final int INSERTIONSORT_THRESHOLD = 7;

    /**
     * Src is the source array that starts at index 0
     * Dest is the (possibly larger) array destination with a possible offset
     * low is the index in dest to start sorting
     * high is the end index in dest to end sorting
     * off is the offset into src corresponding to low in dest
     */
    private static void mergeSort(Object[] src, Object[] dest,
            int low, int high, int off, Comparator c) {
        int length = high - low;

        // Insertion sort on smallest arrays
        if (length < INSERTIONSORT_THRESHOLD) {
            for (int i = low; i < high; i++)
                for (int j = i; j > low && c
                        .compare(dest[j - 1], dest[j]) > 0; j--)
                    swap(dest, j, j - 1);
            return;
        }

        // Recursively sort halves of dest into src
        int destLow = low;
        int destHigh = high;
        low += off;
        high += off;
        int mid = (low + high) >> 1;
        mergeSort(dest, src, low, mid, -off, c);
        mergeSort(dest, src, mid, high, -off, c);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if (c.compare(src[mid - 1], src[mid]) <= 0) {
            for (int i = 0; i < length; i++) {
                dest[destLow + i] = src[low + i];
            }
            return;
        }

        // Merge sorted halves (now in src) into dest
        for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
            if (q >= high || p < mid && c.compare(src[p], src[q]) <= 0) {
                dest[i] = src[p++];
            } else {
                dest[i] = src[q++];
            }
        }
    }

    private static void swap(Object[] x, int a, int b) {
        Object t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

}
