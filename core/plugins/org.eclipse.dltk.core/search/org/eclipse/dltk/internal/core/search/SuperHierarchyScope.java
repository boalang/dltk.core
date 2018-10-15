/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;

/**
 * Scope limited to the supertype hierarchy of a given type.
 */
public class SuperHierarchyScope extends HierarchyScope {

	public SuperHierarchyScope(IDLTKLanguageToolkit languageToolkit,
			IType type, WorkingCopyOwner owner) throws ModelException {
		super(languageToolkit, type, owner);
	}

	/**
	 * This implementation builds only supertype hierarchy for the given focus
	 * type.
	 * 
	 * @see HierarchyScope#createHierarchy(IType, WorkingCopyOwner,
	 *      IProgressMonitor)
	 */
	@Override
	protected ITypeHierarchy createHierarchy(IType focusType,
			WorkingCopyOwner owner, IProgressMonitor monitor)
			throws ModelException {
		return focusType.newSupertypeHierarchy(owner, monitor);
	}
}
