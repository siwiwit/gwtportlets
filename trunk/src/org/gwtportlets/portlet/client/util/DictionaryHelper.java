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

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Set;

/**
 * Helper class to read data from Dictionary's.
 */
public class DictionaryHelper {

    private final String name;
    private Dictionary d;
    private Set<String> keys;

    public DictionaryHelper(String name) {
        this.name = name;
        try {
            d = Dictionary.getDictionary(name);
            keys = d.keySet();
        } catch (MissingResourceException e) {
            // ignore
        }
    }

    /**
     * Was the dictionary not found?
     */
    public boolean isMissing() {
        return d == null;
    }

    public String getName() {
        return name;
    }

    public String get(String key, String def) {
        if (d != null && keys.contains(key)) {
            return d.get(key);
        }
        return def;
    }

    public int getInt(String key, int def) {
        if (d != null && keys.contains(key)) {
            String s = d.get(key);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                bad(key, s, e);
            }
        }
        return def;
    }

    private void bad(String key, String s, Exception e) {
        GWT.log("Invalid value " + name + "." + key + ": '" + s + "'", e);
    }

    public float getFloat(String key, float def) {
        if (d != null && keys.contains(key)) {
            String s = d.get(key);
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                bad(key, s, e);
            }
        }
        return def;
    }

    /**
     * Get a comma separated list of ints.
     */
    public int[] getIntArray(String key, int[] def) {
        if (d != null && keys.contains(key)) {
            String s = d.get(key);
            try {
                List<Integer> list = new ArrayList<Integer>();
                int pos = 0;
                int n = s.length();
                for (; pos < n;) {
                    int i  = s.indexOf(',', pos);
                    if (i < 0) {
                        i = n;
                    }
                    list.add(Integer.parseInt(s.substring(pos, i).trim()));
                    pos = i + 1;
                }
                n = list.size();
                int[] a = new int[n];
                for (int i = 0; i < n; i++) {
                    a[i] = list.get(i);
                }
                return a;
            } catch (NumberFormatException e) {
                bad(key, s, e);
            }
        }
        return def;
    }

    /**
     * Get a comma separated list of strings.
     */
    public String[] getStringArray(String key, String[] def) {
        if (d != null && keys.contains(key)) {
            String s = d.get(key);
            List<String> list = new ArrayList<String>();
            int pos = 0;
            int n = s.length();
            for (; pos < n;) {
                int i  = s.indexOf(',', pos);
                if (i < 0) {
                    i = n;
                }
                list.add(s.substring(pos, i).trim());
                pos = i + 1;
            }
            return list.toArray(new String[list.size()]);
        }
        return def;
    }

}
