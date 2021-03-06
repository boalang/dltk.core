/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.search;

import org.eclipse.dltk.internal.ui.actions.CompositeActionGroup;
import org.eclipse.dltk.internal.ui.actions.NavigateActionGroup;
import org.eclipse.dltk.internal.ui.actions.refactoring.RefactorActionGroup;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionGroup;

class NewSearchViewActionGroup extends CompositeActionGroup {
	NavigateActionGroup fNavigateActionGroup;

	public NewSearchViewActionGroup(IViewPart part) {
		setGroups(new ActionGroup[] {
				fNavigateActionGroup = new NavigateActionGroup(part),
				// new GenerateActionGroup(part),
				new RefactorActionGroup(part, null),
				// new DLTKSearchActionGroup(part, toolkit)
		});
	}

	public void handleOpen(OpenEvent event) {
		IAction openAction = fNavigateActionGroup.getOpenAction();
		if (openAction != null && openAction.isEnabled()) {
			openAction.run();
			return;
		}
	}
}
