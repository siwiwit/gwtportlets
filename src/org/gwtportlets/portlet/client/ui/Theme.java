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

package org.gwtportlets.portlet.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import org.gwtportlets.portlet.client.util.DictionaryHelper;
import org.gwtportlets.portlet.client.layout.Layout;
import org.gwtportlets.portlet.client.layout.RowLayout;

import java.util.Date;

/**
 * Singleton to manage themes. Different components in the framework are
 * themed using CSS and helper methods in this class to define dimensions
 * e.g. the height of a dialog header. This makes spriting easier and keeps
 * the CSS for a new theme simple.<p>
 *
 * The current theme (if any) is read from a cookie on startup.<p>
 */
public class Theme {

    private static Theme instance;

    /** Get the singleton Theme instance. */
    public static Theme get() {
        if (instance == null) {
            instance = new Theme();
        }
        return instance;
    }

    public static void set(Theme instance) {
        Theme.instance = instance;
    }

    // name of default theme - requires no extra CSS
    public static final String DEFAULT = "BlueGradient";

    private String currentTheme;
    private String[] themes;
    private String themeCookie;

    protected int titleBarHeight;
    protected int titleBarLeftWidth;
    protected int titleBarRightWidth;
    protected int titleBarSpacing;

    protected int toolButtonWidth;
    protected int toolButtonHeight;

    protected int dialogHeaderHeight;
    protected int dialogHeaderLeftWidth;
    protected int dialogHeaderRightWidth;
    protected int dialogHeaderSpacing;
    protected int dialogButtonBarHeight;
    protected int dialogSidesHeight;
    protected int dialogSidesLeftWidth;
    protected int dialogSidesRightWidth;
    protected int dialogFooterHeight;
    protected int dialogFooterLeftWidth;
    protected int dialogFooterRightWidth;

    protected int buttonHeight;
    protected float buttonCharWidth;
    protected int[] buttonWidth;

    protected int shadowPanelNorthHeight;
    protected int shadowPanelSouthHeight;
    protected int shadowPanelWestWidth;
    protected int shadowPanelEastWidth;

    protected int sectionPanelHeaderHeight;

    protected Theme() {
        DictionaryHelper d = new DictionaryHelper("gwt_portlets");
        themeCookie = d.get("themeCookie", "gwt-portlets-theme");
        
        String[] stdThemes = getStandardThemes();
        themes = d.getStringArray("themes", stdThemes);
        if (themes.length == 0) {
            themes = stdThemes;
        }

        currentTheme = loadThemeName();
        if (currentTheme == null) {
            currentTheme = themes[0];
        }

        if (!DEFAULT.equals(currentTheme)) {
            Element e = DOM.createElement("link");
            DOM.setElementProperty(e, "rel", "stylesheet");
            DOM.setElementProperty(e, "href", GWT.getModuleBaseURL() +
                    "gwt-portlets-" + currentTheme + ".css");
            DOM.appendChild(getHead(), e);
        }
        init(new DictionaryHelper("gwt_portlets_" + currentTheme));
    }

    protected String[] getStandardThemes() {
        return new String[]{DEFAULT, "LightBlue", "Gray"};
    }

    private native Element getHead() /*-{
        return $doc.getElementsByTagName('head')[0];
    }-*/;

    /**
     * Get the name of the currently selected theme.
     */
    public String getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Get the names of the available themes.
     */
    public String[] getThemes() {
        return themes;
    }

    /**
     * Change to a different theme or NOP if the theme is already active.
     * Expects to find a
     * gwt-portlets-&lt;name&gt;.css stylesheet (unless the default 'BlueGradient'
     * theme is selected) and an optional gwt_portlet_&lt;name&gt; Javascript
     * object with overrides for dimensions of dialog headers and whatnot.<p>
     *
     * <b>NB: This method reloads the application if the theme is changed.</b>
     */
    public void changeTheme(String name) {
        if (name.equals(this.currentTheme)) {
            return;
        }
        saveThemeName(name);
        reload();
    }

    private native void reload() /*-{
        $wnd.location.reload();
    }-*/;

    /**
     * Store the name of the selected theme in a cookie or something so it
     * is available via {@link #loadThemeName()} when the application reloads.
     */
    protected void saveThemeName(String name) {
        Date exp = new Date(System.currentTimeMillis()
                + 1000L * 60 * 60 * 24 * 365);
        // expires in 1 years time
        Cookies.setCookie(themeCookie, name, exp);
    }

    /**
     * Load the name of the selected theme from a cookie or something or
     * return null if none.
     */
    protected String loadThemeName() {
        return Cookies.getCookie(themeCookie);
    }

