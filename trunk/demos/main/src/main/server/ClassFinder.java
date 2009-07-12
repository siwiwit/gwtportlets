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

package main.server;

import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Finds classes by searching for files in WEB-INF/classes. In applications
 * using Spring (or similar frameworks) this sort of thing is not needed.
 */
public class ClassFinder {

    private Logger log = Logger.getLogger(getClass());

    private final String classesPath;
    private final File pkgDir;

    private static final String DOT_CLASS = ".class";

    public ClassFinder(ServletContext sc, String pkg) {
        File classesDir = new File(sc.getRealPath("WEB-INF/classes"));
        classesPath = classesDir.getAbsolutePath();
        pkgDir = new File(classesDir, pkg.replace('.', '/'));
    }

    /**
     * Find classes with names ending with suffix that are of type assignableTo.
     */
    public List<Class> findClasses(String suffix, Class assignableTo) {
        List<Class> ans = new ArrayList<Class>();
        findClasses(pkgDir, suffix + DOT_CLASS, assignableTo, ans);
        return ans;
    }

    private void findClasses(File dir, String suffix, Class assignableTo, List<Class> ans) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                findClasses(f, suffix, assignableTo, ans);
            } else if (f.getName().endsWith(suffix)) {
                String s = f.getAbsolutePath();
                s = s.substring(classesPath.length() + 1, s.length() - DOT_CLASS.length())
                    .replace(File.separatorChar, '.');
                try {
                    Class<?> cls = Class.forName(s);
                    if (assignableTo.isAssignableFrom(cls)) {
                        ans.add(cls);
                    }
                } catch (ClassNotFoundException e) {
                    log.error(e, e);
                }
            }
        }
    }
    
}
