/* Copyright (c) 2010-2015 Vanderbilt University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.vu.isis.ammo.contacts.content;

import android.content.Entity;
import android.content.EntityIterator;
import android.database.Cursor;
import android.os.RemoteException;

/**
 * Abstract implementation of EntityIterator that makes it easy to wrap a cursor
 * that can contain several consecutive rows for an entity.
 * @hide
 */
public abstract class CursorEntityIterator implements EntityIterator {
    private final Cursor mCursor;
    private boolean mIsClosed;

    /**
     * Constructor that makes initializes the cursor such that the iterator points to the
     * first Entity, if there are any.
     * @param cursor the cursor that contains the rows that make up the entities
     */
    public CursorEntityIterator(Cursor cursor) {
        mIsClosed = false;
        mCursor = cursor;
        mCursor.moveToFirst();
    }

    /**
     * Returns the entity that the cursor is currently pointing to. This must take care to advance
     * the cursor past this entity. This will never be called if the cursor is at the end.
     * @param cursor the cursor that contains the entity rows
     * @return the entity that the cursor is currently pointing to
     * @throws RemoteException if a RemoteException is caught while attempting to build the Entity
     */
    public abstract Entity getEntityAndIncrementCursor(Cursor cursor) throws RemoteException;

    /**
     * Returns whether there are more elements to iterate, i.e. whether the
     * iterator is positioned in front of an element.
     *
     * @return {@code true} if there are more elements, {@code false} otherwise.
     * @see EntityIterator#next()
     */
    public final boolean hasNext() {
        if (mIsClosed) {
            throw new IllegalStateException("calling hasNext() when the iterator is closed");
        }

        return !mCursor.isAfterLast();
    }

    /**
     * Returns the next object in the iteration, i.e. returns the element in
     * front of the iterator and advances the iterator by one position.
     *
     * @return the next object.
     * @throws java.util.NoSuchElementException
     *             if there are no more elements.
     * @see EntityIterator#hasNext()
     */
    public Entity next() {
        if (mIsClosed) {
            throw new IllegalStateException("calling next() when the iterator is closed");
        }
        if (!hasNext()) {
            throw new IllegalStateException("you may only call next() if hasNext() is true");
        }

        try {
            return getEntityAndIncrementCursor(mCursor);
        } catch (RemoteException e) {
            throw new RuntimeException("caught a remote exception, this process will die soon", e);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("remove not supported by EntityIterators");
    }

    public final void reset() {
        if (mIsClosed) {
            throw new IllegalStateException("calling reset() when the iterator is closed");
        }
        mCursor.moveToFirst();
    }

    /**
     * Indicates that this iterator is no longer needed and that any associated resources
     * may be released (such as a SQLite cursor).
     */
    public final void close() {
        if (mIsClosed) {
            throw new IllegalStateException("closing when already closed");
        }
        mIsClosed = true;
        mCursor.close();
    }
}
