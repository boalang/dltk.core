/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.refactoring.reorg;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.internal.ui.model.DLTKElementResourceMapping;
import org.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;



public class MonitoringNewNameQueries implements INewNameQueries {
	private INewNameQueries fDelegate;
	private ReorgExecutionLog fExecutionLog;
	public MonitoringNewNameQueries(INewNameQueries delegate, ReorgExecutionLog log) {
		fDelegate= delegate;
		fExecutionLog= log;
	}

	@Override
	public INewNameQuery createNewSourceModuleNameQuery(final ISourceModule cu, final String initialSuggestedName) {
		return () -> {
			String result = fDelegate
					.createNewSourceModuleNameQuery(cu, initialSuggestedName)
					.getNewName();
			String newName = ScriptModelUtil.getRenamedCUName(cu, result);
			fExecutionLog.setNewName(cu, newName);
			ResourceMapping mapping = DLTKElementResourceMapping.create(cu);
			if (mapping != null) {
				fExecutionLog.setNewName(mapping, newName);
			}
			return result;
		};
	}

	@Override
	public INewNameQuery createNewProjectFragmentNameQuery(final IProjectFragment root, final String initialSuggestedName) {
		return () -> {
			String result = fDelegate.createNewProjectFragmentNameQuery(root,
					initialSuggestedName).getNewName();
			fExecutionLog.setNewName(root, result);
			ResourceMapping mapping = DLTKElementResourceMapping.create(root);
			if (mapping != null) {
				fExecutionLog.setNewName(mapping, result);
			}
			return result;
		};
	}

	@Override
	public INewNameQuery createNewPackageNameQuery(final IScriptFolder pack, final String initialSuggestedName) {
		return () -> {
			String result = fDelegate
					.createNewPackageNameQuery(pack, initialSuggestedName)
					.getNewName();
			fExecutionLog.setNewName(pack, result);
			ResourceMapping mapping = DLTKElementResourceMapping.create(pack);
			if (mapping != null) {
				int index = result.lastIndexOf('.');
				String newFolderName = index == -1 ? result
						: result.substring(index + 1);
				fExecutionLog.setNewName(mapping, newFolderName);
			}
			return result;
		};
	}

	@Override
	public INewNameQuery createNewResourceNameQuery(final IResource res,
			final String initialSuggestedName) {
		return () -> {
			String result = fDelegate
					.createNewResourceNameQuery(res, initialSuggestedName)
					.getNewName();
			fExecutionLog.setNewName(res, result);
			return result;
		};
	}

	@Override
	public INewNameQuery createNullQuery() {
		return fDelegate.createNullQuery();
	}

	@Override
	public INewNameQuery createStaticQuery(String newName) {
		return fDelegate.createStaticQuery(newName);
	}
}
