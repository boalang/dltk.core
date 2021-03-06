/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.workingsets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionGroup;

public class WorkingSetShowActionGroup extends ActionGroup implements IWorkingSetActionGroup {

	private List fContributions= new ArrayList();
	private ConfigureWorkingSetAction fConfigureWorkingSetAction;
	private WorkingSetModel fWorkingSetModel;
	private final IWorkbenchPartSite fSite;

	public WorkingSetShowActionGroup(IWorkbenchPartSite site) {
		fSite= site;
	}

	public void setWorkingSetMode(WorkingSetModel model) {
		fWorkingSetModel= model;
		if (fConfigureWorkingSetAction != null)
			fConfigureWorkingSetAction.setWorkingSetModel(fWorkingSetModel);
	}

	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		IMenuManager menuManager= actionBars.getMenuManager();
		fillViewMenu(menuManager);
	}

	@Override
	public void fillViewMenu(IMenuManager menuManager) {
		fConfigureWorkingSetAction=  new ConfigureWorkingSetAction(fSite);
		if (fWorkingSetModel != null)
			fConfigureWorkingSetAction.setWorkingSetModel(fWorkingSetModel);
		addAction(menuManager, fConfigureWorkingSetAction);
	}

	@Override
	public void cleanViewMenu(IMenuManager menuManager) {
		for (Iterator iter= fContributions.iterator(); iter.hasNext();) {
			menuManager.remove((IContributionItem)iter.next());
		}
		fContributions.clear();
	}

	private void addAction(IMenuManager menuManager, Action action) {
		IContributionItem item= new ActionContributionItem(action);
		menuManager.appendToGroup(ACTION_GROUP, item);
		fContributions.add(item);
	}
}
