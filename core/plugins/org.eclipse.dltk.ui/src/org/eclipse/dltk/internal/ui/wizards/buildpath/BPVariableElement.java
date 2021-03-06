/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.wizards.buildpath;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;

public class BPVariableElement {

	private String fName;
	private IPath fPath;

	/**
	 * @param name
	 *            the variable name
	 * @param path
	 *            the path
	 */
	public BPVariableElement(String name, IPath path) {
		Assert.isNotNull(name);
		Assert.isNotNull(path);
		fName = name;
		fPath = path;
	}

	/**
	 * Gets the path
	 *
	 * @return Returns a IPath
	 */
	public IPath getPath() {
		return fPath;
	}

	/**
	 * Sets the path
	 *
	 * @param path
	 *            The path to set
	 */
	public void setPath(IPath path) {
		Assert.isNotNull(path);
		fPath = path;
	}

	/**
	 * Gets the name
	 *
	 * @return Returns a String
	 */
	public String getName() {
		return fName;
	}

	/**
	 * Sets the name
	 *
	 * @param name
	 *            The name to set
	 */
	public void setName(String name) {
		Assert.isNotNull(name);
		fName = name;
	}

	@Override
	public boolean equals(Object other) {
		if (other != null && other.getClass().equals(getClass())) {
			BPVariableElement elem = (BPVariableElement) other;
			return fName.equals(elem.fName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return fName.hashCode();
	}

	/**
	 * @return <code>true</code> iff variable is read-only
	 */
	public boolean isReadOnly() {
		return DLTKCore.isBuildpathVariableReadOnly(fName);
	}
}
