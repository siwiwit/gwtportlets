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

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of undoable operations to help with undo/redo support.
 */
public class UndoStack {

    private int maxSize;
    private List list = new ArrayList();
    private int undoIndex = -1;

    public UndoStack() {
        this(32);
    }

    public UndoStack(int maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * Add an undoable operation.
     */
    public void push(Operation op) {
        // if pos is not at the end of the list (i.e. some undo's have been
        // done) then get rid of the operations that can no longer be redone
        for (int i = list.size() - 1; i > undoIndex; i--) {
            list.remove(i);
        }
        list.add(op);
        if (list.size() > maxSize) {
            list.remove(0);
        }
        undoIndex = list.size() - 1;
    }

    /**
     * Discard the most recently pushed operation from the stack. Use this
     * if the operation didn't actually take place to get rid of it.
     */
    public void discard() {
        if (undoIndex < list.size() - 1) {
            throw new IllegalStateException("Cannot discard after an undo");
        }
        list.remove(list.size() - 1);
        undoIndex = list.size() - 1;
    }

    /**
     * Get the operation that is next to be undone i.e. should go on
     * the undo menu item or null if none.
     */
    public Operation getUndo() {
        return undoIndex < 0 ? null : (Operation)list.get(undoIndex);
    }

    /**
     * Get the operation that is next to be redone i.e. should go on the
     * redo menu item or null if none.
     */
    public Operation getRedo() {
        return undoIndex == list.size() - 1
                ? null
                : (Operation)list.get(undoIndex + 1);
    }

    /**
     * Undo the operation that is next to be undone. NOP if none.
     */
    public void undo() {
        Operation op = getUndo();
        if (op != null) {
            op.undo();
            --undoIndex;
        }
    }

    /**
     * Redo the operation that is next to be redone. NOP if none.
     */
    public void redo() {
        Operation op = getRedo();
        if (op != null) {
            op.redo();
            ++undoIndex;
        }
    }

    public void clear() {
        list.clear();
        undoIndex = -1;
    }

    /**
     * An undoable operation.
     */
    public abstract static class Operation {

        private String description;

        public Operation(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        /**
         * Undo the operation.
         */
        protected abstract void undo();

        /**
         * Redo the operation.
         */
        protected abstract void redo();

    }

}
