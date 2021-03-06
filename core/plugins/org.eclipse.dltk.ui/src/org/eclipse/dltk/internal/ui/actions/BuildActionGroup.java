/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.actions;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.ui.IContextMenuConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.BuildAction;
import org.eclipse.ui.ide.IDEActionFactory;

/**
 * Contributes all build related actions to the context menu and installs
 * handlers for the corresponding global menu actions.
 *
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 *
 */
public class BuildActionGroup extends ActionGroup {

	private IWorkbenchSite fSite;

	private BuildAction fBuildAction;
	private RefreshAction fRefreshAction;

	/**
	 * Creates a new <code>BuildActionGroup</code>. The group requires that the
	 * selection provided by the view part's selection provider is of type
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 *
	 * @param part
	 *            the view part that owns this action group
	 */
	public BuildActionGroup(IViewPart part) {
		fSite = part.getSite();
		ISelectionProvider provider = fSite.getSelectionProvider();

		fBuildAction = new BuildAction(fSite,
				IncrementalProjectBuilder.INCREMENTAL_BUILD);
		fBuildAction.setText(ActionMessages.BuildAction_label);
		fBuildAction
				.setActionDefinitionId("org.eclipse.ui.project.buildProject"); //$NON-NLS-1$

		fRefreshAction = new RefreshAction(fSite);
		fRefreshAction.setActionDefinitionId("org.eclipse.ui.file.refresh"); //$NON-NLS-1$

		provider.addSelectionChangedListener(fBuildAction);
		provider.addSelectionChangedListener(fRefreshAction);
	}

	/**
	 * Returns the refresh action managed by this group.
	 *
	 * @return the refresh action. If this group doesn't manage a refresh action
	 *         <code>null</code> is returned
	 */
	public IAction getRefreshAction() {
		return fRefreshAction;
	}

	@Override
	public void fillActionBars(IActionBars actionBar) {
		super.fillActionBars(actionBar);
		setGlobalActionHandlers(actionBar);
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		ISelection selection = getContext().getSelection();
		if (!ResourcesPlugin.getWorkspace().isAutoBuilding()
				&& isBuildTarget(selection)) {
			appendToGroup(menu, fBuildAction);
		}
		appendToGroup(menu, fRefreshAction);
		super.fillContextMenu(menu);
	}

	@Override
	public void dispose() {
		ISelectionProvider provider = fSite.getSelectionProvider();
		provider.removeSelectionChangedListener(fBuildAction);
		provider.removeSelectionChangedListener(fRefreshAction);
		super.dispose();
	}

	private void setGlobalActionHandlers(IActionBars actionBar) {
		actionBar.setGlobalActionHandler(IDEActionFactory.BUILD_PROJECT.getId(),
				fBuildAction);
		actionBar.setGlobalActionHandler(ActionFactory.REFRESH.getId(),
				fRefreshAction);
	}

	private void appendToGroup(IMenuManager menu, IAction action) {
		if (action.isEnabled())
			menu.appendToGroup(IContextMenuConstants.GROUP_BUILD, action);
	}

	private boolean isBuildTarget(ISelection s) {
		if (!(s instanceof IStructuredSelection))
			return false;
		IStructuredSelection selection = (IStructuredSelection) s;
		if (selection.size() != 1)
			return false;
		return selection.getFirstElement() instanceof IScriptProject;
	}
}
