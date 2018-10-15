/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.search.indexing.core;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.core.search.indexing.ReadWriteMonitor;

public class SourceModuleRemoveRequest extends IndexRequest {

	private final IScriptProject project;
	private final String path;

	/**
	 * @param project
	 * @param path
	 * @param name
	 */
	public SourceModuleRemoveRequest(IProjectIndexer indexer,
			IScriptProject project, String path) {
		super(indexer);
		this.project = project;
		this.path = path;
	}

	@Override
	protected String getName() {
		return path;
	}

	@Override
	public boolean belongsTo(String jobFamily) {
		return jobFamily.equals(project.getElementName());
	}

	@Override
	protected void run() throws CoreException, IOException {
		IEnvironment environment = EnvironmentManager.getEnvironment(project);
		if (environment == null || !environment.connect()) {
			return;
		}
		final Index index = getIndexer().getProjectIndex(project);
		final ReadWriteMonitor imon = index.monitor;
		imon.enterWrite();
		try {
			index.remove(path);
		} finally {
			imon.exitWrite();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		SourceModuleRemoveRequest other = (SourceModuleRemoveRequest) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}

}
