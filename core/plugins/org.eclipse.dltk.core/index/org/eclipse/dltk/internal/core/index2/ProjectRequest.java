/*******************************************************************************
 * Copyright (c) 2009, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.internal.core.index2;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.index2.ProjectIndexer2;
import org.eclipse.dltk.internal.core.BuiltinProjectFragment;

/**
 * Request for indexing project
 *
 * @author michael
 *
 */
public class ProjectRequest extends AbstractIndexRequest {

	private final IScriptProject project;

	public ProjectRequest(ProjectIndexer2 indexer, IScriptProject project,
			ProgressJob progressJob) {
		super(indexer, progressJob);
		this.project = project;
	}

	@Override
	protected String getName() {
		return project.getElementName();
	}

	@Override
	protected void run() throws CoreException {
		final IProjectFragment[] fragments = project.getAllProjectFragments();

		final Set<ISourceModule> sourceModules = new HashSet<>();
		for (final IProjectFragment fragment : fragments) {
			if (isCancelled) {
				return;
			}
			if (fragment instanceof BuiltinProjectFragment) {
				projectIndexer.request(new BuiltinProjectFragmentRequest(
						projectIndexer, fragment, progressJob));
			} else if (fragment.isExternal()) {
				projectIndexer.request(new ExternalProjectFragmentRequest(
						projectIndexer, fragment, progressJob));
			} else if (project.equals(fragment.getScriptProject())) {
				getSourceModules(fragment, sourceModules);
			}
		}

		projectIndexer.request(new SourceModulesRequest(projectIndexer,
				project.getPath(), sourceModules, progressJob));
	}

	private void getSourceModules(IProjectFragment fragment,
			final Set<ISourceModule> sourceModules) throws ModelException {
		IModelElementVisitor visitor = element -> {
			if (element.getElementType() == IModelElement.SOURCE_MODULE) {
				sourceModules.add((ISourceModule) element);
				return false;
			}
			return true;
		};
		fragment.accept(visitor);
	}

	@Override
	public boolean belongsTo(String jobFamily) {
		return jobFamily.equals(project.getProject().getName());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectRequest other = (ProjectRequest) obj;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}
}
