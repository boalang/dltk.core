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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public class MoveSourceModuleChange extends SourceModuleReorgChange {

	private boolean fUndoable;
	private long fStampToRestore;

	public MoveSourceModuleChange(ISourceModule cu, IScriptFolder newPackage) {
		super(cu, newPackage);
		fStampToRestore = IResource.NULL_STAMP;
	}

	private MoveSourceModuleChange(IScriptFolder oldPackage, String cuName, IScriptFolder newPackage,
			long stampToRestore) {
		super(oldPackage.getHandleIdentifier(), newPackage.getHandleIdentifier(),
				oldPackage.getSourceModule(cuName).getHandleIdentifier());
		fStampToRestore = stampToRestore;
	}

	@Override
	public String getName() {
		return MessageFormat.format(RefactoringCoreMessages.MoveSourceModuleChange_name, getCu().getElementName(),
				getPackageName(getDestinationPackage()));
	}

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		return super.isValid(pm, READ_ONLY | SAVE_IF_DIRTY);
	}

	@Override
	Change doPerformReorg(IProgressMonitor pm) throws CoreException {
		String name;
		String newName = getNewName();
		if (newName == null)
			name = getCu().getElementName();
		else
			name = newName;

		// get current modification stamp
		long currentStamp = IResource.NULL_STAMP;
		IResource resource = getCu().getResource();
		if (resource != null) {
			currentStamp = resource.getModificationStamp();
		}

		fUndoable = !getDestinationPackage().getSourceModule(name).exists();

		// perform the move and restore modification stamp
		getCu().move(getDestinationPackage(), null, newName, true, pm);
		if (fStampToRestore != IResource.NULL_STAMP) {
			ISourceModule moved = getDestinationPackage().getSourceModule(name);
			IResource movedResource = moved.getResource();
			if (movedResource != null) {
				movedResource.revertModificationStamp(fStampToRestore);
			}
		}

		if (fUndoable) {
			return new MoveSourceModuleChange(getDestinationPackage(), getCu().getElementName(), getOldPackage(),
					currentStamp);
		}
		return null;
	}
}
