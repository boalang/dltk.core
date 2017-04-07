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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.internal.mylyn.DLTKUiBridgePlugin;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Mik Kersten
 */
public class ToggleActiveFoldingAction extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {

	private static ToggleActiveFoldingAction INSTANCE;

	private IAction parentAction = null;

	public ToggleActiveFoldingAction() {
		super();
		INSTANCE = this;
		setText(Messages.ToggleActiveFoldingAction_Active_folding);
		setImageDescriptor(TasksUiImages.CONTEXT_FOCUS);
	}

	public static void toggleFolding(boolean on) {
		if (INSTANCE.parentAction != null) {
			INSTANCE.valueChanged(INSTANCE.parentAction, on);
		}
	}

	@Override
	public void run(IAction action) {
		valueChanged(action, action.isChecked());
	}

	private void valueChanged(IAction action, final boolean on) {
		try {
			if (on) {
				DLTKUIPlugin.getDefault().getPreferenceStore().setValue(PreferenceConstants.EDITOR_FOLDING_ENABLED,
						true);
			}
			action.setChecked(on);
			DLTKUiBridgePlugin.getDefault().getPreferenceStore().setValue(DLTKUiBridgePlugin.AUTO_FOLDING_ENABLED, on);
		} catch (Throwable t) {
			StatusHandler.log(
					new Status(IStatus.ERROR, DLTKUiBridgePlugin.ID_PLUGIN, "Could not enable editor management", t)); //$NON-NLS-1$
		}
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		// don't care when the active editor changes
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// don't care when the selection changes
	}

	@Override
	public void init(IAction action) {
		this.parentAction = action;
		valueChanged(action, DLTKUiBridgePlugin.getDefault().getPreferenceStore().getBoolean(
				DLTKUiBridgePlugin.AUTO_FOLDING_ENABLED));
	}

	@Override
	public void dispose() {
		// don't need to do anything

	}

	@Override
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	@Override
	public void init(IWorkbenchWindow window) {
	}
}
