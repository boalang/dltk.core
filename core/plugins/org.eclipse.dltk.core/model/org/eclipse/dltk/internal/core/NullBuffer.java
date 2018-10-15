/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.IOpenable;

/**
 * This class represents a null buffer. This buffer is used to represent a buffer for a class file
 * that has no source attached.
 */
public class NullBuffer extends Buffer {
	/**
	 * Creates a new null buffer on an underlying resource.
	 */
	public NullBuffer(IFile file, IOpenable owner, boolean readOnly) {
		super(file, owner, readOnly);
	}
}
