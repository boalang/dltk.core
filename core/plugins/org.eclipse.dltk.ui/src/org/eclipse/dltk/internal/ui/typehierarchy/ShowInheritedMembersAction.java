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
 * Action to show / hide inherited members in the method view
 * Depending in the action state a different label provider is installed in the viewer
 */
public class ShowInheritedMembersAction extends Action {

	private MethodsViewer fMethodsViewer;

	/**
	 * Creates the action.
	 */
	public ShowInheritedMembersAction(MethodsViewer viewer, boolean initValue) {
		super(TypeHierarchyMessages.ShowInheritedMembersAction_label);
		setDescription(TypeHierarchyMessages.ShowInheritedMembersAction_description);
		setToolTipText(TypeHierarchyMessages.ShowInheritedMembersAction_tooltip);

		DLTKPluginImages.setLocalImageDescriptors(this, "inher_co.png"); //$NON-NLS-1$

		fMethodsViewer= viewer;

		//PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.SHOW_INHERITED_ACTION);

		setChecked(initValue);
	}

	@Override
	public void run() {
		BusyIndicator.showWhile(fMethodsViewer.getControl().getDisplay(),
				() -> fMethodsViewer.showInheritedMethods(isChecked()));
	}
}
