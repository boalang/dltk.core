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

import org.eclipse.dltk.internal.ui.scriptview.ScriptExplorerPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.context.ui.BrowseFilteredListener;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Mik Kersten
 */
@Deprecated
public class ShowFilteredChildrenAction extends Action implements IObjectActionDelegate, IViewActionDelegate {

	private BrowseFilteredListener browseFilteredListener;

	private TreeViewer treeViewer;

	private IStructuredSelection selection;

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		if (targetPart instanceof ScriptExplorerPart) {
			treeViewer = ((ScriptExplorerPart) targetPart).getTreeViewer();
			browseFilteredListener = new BrowseFilteredListener(treeViewer);
		}
	}

	@Override
	public void init(IViewPart targetPart) {
		if (targetPart instanceof ScriptExplorerPart) {
			treeViewer = ((ScriptExplorerPart) targetPart).getTreeViewer();
			browseFilteredListener = new BrowseFilteredListener(treeViewer);
		}
	}

	@Override
	public void run(IAction action) {
		if (selection != null) {
			browseFilteredListener.unfilterSelection(treeViewer, selection);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}
}
