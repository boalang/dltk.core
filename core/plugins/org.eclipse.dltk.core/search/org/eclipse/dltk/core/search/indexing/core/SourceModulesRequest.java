/*******************************************************************************
 * Copyright (c) 2008, 2017 xored software, Inc. and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.search.indexing.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.core.search.indexing.ReadWriteMonitor;

/**
 * @since 2.0
 */
public class SourceModulesRequest extends IndexRequest {

	private final IScriptProject project;
	private final IDLTKLanguageToolkit toolkit;
	private final Set<ISourceModule> modules;

	/**
	 * @param project
	 * @param modules
	 */
	public SourceModulesRequest(IProjectIndexer indexer, IScriptProject project,
			IDLTKLanguageToolkit toolkit, Set<ISourceModule> modules) {
		super(indexer);
		this.project = project;
		this.toolkit = toolkit;
		this.modules = modules;
	}

	@Override
	protected String getName() {
		return project.getElementName();
	}

	@Override
	protected void run() throws CoreException, IOException {
		IEnvironment environment = EnvironmentManager.getEnvironment(project);
		if (environment == null || !environment.connect()) {
			return;
		}
		final Index index = getIndexer().getProjectIndex(project);
		if (index == null) {
			DLTKCore.error("Index are null for:" + this.modules);
			return;
		}
		final IPath containerPath = project.getPath();
		Set<IFileHandle> parentFolders = new HashSet<>();
		final List<?> changes = checkChanges(index, modules, containerPath,
				EnvironmentManager.getEnvironment(project), parentFolders);
		if (DEBUG) {
			log("changes.size=" + changes.size()); //$NON-NLS-1$
		}
		if (changes.isEmpty()) {
			return;
		}
		final ReadWriteMonitor imon = index.monitor;
		imon.enterWrite();
		try {
			for (Iterator<?> i = changes.iterator(); !isCancelled
					&& i.hasNext();) {
				final Object change = i.next();
				if (change instanceof String) {
					index.remove((String) change);
				} else {
					getIndexer().indexSourceModule(index, toolkit,
							(ISourceModule) change, containerPath);
				}
			}
		} catch (Throwable t) {
			if (DLTKCore.DEBUG) {
				t.printStackTrace();
			}
		} finally {
			try {
				index.save();
			} catch (IOException e) {
				DLTKCore.error("error saving index", e); //$NON-NLS-1$
			} finally {
				imon.exitWrite();
			}
		}
	}

	@Override
	public boolean belongsTo(String jobFamily) {
		return jobFamily.equals(project.getProject().getName());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((modules == null) ? 0 : modules.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceModulesRequest other = (SourceModulesRequest) obj;
		if (modules == null) {
			if (other.modules != null)
				return false;
		} else if (!modules.equals(other.modules))
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}
}
