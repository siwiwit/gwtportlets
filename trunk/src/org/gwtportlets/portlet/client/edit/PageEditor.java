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

package org.gwtportlets.portlet.client.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.gwtportlets.portlet.client.WidgetFactory;
import org.gwtportlets.portlet.client.WidgetFactoryProvider;
import org.gwtportlets.portlet.client.WidgetRefreshHandler;
import org.gwtportlets.portlet.client.edit.row.RowLayoutEditor;
import org.gwtportlets.portlet.client.edit.PageEditorDialog;
import org.gwtportlets.portlet.client.layout.*;
import org.gwtportlets.portlet.client.ui.*;
import org.gwtportlets.portlet.client.util.Rectangle;
import org.gwtportlets.portlet.client.util.SyncToClientArea;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages editing the layouts for a tree of containers. Extend this and
 * implement savePage. 
 */
public abstract class PageEditor implements EventPreview, ContainerListener {

    private final LayoutPanel overlay = new LayoutPanel();
    private final SyncToClientArea sync = new SyncToClientArea(overlay);
    private final HTML dropIndicator = new HTML();
    private final HTML heldIndicator = new HTML();
    private final EditorManagerControlDialog status =
            new EditorManagerControlDialog(this);

    private Container root;
    private Container previousRoot;
    private String pageName;
    private int editDepth;
    private int maxEditDepth;

    private List editorList = new ArrayList();
    private List resizerList = new ArrayList();
    private List emptyList = new ArrayList();

    private UndoStack undoStack = new UndoStack();
    private WidgetEditor defaultWidgetEditor = createDefaultWidgetEditor();
    private TitlePanelEditor titlePanelEditor = createCaptionPanelEditor();
    private LayoutPanelEditor layoutPanelEditor = createLayoutPanelEditor();
    private WidgetReplacer widgetReplacer = createWidgetReplacer();
    private Button save = new CssButton("Save");

    private boolean updatePending;

    private PopupPanel menuPopup;
    private Widget menuTarget;

    private Widget heldWidget;
    private Container heldWidgetContainer;
    private LayoutConstraints heldConstraints;
    private String overflow;
    private boolean drillDownOnDrop;
    private String description;
    private boolean refreshOnDrop;

    private boolean pickupBusy;
    private int lastDropIndicatorClientX;
    private int lastDropIndicatorClientY;

    private static final String STYLE_RAISED = "raised";

