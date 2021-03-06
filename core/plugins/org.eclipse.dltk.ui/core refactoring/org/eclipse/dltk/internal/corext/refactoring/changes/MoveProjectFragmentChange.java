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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.dltk.internal.corext.refactoring.reorg.IProjectFragmentManipulationQuery;
import org.eclipse.ltk.core.refactoring.Change;

public class MoveProjectFragmentChange extends ProjectFragmentReorgChange {

	public MoveProjectFragmentChange(IProjectFragment root, IProject destination,
			IProjectFragmentManipulationQuery updateClasspathQuery) {
		super(root, destination, null, updateClasspathQuery);
	}

	@Override
	protected Change doPerformReorg(IPath destinationPath, IProgressMonitor pm) throws ModelException {
		getRoot().move(destinationPath, getResourceUpdateFlags(), getUpdateModelFlags(false), null, pm);
		return null;
	}

	@Override
	public String getName() {
		return MessageFormat.format(RefactoringCoreMessages.MoveProjectFragmentChange_move, getRoot().getElementName(),
				getDestinationProject().getName());
	}
}
