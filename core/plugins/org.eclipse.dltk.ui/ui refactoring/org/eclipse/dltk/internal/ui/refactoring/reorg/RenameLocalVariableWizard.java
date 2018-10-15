/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.refactoring.reorg;

import org.eclipse.dltk.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.ltk.core.refactoring.Refactoring;

public final class RenameLocalVariableWizard extends RenameRefactoringWizard {

	public RenameLocalVariableWizard(Refactoring refactoring) {
		super(
				refactoring,
				refactoring.getName(),
				RefactoringMessages.RenameTypeParameterWizard_inputPage_description,
				DLTKPluginImages.DESC_WIZBAN_REFACTOR, "" //$NON-NLS-1$
		/* IJavaHelpContextIds.RENAME_LOCAL_VARIABLE_WIZARD_PAGE */);
	}
}
