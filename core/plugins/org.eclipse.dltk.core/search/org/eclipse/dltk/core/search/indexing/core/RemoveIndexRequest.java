/*******************************************************************************
 * Copyright (c) 2008, 2016 xored software, Inc.
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;

public class RemoveIndexRequest extends IndexRequest {

	private final IPath path;

	public RemoveIndexRequest(IProjectIndexer indexer, IPath path) {
		super(indexer);
		this.path = path;
	}

	@Override
	protected String getName() {
		return path.toString();
	}

	@Override
	protected void run() throws CoreException, IOException {
		getIndexer().getIndexManager().removeIndex(path);
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
		RemoveIndexRequest other = (RemoveIndexRequest) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}
}
