/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.typehierarchy;

import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;

/**
 * Action to let the label provider show the defining type of the method
 */
public class SortByDefiningTypeAction extends Action {

	private MethodsViewer fMethodsViewer;

	/**
	 * Creates the action.
	 */
	public SortByDefiningTypeAction(MethodsViewer viewer, boolean initValue) {
		super(TypeHierarchyMessages.SortByDefiningTypeAction_label);
		setDescription(TypeHierarchyMessages.SortByDefiningTypeAction_description);
		setToolTipText(TypeHierarchyMessages.SortByDefiningTypeAction_tooltip);

		DLTKPluginImages.setLocalImageDescriptors(this, "definingtype_sort_co.png"); //$NON-NLS-1$

		fMethodsViewer= viewer;

		//PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.SORT_BY_DEFINING_TYPE_ACTION);

		setChecked(initValue);
	}

	@Override
	public void run() {
		BusyIndicator.showWhile(fMethodsViewer.getControl().getDisplay(),
				() -> fMethodsViewer.sortByDefiningType(isChecked()));
	}
}
