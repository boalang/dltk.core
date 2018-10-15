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
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.index2.IIndexer;
import org.eclipse.dltk.core.index2.ProjectIndexer2;

/**
 * Request to add source module to the index
 * 
 * @author michael
 * 
 */
public class AddSourceModuleRequest extends AbstractIndexRequest {

	protected final ISourceModule sourceModule;

	public AddSourceModuleRequest(ProjectIndexer2 indexer,
			ISourceModule sourceModule, ProgressJob progressJob) {
		super(indexer, progressJob);
		this.sourceModule = sourceModule;
	}

	@Override
	protected String getName() {
		return sourceModule.getElementName();
	}

	@Override
	protected void run() throws CoreException, IOException {
		IIndexer indexer = IndexerManager.getIndexer();
		if (indexer == null || isCancelled) {
			return;
		}
		reportToProgress(sourceModule);
		indexer.indexDocument(sourceModule);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((sourceModule == null) ? 0 : sourceModule.hashCode());
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
		AddSourceModuleRequest other = (AddSourceModuleRequest) obj;
		if (sourceModule == null) {
			if (other.sourceModule != null)
				return false;
		} else if (!sourceModule.equals(other.sourceModule))
			return false;
		return true;
	}
}
