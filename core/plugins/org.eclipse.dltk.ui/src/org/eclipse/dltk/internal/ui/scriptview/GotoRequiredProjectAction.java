/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.scriptview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Goto to the referenced required project
 */
class GotoRequiredProjectAction extends Action {

	private ScriptExplorerPart fPackageExplorer;

	GotoRequiredProjectAction(ScriptExplorerPart part) {
		super(ScriptMessages.GotoRequiredProjectAction_label);
		setDescription(ScriptMessages.GotoRequiredProjectAction_description);
		setToolTipText(ScriptMessages.GotoRequiredProjectAction_tooltip);
		fPackageExplorer = part;
	}

	@Override
	public void run() {
		IStructuredSelection selection = (IStructuredSelection) fPackageExplorer
				.getSite().getSelectionProvider().getSelection();
		Object element = selection.getFirstElement();
		if (element instanceof BuildPathContainer.RequiredProjectWrapper) {
			BuildPathContainer.RequiredProjectWrapper wrapper = (BuildPathContainer.RequiredProjectWrapper) element;
			fPackageExplorer.tryToReveal(wrapper.getProject());
		}
	}
}
