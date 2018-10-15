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
package org.eclipse.dltk.internal.core.mixin;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.mixin.MixinModelRegistry;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IndexDocument;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.core.search.indexing.core.AbstractProjectIndexer;
import org.eclipse.dltk.core.search.indexing.core.ReconcileSourceModuleRequest;
import org.eclipse.dltk.core.search.indexing.core.RemoveIndexRequest;

public class MixinProjectIndexer extends AbstractProjectIndexer {

	@Override
	public void doIndexing(IndexDocument document) {
		new MixinIndexer(document).indexDocument();
		MixinModelRegistry.clearKeysCache(DLTKLanguageManager
				.getLanguageToolkit(document.getSourceModule()));
	}

	@Override
	public Index getProjectIndex(IScriptProject project) {
		final String path = project.getProject().getFullPath().toString();
		return getIndexManager().getSpecialIndex(IndexManager.SPECIAL_MIXIN,
				path, path);
	}

	@Override
	public Index getProjectFragmentIndex(IProjectFragment fragment) {
		final String path = fragment.getPath().toString();
		return getIndexManager().getSpecialIndex(IndexManager.SPECIAL_MIXIN,
				path, path);
	}

	@Override
	public void removeProject(IPath projectPath) {
		requestIfNotWaiting(new RemoveIndexRequest(this, new Path(
				IndexManager.SPECIAL_MIXIN + projectPath.toString())));
	}

	@Override
	public void removeLibrary(IScriptProject project, IPath path) {
		requestIfNotWaiting(new RemoveIndexRequest(this, new Path(
				IndexManager.SPECIAL_MIXIN + path.toString())));
	}

	@Override
	public void reconciled(ISourceModule workingCopy,
			IDLTKLanguageToolkit toolkit) {
		request(new ReconcileSourceModuleRequest(this, workingCopy, toolkit));
	}
}
