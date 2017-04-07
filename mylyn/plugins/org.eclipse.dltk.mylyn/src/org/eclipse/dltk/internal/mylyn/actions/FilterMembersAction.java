/*******************************************************************************
 * Copyright (c) 2004, 2017 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.internal.mylyn.actions;

import org.eclipse.dltk.internal.mylyn.DLTKDeclarationsFilter;
import org.eclipse.dltk.internal.mylyn.DLTKUiBridgePlugin;
import org.eclipse.dltk.internal.ui.scriptview.ScriptExplorerPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class FilterMembersAction extends Action implements IViewActionDelegate {

	public static final String PREF_ID = "org.eclipse.dltk.mylyn.explorer.filter.members"; //$NON-NLS-1$

	public FilterMembersAction() {
		super();
		// setChecked(true);
		// try {
		// boolean checked=
		// ContextCore.getPreferenceStore().getBoolean(PREF_ID);
		// valueChanged(true, true);
		// } catch (Exception e) {
		//
		// }
	}

	@Override
	public void run(IAction action) {
		valueChanged(isChecked(), true);

	}

	private void valueChanged(final boolean on, boolean store) {
		if (store) {
			DLTKUiBridgePlugin.getDefault().getPreferenceStore().setValue(PREF_ID, on);
		}

		setChecked(true);
		ScriptExplorerPart packageExplorer = ScriptExplorerPart.getFromActivePerspective();
		ViewerFilter existingFilter = null;
		for (int i = 0; i < packageExplorer.getTreeViewer().getFilters().length; i++) {
			ViewerFilter filter = packageExplorer.getTreeViewer().getFilters()[i];
			if (filter instanceof DLTKDeclarationsFilter) {
				existingFilter = filter;
			}
		}
		if (existingFilter != null) {
			packageExplorer.getTreeViewer().removeFilter(existingFilter);
		} else {
			packageExplorer.getTreeViewer().addFilter(new DLTKDeclarationsFilter());
		}
	}

	@Override
	public void init(IViewPart view) {
		// don't need to do anything on init
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// don't care when the selection changes
	}

}
