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
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.core.search.indexing.ReadWriteMonitor;

public class SourceModuleRequest extends IndexRequest {

	protected final ISourceModule module;
	protected final IDLTKLanguageToolkit toolkit;

	public SourceModuleRequest(IProjectIndexer indexer, ISourceModule module,
			IDLTKLanguageToolkit toolkit) {
		super(indexer);
		this.module = module;
		this.toolkit = toolkit;
	}

	@Override
	protected String getName() {
		return module.getElementName();
	}

	@Override
	public boolean belongsTo(String jobFamily) {
		return jobFamily.equals(module.getScriptProject().getElementName());
	}

	@Override
	protected void run() throws CoreException, IOException {
		IEnvironment environment = EnvironmentManager.getEnvironment(module
				.getScriptProject());

		if (environment == null || !environment.connect()) {
			return;
		}
		final IScriptProject project = module.getScriptProject();
		final Index index = getIndexer().getProjectIndex(project);
		final ReadWriteMonitor imon = index.monitor;
		imon.enterWrite();
		try {
			getIndexer().indexSourceModule(index, toolkit, module,
					project.getPath());
		} finally {
			imon.exitWrite();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((module == null) ? 0 : module.hashCode());
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
		SourceModuleRequest other = (SourceModuleRequest) obj;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		return true;
	}
}
