/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.AbstractJob;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.core.search.indexing.ReadWriteMonitor;

abstract class IndexRequest extends AbstractJob {
	private IProjectIndexer indexer;

	public IndexRequest(IProjectIndexer indexer) {
		this.indexer = indexer;
	}

	public IProjectIndexer getIndexer() {
		return indexer;
	}

	/**
	 * Returns the document names that starts with the given substring, if
	 * <code>null</code> then returns all of them. Read lock is acquired
	 * automatically.
	 * 
	 * @param index
	 * @return
	 * @throws IOException
	 */
	protected String[] queryDocumentNames(final Index index) throws IOException {
		final ReadWriteMonitor monitor = index.monitor;
		monitor.enterRead();
		try {
			return index.queryDocumentNames(null);
		} finally {
			monitor.exitRead();
		}
	}

	protected Map collectSourceModulePaths(Collection modules,
			IPath containerPath) {
		final Map paths = new HashMap();
		for (Iterator i = modules.iterator(); i.hasNext();) {
			final ISourceModule module = (ISourceModule) i.next();
			paths.put(SourceIndexUtil.containerRelativePath(containerPath,
					module), module);
		}
		return paths;
	}

	/**
	 * Check changes of the specified modules compared to the index. Returns the
	 * {@link List} of changes. List items could be {@link String} if that
	 * document should be removed from the index or ISourceModule if that module
	 * should be indexed. If there are no changes the empty {@link List} is
	 * returned. If <code>environment</code> is specified then it is used to
	 * retrieve the modification time of the files to compare with the
	 * modification time of the index.
	 * 
	 * @param index
	 * @param modules
	 * @param containerPath
	 * @param environment
	 *            could be <code>null</code>
	 * @return
	 * @throws ModelException
	 * @throws IOException
	 */
	protected List checkChanges(Index index, Collection modules,
			IPath containerPath, IEnvironment environment)
			throws ModelException, IOException {
		final String[] documents = queryDocumentNames(index);
		if (documents != null && documents.length != 0) {
			final long indexLastModified = index.getIndexFile().lastModified();
			final List changes = new ArrayList();
			final Map m = collectSourceModulePaths(modules, containerPath);
			if (DEBUG) {
				log("documents.length=" + documents.length); //$NON-NLS-1$
				log("modules.size=" + modules.size()); //$NON-NLS-1$
				log("map.size=" + m.size()); //$NON-NLS-1$
			}
			for (int i = 0; i < documents.length; ++i) {
				final String document = documents[i];
				final ISourceModule module = (ISourceModule) m.remove(document);
				if (module == null) {
					changes.add(document);
				} else if (environment != null) {
					final IFileHandle handle = environment
							.getFile(EnvironmentPathUtils.getLocalPath(module
									.getPath()));
					if (handle != null
							&& handle.lastModified() > indexLastModified) {
						changes.add(module);
					}
				}
			}
			if (!m.isEmpty()) {
				changes.addAll(m.values());
			}
			return changes;
		} else {
			return new ArrayList(modules);
		}
	}

}