    private final PopupListener editDialogClosed = new PopupListener() {
        public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
            if (sender instanceof PageEditorDialog
                    && !((PageEditorDialog)sender).isDirty()) {
                discardUndo();
            }
            hideIndicator(heldIndicator);
            lowerOverlay();
            showAllResizers();
        }
    };

    private final ClickListener discardHeldWidget = new ClickListener() {
        public void onClick(Widget sender) {
            discardHeldWidget();
        }
    };

    /** Creates a LayoutPanel with row layout. */
    protected WidgetOption createRow = new WidgetOption() {
        public Widget createWidget(Widget replacing) {
            return new LayoutPanel(new RowLayout(false));
        }
        public String getName() {
            return "Row";
        }
        public boolean isContainerOption() {
            return true;
        }
    };

    /** Creates a LayoutPanel with column layout. */
    protected WidgetOption createColumn = new WidgetOption() {
        public Widget createWidget(Widget replacing) {
            return new LayoutPanel(new RowLayout(true));
        }
        public String getName() {
            return "Column";
        }
        public boolean isContainerOption() {
            return true;
        }
    };

    /** Creates a TitlePanel. */
    protected WidgetOption createTitle = new WidgetOption() {
        public Widget createWidget(Widget replacing) {
            return new TitlePanel();
        }
        public String getName() {
            return "Title";
        }
        public boolean isContainerOption() {
            return true;
        }
        public boolean isAddPlaceholderOnContain() {
            return false;
        }
    };

    /** Creates a placeholder.  */
    protected WidgetOption createPlaceholder = new WidgetOption() {
        public String getName() {
            return "Placeholder";
        }
        public Widget createWidget(Widget replacing) {
            return new PlaceholderPortlet();
        }
    };

    /** Creates a spacer.  */
    protected WidgetOption createSpacer = new WidgetOption() {
        public String getName() {
            return "Spacer";
        }
        public Widget createWidget(Widget replacing) {
            return new SpacerPortlet();
        }
    };

    /** Popup a dialog to choose a portlet. */
    protected WidgetOption createPortlet = new WidgetOption() {
        public String getName() {
            return "Portlet...";
        }
        public void createWidget(Widget replacing, AsyncCallback cb) {
            PageEditor.this.createWidget(replacing, cb);
        }
    };

    /**
     * Add this to widgets to make mouse move events look they they come
     * from the overlay panel.
     */
    protected MouseListener overlayMouseListener = new MouseListenerAdapter() {
        public void onMouseMove(Widget sender, int x, int y) {
            Event ev = DOM.eventGetCurrentEvent();
            onOverlayMouseMove(DOM.eventGetClientX(ev),
                    DOM.eventGetClientY(ev));
        }
    };

    /**
     * Add this to widgets to make click events look they they come
     * from the overlay panel.
     */
    protected ClickListener overlayClickListener = new ClickListener() {
        public void onClick(Widget sender) {
            Event ev = DOM.eventGetCurrentEvent();
            onOverlayClicked(DOM.eventGetClientX(ev),
                    DOM.eventGetClientY(ev));
        }
    };

    public PageEditor() {
        dropIndicator.setStyleName("portlet-ed-drop-indicator");
        heldIndicator.setStyleName("portlet-ed-held-indicator");
        overlay.setStyleName("portlet-ed-overlay");
        overlay.setTitle("Click for menu");

        overlay.addClickListener(overlayClickListener);
        overlay.addMouseListener(overlayMouseListener);
        heldIndicator.addMouseListener(overlayMouseListener);

        dropIndicator.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                Event ev = DOM.eventGetCurrentEvent();
                onDropIndicatorClicked(DOM.eventGetClientX(ev),
                        DOM.eventGetClientY(ev));
            }
        });

        getControl().getLeftPanel().add(save);

        save.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                WidgetFactory wf = getRoot().createWidgetFactory();
                LayoutUtil.clearDoNotSendToServerFields(wf);
                savePage(wf, new AsyncCallback() {
                    public void onFailure(Throwable caught) {
                    }
                    public void onSuccess(Object result) {
                    }
                });
            }
        });
    }

    /**
     * Save changes to the page and invoke callback when done or on error.
     */
    protected abstract void savePage(WidgetFactory wf, AsyncCallback callback);

    private void onDropIndicatorClicked(int clientX, int clientY) {
        if (pickupBusy) {
            dropWidget(clientX, clientY);
        }
    }

    /**
     * Mouse move from our overlay panel or the held indicator.
     */
    private void onOverlayMouseMove(int clientX, int clientY) {
        if (pickupBusy) {
            moveDropIndicator(clientX, clientY);
        }
    }

    /**
     * Our overlay panel has been clicked.
     */
    private void onOverlayClicked(int clientX, int clientY) {
        if (menuPopup != null) {
            hideMenu();
        } else if (pickupBusy) {
            dropWidget(clientX, clientY);
        } else {
            toggleMenuForWidget(null);
        }
    }

    /**
     * Start editing root.
     */
    public void startEditing(String pageName, Container root) {
        stopEditing();
        this.root = root;
        if (previousRoot != root) {
            undoStack.clear();
        }
        previousRoot = null;
        RootPanel.get().add(overlay);
        sync.resizeWidget();
        Window.addWindowResizeListener(sync);
        setEditDepth(editDepth);
        this.pageName = pageName;
        save.setTitle(pageName == null ? "Save" : "Save '" + pageName + "'");
    }

    /**
     * Stop editing our root container. NOP if not editing.
     */
    public void stopEditing() {
        if (root != null) {
            hideMenu();
            hideIndicator(dropIndicator);
            disposeEditorsEtc();
            if (overlay.isAttached()) {
                Window.removeWindowResizeListener(sync);
                RootPanel.get().remove(overlay);
            }
            status.hide();
            previousRoot = root;
            root = null;
        }
    }

    /**
     * Clear the undo history. Normally the editor will keep the history
     * between start and stop editing calls so long as the root Container
     * being edited remains the same.
     */
    public void clearUndoStack() {
        undoStack.clear();
        previousRoot = null;
    }

    public Container getRoot() {
        return root;
    }

    /**
     * Get the name of the page being edited.
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * Get rid of all editors, resizers and empty indicators.
     */
    private void disposeEditorsEtc() {
        for (int i = editorList.size() - 1; i >= 0; i--) {
            LayoutEditor ed = (LayoutEditor)editorList.get(i);
            ed.getContainer().removeContainerListener(this);
            ed.dispose();
        }
        editorList.clear();
        for (int i = resizerList.size() - 1; i >= 0; i--) {
            ((WidgetResizer)resizerList.get(i)).dispose();
        }
        resizerList.clear();
        for (int i = emptyList.size() - 1; i >= 0; i--) {
            ((ContainerIndicator)emptyList.get(i)).dispose();
        }
        emptyList.clear();
    }

    /**
     * Get all the widget options we support. These are used by
     * {@link #buildAddMenu()},
     * {@link #buildChangeContainerToMenu(org.gwtportlets.portlet.client.layout.Container)}
     * and {@link #buildReplaceWithMenu(com.google.gwt.user.client.ui.Widget)}
     * to create menu items. Subclasses should override this and return
     * extra application specific options.<p>
     *
     * Subclasses can also override the methods that create the menus and
     * menu items themselves (e.g. to group options into submenus).
     */
    protected WidgetOption[] getWidgetOptionList() {
        return new WidgetOption[]{createRow, createColumn, createTitle,
                createPlaceholder, createSpacer, createPortlet};
    }

    /**
     * Change the editing depth. If there are no containers at the specified
     * depth then try one level up and so on.
     */
    public void setEditDepth(int editDepth) {
        hideMenu();
        hideIndicator(dropIndicator);
        if (editDepth < 0) {
            editDepth = 0;
        }
        ArrayList found = new ArrayList();
        for (;; --editDepth) {
            maxEditDepth = findContainersAtDepth(root, 0, editDepth, found);
            if (!found.isEmpty()) {
                break;
            }
        }
        if (this.editDepth != editDepth) {
            disposeEditorsEtc();
        }
        this.editDepth = editDepth;

        // make sure we have an editor for each container, a resizer widget
        // for each widget in each container and an empty indicator for each
        // empty container
        ArrayList newEditorList = new ArrayList();
        ArrayList newResizerList = new ArrayList();
        ArrayList newEmptyList = new ArrayList();
        for (int i = 0; i < found.size(); i++) {
            Container container = (Container)found.get(i);
            LayoutEditor ed = getEditorFor(container);
            if (ed == null) {
                ed = createEditorFor(container);
                if (ed == null) {
                    continue;
                }
                ed.init(this, container);
                container.addContainerListener(this);
            }
            newEditorList.add(ed);
            int wc = container.getWidgetCount();
            if (wc == 0) {
                ContainerIndicator e = getEmptyIndicatorFor(ed);
                if (e == null) {
                    e = new ContainerIndicator(this, ed);
                }
                newEmptyList.add(e);
                e.update();
            } else {
                for (int j = 0; j < wc; j++) {
                    Widget w = container.getWidget(j);
                    WidgetResizer r = getResizerFor(w);
                    if (r == null || r.getEditor() != ed) {
                        r = new WidgetResizer(this, ed, w);
                    }
                    newResizerList.add(r);
                    r.update();
                }
            }
        }

        // dispose editors for containers we no longer have
        editorList.removeAll(newEditorList);
        for (int i = editorList.size() - 1; i >= 0; i--) {
            LayoutEditor ed = (LayoutEditor)editorList.get(i);
            ed.getContainer().removeContainerListener(this);
            ed.dispose();
        }
        editorList = newEditorList;

        // dispose resizers for widgets we no longer have
        resizerList.removeAll(newResizerList);
        for (int i = resizerList.size() - 1; i >= 0; i--) {
            ((WidgetResizer)resizerList.get(i)).dispose();
        }
        resizerList = newResizerList;

        // dispose of empty indicators that are no longer needed
        emptyList.removeAll(newEmptyList);
        for (int i = emptyList.size() - 1; i >= 0; i--) {
            ((ContainerIndicator)emptyList.get(i)).dispose();
        }
        emptyList = newEmptyList;

        if (pickupBusy) {
            moveDropIndicator(lastDropIndicatorClientX, lastDropIndicatorClientY);
        }
        if (heldWidget != null) {
            moveIndicator(heldIndicator, heldWidget);            
        }
        
        status.update();
    }

    public int getEditDepth() {
        return editDepth;
    }

    public int getMaxEditDepth() {
        return maxEditDepth;
    }

    public void update() {
        setEditDepth(editDepth);
    }

    /**
     * Put all containers present at depth into list and return the maximum
     * depth of the container tree.
     */
    private int findContainersAtDepth(Container container, int depth,
            int targetDepth, List list) {
        if (depth == targetDepth) {
            list.add(container);
        }
        int ans = depth;
        for (int i = container.getWidgetCount() - 1; i >= 0; i--) {
            Widget w = container.getWidget(i);
            if (w instanceof Container) {
                int max = findContainersAtDepth((Container)w, depth + 1,
                        targetDepth, list);
                if (max > ans) {
                    ans = max;
                }
            }
        }
        return ans;
    }

    /**
     * Get the resizer for widget (null if none).
     */
    private WidgetResizer getResizerFor(Widget widget) {
        for (int i = resizerList.size() - 1; i >= 0; i--) {
            WidgetResizer r = (WidgetResizer)resizerList.get(i);
            if (r.getTarget() == widget) {
                return r;
            }
        }
        return null;
    }

    /**
     * Get the editor for container (null if none).
     */
    private LayoutEditor getEditorFor(Container container) {
        for (int i = editorList.size() - 1; i >= 0; i--) {
            LayoutEditor ed = (LayoutEditor)editorList.get(i);
            if (ed.getContainer() == container) {
                return ed;
            }
        }
        return null;
    }

    /**
     * Get the empty indicator for ed (null if none).
     */
    private ContainerIndicator getEmptyIndicatorFor(LayoutEditor ed) {
        for (int i = emptyList.size() - 1; i >= 0; i--) {
            ContainerIndicator e = (ContainerIndicator)emptyList.get(i);
            if (e.getEditor() == ed) {
                return e;
            }
        }
        return null;
    }

    /**
     * Create an editor for container or return null if none is available
     * (i.e. unsupported layout).
     */
    protected LayoutEditor createEditorFor(Container c) {
        Layout layout = c.getLayout();
        if (layout instanceof RowLayout) {
            return new RowLayoutEditor();
        }
        return null;
    }

    /**
     * Get or create an editor for w or return null if none.
     */
    protected WidgetEditor getWidgetEditorFor(Widget w) {
        if (w instanceof WidgetEditorProvider) {
            // can provide its own editor
            return ((WidgetEditorProvider)w).getWidgetEditorFor(w);
        }
        if (w instanceof TitlePanel) {
            return titlePanelEditor;
        }
        if (w instanceof LayoutPanel) {
            return layoutPanelEditor;
        }
        if (w instanceof WidgetFactoryProvider) {
            return defaultWidgetEditor;
        }
        // No point in editing if widget does not preserve its state
        return null;
    }

    /**
     * Use preview for our global actions so they work even when the mouse is
     * over our resizers and other widgets.
     */
    public boolean onEventPreview(Event ev) {
        switch (DOM.eventGetType(ev)) {

            case Event.ONMOUSEWHEEL:
                MouseWheelVelocity v = new MouseWheelVelocity(ev);
                onMouseWheel(v);
                return false;

            case Event.ONKEYPRESS:
                int key = DOM.eventGetKeyCode(ev);
                if (key == 27) {
                    onEscPressed();
                } else if (DOM.eventGetCtrlKey(ev)) {
                    if (key == 'z') {
                        undoStack.undo();
                    } else if (key == 'Z' && DOM.eventGetShiftKey(ev)) {
                        undoStack.redo();
                    }
                }
                return false;
            
        }
        return true;
    }

    /**
     * The mouse wheel was moved.
     */
    private void onMouseWheel(MouseWheelVelocity v) {
        if (status.isLevelVisible()) {
            setEditDepth(v.isNorth() ? editDepth - 1 : editDepth + 1);
        }
    }

    /**
     * The escape key was pressed.
     */
    protected void onEscPressed() {
        if (pickupBusy) {
            discardHeldWidget();
        } else {
            stopEditing();
        }
    }

    /**
     * Find the editor containing the coordinates given.
     */
    private LayoutEditor getEditorFor(int clientX, int clientY) {
        for (int i = editorList.size() - 1; i >= 0; i--) {
            LayoutEditor ed = (LayoutEditor)editorList.get(i);
            Widget container = (Widget)ed.getContainer();
            if (LDOM.contains(container.getElement(), clientX, clientY)) {
                return ed;
            }
        }
        return null;
    }

    /**
     * Start dragging the widget.
     */
    public void dragWidget(LayoutEditor src, Widget widget, int clientX,
            int clientY) {
        description = "Move " + getWidgetDescription(widget);
        startWidgetDrag(src, widget, clientX, clientY);
    }

    /**
     * Pickup the widget and enter 'drop it somewhere' mode.
     */
    private void pickupWidget(LayoutEditor src, Widget widget) {
        pickupOrAddWidget(src, widget, "Drop " + getWidgetDescription(widget));
    }

    /**
     * Get a short description for w. This is used to describe operations
     * for undo/redo etc. Subclasses can override this to provide more
     * information about their widgets.
     */
    public String getWidgetDescription(Widget w) {
        StringBuffer s = new StringBuffer();
        if (w instanceof Portlet) {
            s.append(((Portlet)w).getWidgetName());
        } else if (w instanceof Container) {
            s.append(((Container)w).getDescription());
        } else {
            s.append("Widget");
        }
        return s.toString();
    }

    /**
     * Hide all resizers and indicators.
     */
    private void hideAllResizers() {
        hideOtherResizers((Container)null);
    }

    /**
     * Hide all resizers and indicators that have nothing to do with container.
     * If container is null then all are hidden.
     */
    private void hideOtherResizers(Container container) {
        for (int i = resizerList.size() - 1; i >= 0; i--) {
            WidgetResizer r = (WidgetResizer)resizerList.get(i);
            r.setVisible(r.getEditor().getContainer() == container);
        }
        for (int i = emptyList.size() - 1; i >= 0; i--) {
            ContainerIndicator ci = (ContainerIndicator)emptyList.get(i);
            ci.setVisible(ci.getEditor().getContainer() == container);
        }
    }

    /**
     * Hide all resizers and indicators that have nothing to do with widget.
     * If widget is null then all are hidden.
     */
    private void hideOtherResizers(Widget widget) {
        for (int i = resizerList.size() - 1; i >= 0; i--) {
            WidgetResizer r = (WidgetResizer)resizerList.get(i);
            r.setVisible(r.getTarget() == widget);
        }
        for (int i = emptyList.size() - 1; i >= 0; i--) {
            ContainerIndicator ci = (ContainerIndicator)emptyList.get(i);
            ci.setVisible(ci.getEditor().getContainer() == widget);
        }
    }

    /**
     * Show all resizers and indicators.
     */
    private void showAllResizers() {
        for (int i = resizerList.size() - 1; i >= 0; i--) {
            ((WidgetResizer)resizerList.get(i)).setVisible(true);
        }
        for (int i = emptyList.size() - 1; i >= 0; i--) {
            ((ContainerIndicator)emptyList.get(i)).setVisible(true);
        }
        update();
        // have to do an update as sometimes the indicators
        // dont display properly after setVisible(true)        
    }

    private void deleteWidget(LayoutEditor src, Widget widget) {
        raiseOverlay();
        hideAllResizers();
        moveIndicator(heldIndicator, widget);
        if (Window.confirm("Are you sure you want to delete this widget?")) {
            beginUndo("Delete " + getWidgetDescription(widget));
            widget.removeFromParent();
            src.getContainer().layout();
            onWidgetDeleted(widget);
        }
        showAllResizers();
        discardHeldWidget();
    }

    /**
     * Raise the overlay panel above the resizers.
     */
    private void raiseOverlay() {
        overlay.addStyleDependentName(STYLE_RAISED);
    }

    /**
     * Lower the overlay panel below the resizers.
     */
    private void lowerOverlay() {
        overlay.removeStyleDependentName(STYLE_RAISED);
    }

    /**
     * The widget has been deleted from the layout. Override this to do
     * extra cleanup.
     */
    protected void onWidgetDeleted(Widget widget) {
    }

    private void pickupOrAddWidget(LayoutEditor src, Widget widget,
            String description) {
        Event ev = DOM.eventGetCurrentEvent();
        startWidgetDrag(src, widget,
                ev == null ? -1 : DOM.eventGetClientX(ev),
                ev == null ? -1 : DOM.eventGetClientY(ev)
        );
        raiseOverlay();
        pickupBusy = true;

        boolean pickup = widget.isAttached();
        if (pickup) {
            moveIndicator(heldIndicator, widget);
        }                   

        this.description = description;
        status.beginOperation(description,
                "Choose position for widget and click", false, discardHeldWidget);
    }

    /**
     * Move the indicator to cover 'over' and show it.
     */
    private void moveIndicator(Widget indicator, Widget over) {
        Rectangle r = LDOM.getBounds(over);
        if (!indicator.isAttached()) {
            RootPanel.get().add(indicator);
        }
        LDOM.setBounds(indicator, r);
    }

    /**
     * Hide the indicator if it is showing.
     */
    private void hideIndicator(Widget indicator) {
        if (indicator.isAttached()) {
            RootPanel.get().remove(indicator);
        }
    }

    private void startWidgetDrag(LayoutEditor src, Widget widget, int left,
            int top) {
        hideMenu();
        heldWidget = widget;
        heldWidgetContainer = src == null ? null : src.getContainer();
        heldConstraints = heldWidgetContainer == null
                ? null
                : heldWidgetContainer.getLayoutConstraints(widget);
        moveDropIndicator(left, top);
    }

    /**
     * Drop the currently held widget at clientX, clientY.
     */
    public void dropWidget(int clientX, int clientY) {
        if (heldWidget == null) {
            return;
        }
        LayoutEditor dest = getEditorFor(clientX, clientY);
        if (dest != null) {
            if (description == null) {
                throw new IllegalStateException("description == null");
            }
            beginUndo(description);
            if (dest.dropWidget(heldWidget, heldConstraints, overflow,
                    clientX, clientY)) {
                if (heldWidgetContainer != dest.getContainer()) {
                    dest.getContainer().layout();
                }
                if (refreshOnDrop) {
                    refresh(heldWidget);
                }
            } else {
                discardUndo();    
            }
        }
        if (heldWidgetContainer != null) {
            heldWidgetContainer.layout();
        }
        int newDepth = drillDownOnDrop ? editDepth + 1 : editDepth;
        discardHeldWidget();
        setEditDepth(newDepth);
        cancelPendingUpdate();
    }

    private void discardHeldWidget() {
        hideIndicator(dropIndicator);
        hideIndicator(heldIndicator);
        pickupBusy = false;
        heldWidget = null;
        heldWidgetContainer = null;
        heldConstraints = null;
        drillDownOnDrop = false;
        overflow = null;
        description = null;
        refreshOnDrop = false;
        lowerOverlay();
        status.endOperation();
    }

    /**
     * Move the drop indicator over the widget at left and top (if any).
     */
    private void moveDropIndicator(int clientX, int clientY) {
        lastDropIndicatorClientX = clientX;
        lastDropIndicatorClientY = clientY;
        Rectangle r = null;
        if (heldWidget != null) {
            LayoutEditor dest = getEditorFor(clientX, clientY);
            if (dest != null) {
                r = dest.getDropArea(heldWidget, heldConstraints, clientX, clientY);
            }
        }
        if (r == null) {
            RootPanel.get().remove(dropIndicator);
        } else {
            if (!dropIndicator.isAttached()) {
                RootPanel.get().add(dropIndicator);
            }
            LDOM.setBounds(dropIndicator, r);
        }
    }

    /**
     * Display an editor menu suitable for resizer. If resizer is null then
     * display a general layout editing menu. If the menu to be displayed
     * is already visible then hide it instead.
     */
    public void toggleMenuForWidget(WidgetResizer resizer) {
        if (resizer == menuTarget && menuPopup != null) {
            hideMenu();
            return;
        }
        menuTarget = resizer;
        MenuBar bar = createMenuBar(true);
        if (resizer != null) {
            buildEditorMenuItems(resizer.getEditor(), resizer.getTarget(),  bar);
        }
        buildManagerMenuItems(bar);
        displayMenu(bar, -1, -1);
    }

    /**
     * Create a menu bar for use in the page editor.
     */
    public MenuBar createMenuBar(boolean vertical) {
        return new PageEditorMenuBar(this, vertical);
    }

    /**
     * Display an editor menu suitable for indicator. If the menu to be
     * displayed is already visible then hide it instead.
     */
    public void toggleMenuForContainer(ContainerIndicator indicator) {
        if (indicator == menuTarget && menuPopup != null) {
            hideMenu();
            return;
        }
        menuTarget = indicator;
        MenuBar bar = createMenuBar(true);
        buildEditorMenuItems(indicator.getEditor(), null, bar);
        buildManagerMenuItems(bar);
        displayMenu(bar, -1, -1);
    }

    /**
     * Add menu items appropriate to editor and widget to bar. Note that
     * widget may be null but editor will never be null.
     */
    private void buildEditorMenuItems(final LayoutEditor editor,
            final Widget widget, MenuBar bar) {

        if (widget != null && editor.isEditConstraints(widget)) {
            bar.addItem("Edit Constraints...", new Command() {
                public void execute() {
                    hideOtherResizers(widget);
                    beginUndo("Edit " + getWidgetDescription(widget) + " Constraints");
                    editor.editConstraints(widget, new ChangeListener() {
                        public void onChange(Widget sender) {
                            onConstraintsChanged(widget);
                        }
                    }, editDialogClosed);
                }
            }).setTitle("Edit widget layout constraints");
        }

        editor.createMenuItems(widget, bar);

        if (widget != null) {
            bar.addItem("Pickup", new Command() {
                public void execute() {
                    pickupWidget(editor, widget);
                }
            }).setTitle("Pickup the widget (and click to drop it somewhere)");

            bar.addItem("Delete...", new Command() {
                public void execute() {
                    deleteWidget(editor, widget);
                }
            }).setTitle("Remove the widget");

            bar.addItem("Replace With", buildReplaceWithMenu(widget));

            WidgetEditor we = getWidgetEditorFor(widget);
            if (we != null) {
                we.createMenuItems(this, bar, widget);
            }
        }

        bar.addItem("Parent Container", buildParentMenu(editor));
    }

    /**
     * The widgets constraints have been updated.
     */
    protected void onConstraintsChanged(Widget widget) {
    }

    /**
     * Build a menu to replace widget with another widget or container.
     */
    private MenuBar buildReplaceWithMenu(final Widget widget) {
        WidgetOption[] a = getWidgetOptionList();
        MenuBar bar = createMenuBar(true);
        for (int i = 0; i < a.length; i++) {
            bar.addItem(createReplaceWithMenuItem(widget, a[i]));
        }
        return bar;
    }

    /**
     * Create a menu item to replace a widget with another widget or container.
     */
    private MenuItem createReplaceWithMenuItem(final Widget widget,
            final WidgetOption op) {
        MenuItem mi = new MenuItem(op.getName(), new Command() {
            public void execute() {
                op.createWidget(widget, new WidgetCallback() {
                    public void onSuccess(Widget w) {
                        replaceWidget(widget, w, op.isContainerOption(),
                                op.isAddPlaceholderOnContain(),
                                op.isRefreshNeeded(w));
                    }
                });
            }
        });
        mi.setTitle((op.isContainerOption()
                    ? "Put widget into new "
                    : "Replace with ") +
                op.getDescription());
        return mi;
    }

    /**
     * Replace old with nw. Prompts for confirmation, creates unto record
     * etc. If addOldToNew is true is a Container then old is added to it
     * i.e. it takes the place of old but contains old.
     */
    private void replaceWidget(Widget old, Widget nw, boolean addOldToNew,
            boolean addPlaceholder, boolean refresh) {
        if (!addOldToNew) {
            hideAllResizers();
            moveIndicator(heldIndicator, old);
            boolean ok = Window.confirm(
                    "Are you sure you want to " +
                    "replace this widget with " +
                    getWidgetDescription(nw));
            showAllResizers();
            discardHeldWidget();
            if (!ok) {
                return;
            }
        }
        beginUndo("Replace " + getWidgetDescription(old) +
                " with " + getWidgetDescription(nw));
        replaceWidgetImpl(old, nw, addOldToNew,
                addOldToNew && addPlaceholder ? createPlaceholder() : null);
        if (refresh) {
            refresh(nw);
        }
        if (addOldToNew) {
            setEditDepth(editDepth + 1);
        }
    }

    private Widget createPlaceholder() {
        return new PlaceholderPortlet();
    }

    /**
     * Replace old with nw. If addOldToNew is true then nw must be a Container
     * and old is added to it.
     */
    private void replaceWidgetImpl(Widget old, Widget nw, boolean addOldToNew,
            Widget placeholder) {
        Container con = (Container)old.getParent();
        int index = con.getWidgetIndex(old);
        LayoutConstraints constraints = con.getLayoutConstraints(old);
        con.remove(old);
        if (addOldToNew) {
            Container newCon = (Container)nw;
            newCon.add(old);
            if (placeholder != null) {
                newCon.add(placeholder);
            }
        } else {
            onWidgetDeleted(old);
        }
        con.insert(nw, index, constraints);
        con.layout();
        // con may not be at current edit depth so we may not get a
        // layoutUpdated notification so schedule an update
        scheduleUpdate();
    }

    /**
     * Build a menu of container specific menu items for the container
     * belonging to editor for the 'Parent >' menu.
     *
     * @see org.gwtportlets.portlet.client.edit.LayoutEditor#getContainer()
     */
    private MenuBar buildParentMenu(final LayoutEditor editor) {
        MenuBar bar = createMenuBar(true);
        final Container con = editor.getContainer();

        if (editor.isEditLayout()) {
            bar.addItem("Edit Layout...", new Command() {
                public void execute() {
                    hideOtherResizers(con);
                    raiseOverlay();
                    moveIndicator(heldIndicator, (Widget)con);
                    beginUndo("Edit " + getWidgetDescription((Widget)con) +
                            " Layout");
                    editor.editLayout(editDialogClosed);
                }
            }).setTitle("Edit container layout settings");
        }

        MenuBar changeTo = buildChangeContainerToMenu(con);
        if (changeTo != null) {
            bar.addItem("Change To", changeTo).setTitle(
                    "Convert this container into a different container");
        }

        WidgetEditor we = getWidgetEditorFor((Widget)con);
        if (we != null) {
            we.createMenuItems(this, bar, (Widget)con);
        }

        return bar;
    }

    /**
     * Build a menu to replace con with a different container and move all of
     * its children to the new container. Returns null if menu would be empty.
     */
    private MenuBar buildChangeContainerToMenu(final Container con) {
        MenuBar bar = createMenuBar(true);
        WidgetOption[] a = getWidgetOptionList();
        int c = 0;
        for (int i = 0; i < a.length; i++) {
            WidgetOption op = a[i];
            if (op.isContainerOption()) {
                bar.addItem(createChangeContainerToMenuItem(con, op));
                ++c;
            }
        }
        return c == 0 ? null : bar;
    }

    /**
     * Create a menu item to replace a container with a different container
     * and move all of its children to the new container. The new container
     * widget is provided by op.
     */
    private MenuItem createChangeContainerToMenuItem(final Container con,
            final WidgetOption op) {
        MenuItem mi = new MenuItem(op.getName(), new Command() {
            public void execute() {
                op.createWidget(null, new WidgetCallback() {
                    public void onSuccess(Widget w) {
                        changeContainerTo(con, w);
                    }
                });
            }
        });
        mi.setTitle("Change to " + op.getDescription());
        return mi;
    }

    /**
     * Replace old with nw (must be a Container) and move all of olds
     * children to nw. Undo support is included.
     */
    private void changeContainerTo(Container old, Widget nw) {
        beginUndo("Change To " + getWidgetDescription(nw));
        moveChildren(old, (Container)nw);
        replaceWidgetImpl((Widget)old, nw, false, null);
    }

    /**
     * Add menu options for this layout editor manager to bar (e.g. for
     * undo, redo and add widget).
     */
    protected void buildManagerMenuItems(MenuBar bar) {
        UndoStack.Operation op = undoStack.getUndo();
        if (op != null) {
            String s = "Undo " + op.getDescription();
            bar.addItem(s, new Command() {
                public void execute() {
                    undoStack.undo();
                }
            }).setTitle(s + " (Ctrl-Z)");
        }

        op = undoStack.getRedo();
        if (op != null) {
            String s = "Redo " + op.getDescription();
            bar.addItem(s, new Command() {
                public void execute() {
                    undoStack.redo();
                }
            }).setTitle(s + " (Ctrl-Shift-Z)");
        }

        bar.addItem("Add", buildAddMenu());
    }

    /**
     * Build a menu to add new widgets to the layout.
     */
    private MenuBar buildAddMenu() {
        MenuBar bar = createMenuBar(true);
        WidgetOption[] a = getWidgetOptionList();
        for (int i = 0; i < a.length; i++) {
            bar.addItem(createAddMenuItem(a[i]));
        }
        return bar;
    }

    /**
     * Create a menu item to add a widget from an option.
     */
    private MenuItem createAddMenuItem(final WidgetOption op) {
        MenuItem mi = new MenuItem(op.getName(), new Command() {
            public void execute() {
                try {
                    op.createWidget(null, new WidgetCallback() {
                        public void onSuccess(Widget w) {
                            addWidget(w, op.isRefreshNeeded(w), op.getOverflow());
                        }
                    });
                } catch (Exception e) {
                    handleCreateWidgetFailure(op, e);
                }
            }
        });
        mi.setTitle("Add " + op.getDescription());
        return mi;
    }

    protected void handleCreateWidgetFailure(WidgetOption op, Exception e) {
        GWT.log(e.toString(), e);
        Window.alert("Error creating " + op.getName() + ":\n" + e);
    }

    /**
     * Add the widget to the layout with support for choosing where to
     * put it, undo and so on. If w is a container then the edit depth
     * is changed to its level when it is dropped.
     */
    protected void addWidget(Widget w, boolean refreshOnDrop, String overflow) {
        pickupOrAddWidget(null, w, "Add " + getWidgetDescription(w));
        drillDownOnDrop = w instanceof Container;
        this.refreshOnDrop = refreshOnDrop;
        this.overflow = overflow;
    }

    /**
     * Display the menu, hiding any other menu. If clientX and clientY are
     * less than zero then the current event is used to decide where to
     * display the menu.
     */
    protected void displayMenu(MenuBar bar, int clientX, int clientY) {
        hideMenu();
        new WidgetZIndexer().start();
        menuPopup = new PopupPanel(false);
        menuPopup.addPopupListener(new PopupListener() {
            public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
                menuPopup = null;
            }
        });
        menuPopup.add(bar);
        if (clientX < 0 && clientY < 0) {
            Event ev = DOM.eventGetCurrentEvent();
            if (ev == null) {
                menuPopup.center();
                return;
            }
            clientX = DOM.eventGetClientX(ev) - 4;
            clientY = DOM.eventGetClientY(ev);
        }
        menuPopup.setPopupPosition(clientX, clientY);
        menuPopup.show();
    }

    /**
     * Hide our current menu.
     *
     * @see #displayMenu(com.google.gwt.user.client.ui.MenuBar, int, int) 
     */
    public void hideMenu() {
        if (menuPopup != null) {
            menuPopup.hide();
            menuTarget = null;
        }
    }

    public void layoutUpdated(Container container) {
        // we may get a update notification from every one of our containers
        // in a single event cycle so schedule just one update for when
        // event processing is complete
        scheduleUpdate();
    }

    /**
     * Schedule an update for when event processing is complete if an update
     * is not already pending.
     */
    protected void scheduleUpdate() {
        if (!updatePending) {
            updatePending = true;
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    if (updatePending) {
                        updatePending = false;
                        update();
                    }
                }
            });
        }
    }

    /**
     * Cancel any pending update.
     */
    protected void cancelPendingUpdate() {
        updatePending = false;
    }

    /**
     * Move all the children of src to dest, preserving layout constraints if
     * possible and child ordering.
     */
    private void moveChildren(Container src, Container dest) {
        int wc = src.getWidgetCount();
        if (wc == 0) {
            return;
        }
        Widget[] wa = new Widget[wc];
        LayoutConstraints[] ca = new LayoutConstraints[wc];
        Layout layout = dest.getLayout();
        for (int i = wc - 1; i >= 0; i--) {
            Widget w = src.getWidget(i);
            wa[i] = w;
            ca[i] = layout.convertConstraints(src.getLayoutConstraints(w));
            src.remove(w);
        }
        for (int i = 0; i < wc; i++) {
            Widget w = wa[i];
            dest.add(w);
            dest.setLayoutConstraints(w, ca[i]);
        }
    }

    /**
     * Get our control dialog.
     */
    public EditorManagerControlDialog getControl() {
        return status;
    }

    /**
     * Override this to provide a custom WidgetReplacer.
     */
    protected WidgetReplacer createWidgetReplacer() {
        return new WidgetReplacer();
    }

    public WidgetReplacer getWidgetReplacer() {
        return widgetReplacer;
    }

    /**
     * Create the default widget editor used to edit widgets when no
     * other editor available.
     */
    protected DefaultWidgetEditor createDefaultWidgetEditor() {
        return new DefaultWidgetEditor();
    }

    /**
     * Create the editor used for CaptionPanels.
     */
    protected TitlePanelEditor createCaptionPanelEditor() {
        return new TitlePanelEditor();
    }

    /**
     * Create the editor used for LayoutPanels.
     */
    protected LayoutPanelEditor createLayoutPanelEditor() {
        return new LayoutPanelEditor();
    }

    /**
     * Get style names appropriate to widget. Include a null in the array
     * to allow the user to enter a style name.
     *
     * @see org.gwtportlets.portlet.client.edit.DefaultWidgetEditor
     */
    public String[] getStyleNamesFor(Widget widget) {
        return new String[]{
                null,
                "portlet-untitled",
                "portlet-borderless",
        };
    }

    /**
     * Display a dialog to edit widget. Highlights the widget and provides undo
     * support.
     */
    public void displayEditWidgetDialog(Widget w, PageEditorDialog dlg) {
        raiseOverlay();
        hideAllResizers();
        moveIndicator(heldIndicator, w);
        beginUndo("Edit " + getWidgetDescription(w));
        dlg.addPopupListener(editDialogClosed);
        dlg.display();
    }

    private void refresh(final Widget w) {
        if (!(w instanceof WidgetFactoryProvider)) {
            return;
        }
        WidgetFactory wf = ((WidgetFactoryProvider)w).createWidgetFactory();
        LayoutUtil.clearDoNotSendToServerFields(wf);
        WidgetRefreshHandler.App.get().refresh(w, wf,
                new AsyncCallback<WidgetFactory>() {
            public void onFailure(Throwable caught) {
                // what to do here?
            }
            public void onSuccess(WidgetFactory wf) {
                wf.refresh(w);
            }
        });
    }

    /**
     * Display a dialog prompting the user to select a widget to create.
     * Invoke cb with the new widget.
     */
    protected void createWidget(Widget replacing, final AsyncCallback cb) {
        final SelectWidgetFactoryDialog dlg = new SelectWidgetFactoryDialog(
                LayoutUtil.getWidgetFactoryList());
        dlg.addPopupListener(new PopupListener() {
            public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
                if (dlg.isOkPressed()) {
                    Widget w = dlg.getSelectedWidgetFactory()
                            .createWidgetFactory().createWidget();
                    refresh(w);
                    cb.onSuccess(w);
                }
            }
        });
        dlg.center();
    }

    /**
     * Replace our root widget with a new one.
     */
    protected void setRoot(Container root) {
        discardHeldWidget();
        widgetReplacer.replace((Widget)this.root, (Widget)root);
        this.root = root;
        update();
    }

    /**
     * Preserve the current state of the layout so an operation about to
     * be performed can be undone.
     *
     * @see #discardUndo() 
     */
    public void beginUndo(String description) {
        undoStack.push(new Op(description));
    }

    /**
     * Discard the most recently added undo information. Call this if the
     * operation fails to proceed for some reason and no changes are actually
     * made to the layout.
     *
     * @see #beginUndo(String)
     */
    public void discardUndo() {
        undoStack.discard();
    }

    /**
     * An undoable change to the layout.
     */
    public class Op extends UndoStack.Operation {

        private WidgetFactory state;

        /**
         * Create the operation, preserving the state of the layout for undo.
         * Do this before performing the undoable operation.
         */
        public Op(String description) {
            super(description);
            state = root.createWidgetFactory();
        }

        /**
         * Add this operation to the undo stack. Call this when the undoable
         * operation has been completed.
         */
        public void complete() {
            undoStack.push(this);
        }

        protected void undo() {
            swapState();
        }

        protected void redo() {
            swapState();
        }

        private void swapState() {
            Widget newRoot = state.createWidget();
            state.refresh(newRoot);
            state = root.createWidgetFactory();
            setRoot((Container)newRoot);
        }

    }

    /**
     * Used to create MenuItem's that create widgets for some purpose.
     * Override one of the createWidget methods.
     */
    public static abstract class WidgetOption {

        /**
         * Get a short user friendly name for this option (e.g. to use as text
         * for a menu item).
         */
        public abstract String getName();

        /**
         * Get a short user friendly description for this option (e.g. for use
         * as a tooltip).
         */
        public String getDescription() {
            return getName();
        }

        /**
         * Create a new widget and pass it to cb.onSuccess. If replacing is
         * not null then this is the widget being replaced. Pass null to
         * not create anything. It is ok to open dialogs and so on to select
         * the widget to create.
         */
        public void createWidget(Widget replacing, AsyncCallback cb) {
            cb.onSuccess(createWidget(replacing));
        }

        /**
         * Create a new widget. If replacing is not null then this is the widget
         * being replaced. Return null to not create anything.
         */
        public Widget createWidget(Widget replacing) {
            return null;
        }

        /**
         * Does this option create containers?
         */
        public boolean isContainerOption() {
            return false;
        }

        /**
         * Should an extra placeholder widget be added to the Container we
         * create when replacing a widget (i.e. containing it)?
         */
        public boolean isAddPlaceholderOnContain() {
            return isContainerOption();
        }

        /**
         * Does w (newly created) need to be refreshed (e.g. to get data
         * from the server) after being added to the layout?
         */
        public boolean isRefreshNeeded(Widget w) {
            return false;
        }

        /**
         * Get the preferred constraint overflow setting for widgets created
         * by this option.
         */
        public String getOverflow() {
            return RowLayout.Constraints.HIDDEN;
        }

    }

    private abstract static class WidgetCallback implements AsyncCallback {

        public final void onSuccess(Object result) {
            if (result instanceof Widget) {
                onSuccess((Widget)result);
            }
        }

        public abstract void onSuccess(Widget w);

        public void onFailure(Throwable caught) {
        }
    }
    
}
