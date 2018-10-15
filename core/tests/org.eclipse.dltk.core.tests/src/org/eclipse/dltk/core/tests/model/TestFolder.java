/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.  
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.eclipse.dltk.core.tests.model;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.ScriptFolder;

public class TestFolder extends ScriptFolder {
	protected TestFolder(ModelElement parent, IPath path) {
		super(parent, path);
	}

	@Override
	public String getElementName() {
		return super.getElementName() + "Goo";
	}

	@Override
	public boolean isRootFolder() {
		return false;
	}

	@Override
	public IResource getResource() {
		IProjectFragment root = this.getProjectFragment();
		if (root.isArchive()) {
			return root.getResource();
		} else {
			if (path.segmentCount() == 0)
				return root.getResource();
			IContainer container = (IContainer) root.getResource();
			if (container != null) {
				return container.getFolder(path);
			}
			return null;
		}
	}

	@Override
	public IPath getPath() {
		return super.getPath().append("Goo");
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return JEM_USER_ELEMENT;
	}
}
