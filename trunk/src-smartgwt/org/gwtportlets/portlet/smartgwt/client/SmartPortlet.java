/*
 * GWT Portlets Framework (http://code.google.com/p/gwtportlets/)
 * Copyright 2010 Business Systems Group (Africa)
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

package org.gwtportlets.portlet.smartgwt.client;

import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.widgets.Canvas;
import org.gwtportlets.portlet.client.ui.Portlet;

import java.util.ArrayList;
import java.util.List;

/**
 * A portlet which is able to use SmartGwt widgets and data integration.
 *
 * @author Carl Crous
 */
public abstract class SmartPortlet extends Portlet {
    /** The list of pending requests. */
    protected List<DSRequest> requestList = new ArrayList<DSRequest>();
    /** If the child widget should be automatically re-sized. */
    protected boolean autoSizeChild = true;
    
    protected SmartPortlet() {
    }

    protected SmartPortlet(boolean autoSizeChild) {
        this.autoSizeChild = autoSizeChild;
    }

    @Override
    protected void initWidget(Widget widget) {
        super.initWidget(widget);
        if (widget instanceof Canvas) {
            Canvas canvas = (Canvas)widget;
            canvas.setZIndex(0);
        }
    }

    @Override
    public void boundsUpdated() {
        super.boundsUpdated();
        Widget widget = getWidget();
        if (widget != null && autoSizeChild) {
            widget.setSize(Integer.toString(getOffsetWidth()), Integer.toString(getOffsetHeight()));
        }
    }

    /**
     * Returns the request associated with the given Smart Portlet Factory.
     * @param f The Smart Portlet Factory which owns the request.
     * @return The request owned by the given factory or null if none exists.
     */
    public DSRequest getRequest(SmartPortletFactory f) {
        return getRequest(f.getRequestId());
    }

    /**
     * Returns the request with a given request id.
     * @param requestId The request id to find.
     * @return The request with the given id or null if none exists.
     */
    public DSRequest getRequest(String requestId) {
        for (DSRequest request : requestList) {
            if (request.getRequestId().equals(requestId)) {
                return request;
            }
        }
        return null;
    }

    /**
     * Adds a request to be associated with this portlet.
     * @param request The request to add.
     */
    public void addRequest(DSRequest request) {
        requestList.add(request);
    }

    /**
     * Removes and returns the request associated with the given Smart Portlet Factory.
     * @param f The Smart Portlet Factory which owns the request.
     * @return The request owned by the given factory or null if none exists.
     */
    public DSRequest removeRequest(SmartPortletFactory f) {
        return removeRequest(f.getRequestId());
    }

    /**
     * Removes and returns the request with a given request id.
     * @param requestId The request id to find.
     * @return The request with the given id or null if none exists.
     */
    public DSRequest removeRequest(String requestId) {
        for (int i = 0, requestListSize = requestList.size(); i < requestListSize; i++) {
            DSRequest request = requestList.get(i);
            if (request.getRequestId().equals(requestId)) {
                requestList.remove(i);
                return request;
            }
        }
        return null;
    }

    /**
     * Creates a response for the given request.
     * @param request The request to create a response for.
     * @return The created response.
     */
    public static DSResponse createResponse(DSRequest request) {
        DSResponse response = new DSResponse();
        response.setAttribute ("clientContext", request.getAttributeAsObject ("clientContext"));
        response.setStatus(0); // Success
        return response;
    }

    /**
     * Creates a response for the given request and initialises it with the given portlet factory.
     * @param request The request to create a response for.
     * @param f The portlet factory which contains data in response to the request.
     * @return The created response.
     */
    public static DSResponse createResponse(DSRequest request, SmartPortletFactory f) {
        DSResponse response = createResponse(request);
        response.setTotalRows(f.getTotalRows());
        return response;
    }
}
