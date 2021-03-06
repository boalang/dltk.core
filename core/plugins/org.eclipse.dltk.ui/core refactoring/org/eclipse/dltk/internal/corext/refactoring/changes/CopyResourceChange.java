/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.refactoring.changes;

import java.text.MessageFormat;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.dltk.internal.corext.refactoring.reorg.INewNameQuery;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class CopyResourceChange extends ResourceReorgChange {

	public CopyResourceChange(IResource res, IContainer dest, INewNameQuery newNameQuery) {
		super(res, dest, newNameQuery);
	}

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		// Copy resource change isn't undoable and isn't used
		// as a redo/undo change right now. Furthermore the current
		// implementation allows copying dirty files. In this case only
		// the content on disk is copied.
		return super.isValid(pm, NONE);
	}

	@Override
	protected Change doPerformReorg(IPath path, IProgressMonitor pm) throws CoreException {
		getResource().copy(path, getReorgFlags(), pm);
		return null;
	}

	@Override
	public String getName() {
		return MessageFormat.format(RefactoringCoreMessages.CopyResourceString_copy,
				getResource().getFullPath().toString(), getDestination().getName());
	}
}
