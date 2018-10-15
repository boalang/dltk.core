/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.core.search.indexing;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.internal.core.search.processing.IJob;


public abstract class IndexRequest implements IJob {
	protected boolean isCancelled = false;
	protected IPath containerPath;
	protected IndexManager manager;

	public IndexRequest(IPath containerPath, IndexManager manager) {
		this.containerPath = containerPath;
		this.manager = manager;
	}
	@Override
	public boolean belongsTo(String projectNameOrArchivePath) {
		// used to remove pending jobs because the project was deleted... not to delete index files
		// can be found either by project name or  path name
		return projectNameOrArchivePath.equals(this.containerPath.segment(0))
			|| projectNameOrArchivePath.equals(this.containerPath.toString());
	}
	@Override
	public void cancel() {
		this.manager.jobWasCancelled(this.containerPath);
		this.isCancelled = true;
	}
	@Override
	public void ensureReadyToRun() {
		// tag the index as inconsistent
		this.manager.aboutToUpdateIndex(this.containerPath, updatedIndexState());
	}
	protected Integer updatedIndexState() {
		return IndexManager.UPDATING_STATE;
	}
}
