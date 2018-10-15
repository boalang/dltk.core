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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.dltk.internal.corext.refactoring.base.DLTKChange;
import org.eclipse.dltk.internal.corext.util.Messages;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

public final class RenameResourceChange extends DLTKChange {

	public static IPath renamedResourcePath(IPath path, String newName) {
		return path.removeLastSegments(1).append(newName);
	}

	private final String fComment;

	private final RefactoringDescriptor fDescriptor;

	private final String fNewName;

	private final IPath fResourcePath;

	private final long fStampToRestore;

	private RenameResourceChange(RefactoringDescriptor descriptor,
			IPath resourcePath, String newName, String comment,
			long stampToRestore) {
		fDescriptor = descriptor;
		fResourcePath = resourcePath;
		fNewName = newName;
		fComment = comment;
		fStampToRestore = stampToRestore;
	}

	public RenameResourceChange(RefactoringDescriptor descriptor,
			IResource resource, String newName, String comment) {
		this(descriptor, resource.getFullPath(), newName, comment,
				IResource.NULL_STAMP);
	}

	@Override
	public ChangeDescriptor getDescriptor() {
		if (fDescriptor != null)
			return new RefactoringChangeDescriptor(fDescriptor);
		return null;
	}

	@Override
	public Object getModifiedElement() {
		return getResource();
	}

	@Override
	public String getName() {
		return Messages.format(
				RefactoringCoreMessages.RenameResourceChange_name,
				fResourcePath.toString(), fNewName);
	}

	public String getNewName() {
		return fNewName;
	}

	private IResource getResource() {
		return ResourcesPlugin.getWorkspace().getRoot()
				.findMember(fResourcePath);
	}

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		IResource resource = getResource();
		if (resource == null || !resource.exists()) {
			return RefactoringStatus.createFatalErrorStatus(Messages.format(
					RefactoringCoreMessages.RenameResourceChange_does_not_exist,
					fResourcePath.toString()));
		}
		return super.isValid(pm, DIRTY);
	}

	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		try {
			pm.beginTask(
					RefactoringCoreMessages.RenameResourceChange_rename_resource,
					1);

			IResource resource = getResource();
			long currentStamp = resource.getModificationStamp();
			IPath newPath = renamedResourcePath(fResourcePath, fNewName);
			resource.move(newPath, IResource.SHALLOW, pm);
			if (fStampToRestore != IResource.NULL_STAMP) {
				IResource newResource = ResourcesPlugin.getWorkspace().getRoot()
						.findMember(newPath);
				newResource.revertModificationStamp(fStampToRestore);
			}
			String oldName = fResourcePath.lastSegment();
			return new RenameResourceChange(null, newPath, oldName, fComment,
					currentStamp);
		} finally {
			pm.done();
		}
	}
}
