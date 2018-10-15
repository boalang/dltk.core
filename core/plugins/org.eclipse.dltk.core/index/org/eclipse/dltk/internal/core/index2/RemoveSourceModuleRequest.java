/*******************************************************************************
 * Copyright (c) 2009, 2016 IBM Corporation and others.
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

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.index2.IIndexer;
import org.eclipse.dltk.core.index2.ProjectIndexer2;

/**
 * Request for removing source module from the index. All elements related to
 * the source module must be removed as well.
 * 
 * @author michael
 * 
 */
public class RemoveSourceModuleRequest extends AbstractIndexRequest {

	private final IPath containerPath;
	private final String relativePath;

	public RemoveSourceModuleRequest(ProjectIndexer2 indexer,
			IPath containerPath, String relativePath, ProgressJob progressJob) {
		super(indexer, progressJob);
		this.containerPath = containerPath;
		this.relativePath = relativePath;
	}

	@Override
	protected String getName() {
		return containerPath.append(relativePath).toString();
	}

	@Override
	protected void run() throws CoreException, IOException {
		IIndexer indexer = IndexerManager.getIndexer();
		if (indexer == null || isCancelled) {
			return;
		}
		indexer.removeDocument(containerPath, relativePath);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((containerPath == null) ? 0 : containerPath.hashCode());
		result = prime * result
				+ ((relativePath == null) ? 0 : relativePath.hashCode());
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
		RemoveSourceModuleRequest other = (RemoveSourceModuleRequest) obj;
		if (containerPath == null) {
			if (other.containerPath != null)
				return false;
		} else if (!containerPath.equals(other.containerPath))
			return false;
		if (relativePath == null) {
			if (other.relativePath != null)
				return false;
		} else if (!relativePath.equals(other.relativePath))
			return false;
		return true;
	}
}
