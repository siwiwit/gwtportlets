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

package main.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.edit.PageEditor;

/**
 * Our page editor.
 */
public class DemoPageEditor extends PageEditor {

    public DemoPageEditor() {
    }

    protected void savePage(WidgetFactory wf, AsyncCallback callback) {
        DemoService.App.get().savePage(getPageName(), wf,
                new AsyncCallback() {
            public void onFailure(Throwable caught) {
                Window.alert("Oops " + caught);
            }
            public void onSuccess(Object result) {
                Window.alert("Saved");
            }
        });
    }

}
