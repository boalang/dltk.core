/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.core.tests.model;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.BufferChangedEvent;
import org.eclipse.dltk.core.IBuffer;
import org.eclipse.dltk.core.IBufferChangedListener;
import org.eclipse.dltk.core.IOpenable;

/*
 * A simple implementation of IBuffer.
 */
public class TestBuffer implements IBuffer {
	private IOpenable owner;
	private ArrayList<IBufferChangedListener> changeListeners;
	private char[] contents = null;
	private boolean hasUnsavedChanges = false;

	public TestBuffer(IOpenable owner) {
		this.owner = owner;
	}

	@Override
	public void addBufferChangedListener(IBufferChangedListener listener) {
		if (this.changeListeners == null) {
			this.changeListeners = new ArrayList<>(5);
		}
		if (!this.changeListeners.contains(listener)) {
			this.changeListeners.add(listener);
		}
	}

	@Override
	public void append(char[] text) {
		this.hasUnsavedChanges = true;
	}

	@Override
	public void append(String text) {
		this.hasUnsavedChanges = true;
	}

	@Override
	public void close() {
		this.contents = null; // mark as closed
		if (this.changeListeners != null) {
			BufferChangedEvent event = null;
			event = new BufferChangedEvent(this, 0, 0, null);
			for (int i = 0, size = this.changeListeners.size(); i < size; ++i) {
				IBufferChangedListener listener = this.changeListeners.get(i);
				listener.bufferChanged(event);
			}
			this.changeListeners = null;
		}
	}

	@Override
	public char getChar(int position) {
		return 0;
	}

	@Override
	public char[] getCharacters() {
		return contents;
	}

	@Override
	public String getContents() {
		return new String(contents);
	}

	@Override
	public int getLength() {
		return contents.length;
	}

	@Override
	public IOpenable getOwner() {
		return this.owner;
	}

	@Override
	public String getText(int offset, int length) {
		return null;
	}

	@Override
	public IResource getUnderlyingResource() {
		return null;
	}

	@Override
	public boolean hasUnsavedChanges() {
		return this.hasUnsavedChanges;
	}

	@Override
	public boolean isClosed() {
		return this.contents == null;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public void removeBufferChangedListener(IBufferChangedListener listener) {
		if (this.changeListeners != null) {
			this.changeListeners.remove(listener);
			if (this.changeListeners.size() == 0) {
				this.changeListeners = null;
			}
		}
	}

	@Override
	public void replace(int position, int length, char[] text) {
		this.hasUnsavedChanges = true;
	}

	@Override
	public void replace(int position, int length, String text) {
		this.hasUnsavedChanges = true;
	}

	@Override
	public void save(IProgressMonitor progress, boolean force) {
		this.hasUnsavedChanges = false;
	}

	@Override
	public void setContents(char[] characters) {
		this.contents = characters;
		this.hasUnsavedChanges = true;
	}

	@Override
	public void setContents(String characters) {
		this.contents = characters.toCharArray();
		this.hasUnsavedChanges = true;
	}
}
