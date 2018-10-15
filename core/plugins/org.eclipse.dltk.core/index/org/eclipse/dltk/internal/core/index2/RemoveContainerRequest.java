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
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.index2.IIndexer;
import org.eclipse.dltk.core.index2.ProjectIndexer2;
import org.eclipse.osgi.util.NLS;

/**
 * Request for removing container path from the index. All elements related to
 * the container path must be removed as well.
 * 
 * @author michael
 * 
 */
public class RemoveContainerRequest extends AbstractIndexRequest {

	private final IPath containerPath;

	public RemoveContainerRequest(ProjectIndexer2 indexer,
			IPath containerPath, ProgressJob progressJob) {
		super(indexer, progressJob);
		this.containerPath = containerPath;
	}

	@Override
	protected String getName() {
		return containerPath.toString();
	}

	@Override
	protected void run() throws CoreException, IOException {
		IIndexer indexer = IndexerManager.getIndexer();
		if (indexer == null || isCancelled) {
			return;
		}
		if (progressJob != null) {
			IPath path = containerPath;
			if (EnvironmentPathUtils.isFull(path)) {
				path = EnvironmentPathUtils.getLocalPath(path);
			}
			progressJob.subTask(NLS.bind("cleaning ''{0}''", path));
		}
		indexer.removeContainer(containerPath);
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
		RemoveContainerRequest other = (RemoveContainerRequest) obj;
		if (containerPath == null) {
			if (other.containerPath != null)
				return false;
		} else if (!containerPath.equals(other.containerPath))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((containerPath == null) ? 0 : containerPath.hashCode());
		return result;
	}
}
