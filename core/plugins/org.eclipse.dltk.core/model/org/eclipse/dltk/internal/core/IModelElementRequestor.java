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

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IType;

/**
 * This interface is used by IRequestorNameLookup. As results are found by
 * IRequestorNameLookup, they are reported to this interface. An
 * IModelElementRequestor is able to cancel at any time (that is, stop receiving
 * results), by responding <code>true</code> to <code>#isCancelled</code>.
 */
public interface IModelElementRequestor {
	public void acceptField(IField field);

	public void acceptMemberType(IType type);

	public void acceptMethod(IMethod method);

	public void acceptScriptFolder(IScriptFolder ScriptFolder);

	public void acceptType(IType type);

	/**
	 * Returns <code>true</code> if this IModelElementRequestor does not want
	 * to receive any more results.
	 */
	boolean isCanceled();
}