    /**
     * Update field values from d.
     */
    protected void init(DictionaryHelper d) {
        titleBarHeight = d.getInt("titleBarHeight", 20);
        titleBarLeftWidth = d.getInt("titleBarLeftWidth", 4);
        titleBarRightWidth = d.getInt("titleBarRightWidth", 4);
        titleBarSpacing = d.getInt("titleBarSpacing", 1);

        toolButtonWidth = d.getInt("toolButtonWidth", 16);
        toolButtonHeight = d.getInt("toolButtonHeight", 16);

        dialogHeaderHeight = d.getInt("dialogHeaderHeight", 24);
        dialogHeaderLeftWidth = d.getInt("dialogHeaderLeftWidth", 8);
        dialogHeaderRightWidth = d.getInt("dialogHeaderRightWidth", 8);
        dialogHeaderSpacing = d.getInt("dialogHeaderSpacing", 1);
        dialogButtonBarHeight = d.getInt("dialogButtonBarHeight", 28);
        dialogSidesHeight = d.getInt("dialogSidesHeight", 4);
        dialogSidesLeftWidth = d.getInt("dialogSidesLeftWidth", 9);
        dialogSidesRightWidth = d.getInt("dialogSidesRightWidth", 9);
        dialogFooterHeight = d.getInt("dialogFooterHeight", 9);
        dialogFooterLeftWidth = d.getInt("dialogFooterLeftWidth", 8);
        dialogFooterRightWidth = d.getInt("dialogFooterRightWidth", 8);

        buttonHeight = d.getInt("buttonHeight", 22);
        buttonCharWidth = d.getFloat("buttonCharWidth", 7.5f);
        buttonWidth = d.getIntArray("buttonWidth",
                new int[]{21, 30, 45, 60, 75, 90, 105, 120, 150});

        shadowPanelNorthHeight = d.getInt("shadowPanelNorthHeight", 4);
        shadowPanelSouthHeight = d.getInt("shadowPanelSouthHeight", 4);
        shadowPanelWestWidth = d.getInt("shadowPanelWestWidth", 4);
        shadowPanelEastWidth = d.getInt("shadowPanelEastWidth", 4);

        sectionPanelHeaderHeight = d.getInt("sectionPanelHeaderHeight", 16);
    }

    /**
     * Configure the dimensions of the title bar.
     */
    public void updateTitleBar(EdgeRow titleBar) {
        titleBar.setDimensions(0, titleBarHeight,
                titleBarLeftWidth, titleBarRightWidth);
        Layout l = titleBar.getLayout();
        if (l instanceof RowLayout) {
            ((RowLayout)l).setSpacing(titleBarSpacing);
        }
    }

    /**
     * Configure the dimensions of the tool button.
     */
    public void updateToolButton(ToolButton b) {
        b.setWidth(toolButtonWidth);
        b.setHeight(toolButtonHeight);
    }

    /**
     * Configure the dimensions of the dialog.
     */
    public void updateDialog(Dialog dlg) {
        EdgeRow header = dlg.getHeader();
        header.setDimensions(0, dialogHeaderHeight,
                dialogHeaderLeftWidth, dialogHeaderRightWidth);
        Layout l = header.getLayout();
        if (l instanceof RowLayout) {
            ((RowLayout)l).setSpacing(dialogHeaderSpacing);
        }
        dlg.setButtonBarHeight(dialogButtonBarHeight);
        dlg.getSidesContent().setDimensions(0, dialogSidesHeight,
                dialogSidesLeftWidth, dialogSidesRightWidth);
        dlg.getSidesButtonBar().setDimensions(0, dialogSidesHeight,
                dialogSidesLeftWidth, dialogSidesRightWidth);
        dlg.getFooter().setDimensions(
                dialogHeaderHeight * 2,
                dialogFooterHeight, dialogFooterLeftWidth,
                dialogFooterRightWidth);
    }

    /**
     * Set the height of b.
     */
    public void updateHeight(CssButton b) {
        b.setHeight(buttonHeight);
    }

    /**
     * Set the width of b.
     */
    public void updateButtonWidth(CssButton b) {
        int w = b.getOffsetWidth();
        if (w == 0) { // heuristic in case where width is not available
            w = (int)(b.getText().length() * buttonCharWidth) + 1;
        }
        int x = 0, bw;
        for (int i = 0; ; i++) {
            bw = buttonWidth[i];
            if (w <= bw || i == buttonWidth.length - 1) {
                break;
            } else {
                x += bw + 1;
            }
        }
        b.setWidth(bw);
        b.setOffset(x);
    }

    /**
     * Set the dimensions of p.
     */
    public void updateShadowPanel(ShadowPanel p) {
        p.setDimensions(shadowPanelNorthHeight, shadowPanelSouthHeight,
                shadowPanelWestWidth, shadowPanelEastWidth);
    }

    /**
     * Set the dimensions of p.
     */
    public void updateSectionPanel(SectionPanel p) {
        p.setDimensions(sectionPanelHeaderHeight);
    }
}
