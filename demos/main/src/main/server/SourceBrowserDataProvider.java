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

package main.server;

import main.client.ui.SourceBrowserPortlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gwtportlets.portlet.server.PageRequest;
import org.gwtportlets.portlet.server.WidgetDataProvider;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Browses source as produced by Java2HTML (http://www.java2html.com).
 */
public class SourceBrowserDataProvider
        implements WidgetDataProvider<SourceBrowserPortlet.Factory> {

    private Log log = LogFactory.getLog(getClass());

    private SourceBrowserPortlet.ClassSuggestion [] classes;
    private Map<File, String> htmlCache =
            Collections.synchronizedMap(new HashMap<File, String>());
    
    private static final String LINE20 = "<A NAME=\"20\"></A>";

    public Class getWidgetFactoryClass() {
        return SourceBrowserPortlet.Factory.class;
    }

    public void refresh(SourceBrowserPortlet.Factory f, PageRequest req) {
        File srcDir = new File(req.getServletConfig().getServletContext()
                .getRealPath("src"));
        File file;
        if (f.filename == null || f.filename.length() == 0) {
            file = new File(srcDir, "AllClasses.html");
        } else {
            file = new File(srcDir, f.filename);
            if (!file.isFile()) { // assume filename is a class name
                file = new File(srcDir, f.filename.replace('.', '/') +
                        ".java.html");
            }
        }
        String srcPath = srcDir.getAbsolutePath();
        String filePath = file.getAbsolutePath();
        if (!filePath.startsWith(srcPath) || !file.isFile()) {
            if (!srcDir.isDirectory()) {
                f.html = "Source not found. Run 'ant java2html' in the root " +
                        "of the GWT Portlets distribution to generate HTML " +
                        "files for each framework source file.";                
            } else {
                f.html = "File not found";
            }
            log.warn("File not found: " + file);
        } else {
            f.html = htmlCache.get(file);
            if (f.html == null) {
                try {
                    f.html = removeCopyrightHeader(readFile(file))
                        // convert relative links to absolute
                        .replaceAll(" HREF=\"(\\.\\./)+", " href=\"src/")
                        // get ride of TARGET="..."
                        .replaceAll(" TARGET=\".*\"", "")
                        // put in src for any remaining HREF links
                        .replace(" HREF=\"", " href=\"src/");
                    htmlCache.put(file, f.html);
                    f.filename = cleanFilename(srcPath, filePath);
                } catch (IOException e) {
                    log.error("Error reading " + file + ": " + e, e);
                    f.html = "Error reading file, see server log for details";
                }
            } else {
                f.filename = cleanFilename(srcPath, filePath);
            }
        }
        if (!f.doNotSendClasses) {
            f.classes = getClasses(srcDir);
        }
    }

    private String removeCopyrightHeader(String html) {
        int i = html.indexOf("<A NAME=\"1\"></A><FONT ID=\"MultiLineComment\">");
        if (i >= 0) {
            int j = html.indexOf(LINE20, i);
            if (j >= 0) {
                html = html.substring(0, i) + html.substring(j + LINE20.length() + 1);
            }
        }
        return html;
    }

    private String cleanFilename(String srcPath, String filePath) {
        String s = filePath.substring(srcPath.length() + 1)
                .replace('\\', '/');
        if (s.endsWith(".java.html")) {
            s = s.substring(0, s.length() - 10);
            s = s.replace('/', '.');
        }
        return s;
    }

    private String readFile(File file) throws IOException {
        StringBuffer buf = new StringBuffer();
        char[] a = new char[4096];
        FileReader r = null;
        try {
            r = new FileReader(file);
            for (;;) {
                int sz = r.read(a);
                if (sz < 0) {
                    break;
                }
                buf.append(a, 0, sz);
            }
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return buf.toString();
    }

    private synchronized SourceBrowserPortlet.ClassSuggestion[] getClasses(
            File srcDir) {
        if (classes == null) {
            List<String> list = new ArrayList<String>();
            findClasses(srcDir.getAbsolutePath(), srcDir, list);
            int n = list.size();
            SourceBrowserPortlet.ClassSuggestion[] a =
                    new SourceBrowserPortlet.ClassSuggestion[n];
            for (int i = 0; i < n; i++) {
                a[i] = new SourceBrowserPortlet.ClassSuggestion(list.get(i));
            }
            Arrays.sort(a);
            classes = a;
        }
        return classes;
    }

    private void findClasses(String srcPath, File dir, List<String> ans) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    findClasses(srcPath, f, ans);
                } else if (f.getName().endsWith(".java.html")) {
                    String s = f.getAbsolutePath();
                    s = s.substring(srcPath.length() + 1, s.length() - 10)
                            .replace('/', '.').replace('\\', '.');
                    ans.add(s);
                }
            }
        }
    }

}
