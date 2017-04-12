/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.ISearchableEnvironment;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.core.search.BasicSearchEngine;
import org.eclipse.dltk.core.search.IDLTKSearchScope;

/**
 * This class provides a <code>SearchableBuilderEnvironment</code> for code
 * assist which uses the Script model as a search tool.
 */
public class SearchableEnvironment implements ISearchableEnvironment {
	public NameLookup nameLookup;

	protected IModuleSource unitToSkip;

	protected org.eclipse.dltk.core.ISourceModule[] workingCopies;

	protected ScriptProject project;

	protected IDLTKSearchScope searchScope;

	protected boolean checkAccessRestrictions;

	/**
	 * Creates a SearchableEnvironment on the given project
	 */
	public SearchableEnvironment(ScriptProject project,
			org.eclipse.dltk.core.ISourceModule[] workingCopies)
			throws ModelException {

		this.project = project;
		this.checkAccessRestrictions = !DLTKCore.IGNORE.equals(project
				.getOption(DLTKCore.COMPILER_PB_FORBIDDEN_REFERENCE, true))
				|| !DLTKCore.IGNORE.equals(project.getOption(
						DLTKCore.COMPILER_PB_DISCOURAGED_REFERENCE, true));

		this.workingCopies = workingCopies;

		this.nameLookup = project.newNameLookup(workingCopies);

		// Create search scope with visible entry on the project's buildpath
		if (this.checkAccessRestrictions) {
			this.searchScope = BasicSearchEngine.createSearchScope(project);
		} else {
			this.searchScope = BasicSearchEngine.createSearchScope(
					this.nameLookup.projectFragments,
					DLTKLanguageManager.getLanguageToolkit(project));
		}
	}

	/**
	 * Creates a SearchableEnvironment on the given project
	 */
	public SearchableEnvironment(ScriptProject project, WorkingCopyOwner owner)
			throws ModelException {
		this(project, owner == null ? null
				: ModelManager.getModelManager().getWorkingCopies(owner, true)); // add
																					// primary
																					// WCs
	}

	@Override
	public void cleanup() {
		// nothing to do
	}

	@Override
	public NameLookup getNameLookup() {
		return this.nameLookup;
	}
}
