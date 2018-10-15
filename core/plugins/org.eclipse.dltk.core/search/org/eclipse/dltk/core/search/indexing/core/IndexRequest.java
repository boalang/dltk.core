/*******************************************************************************
 * Copyright (c) 2008, 2017 xored software, Inc.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.caching.IContentCache;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.AbstractJob;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer.Internal;
import org.eclipse.dltk.core.search.indexing.ReadWriteMonitor;
import org.eclipse.dltk.internal.core.ModelManager;

/**
 * @since 2.0
 */
public abstract class IndexRequest extends AbstractJob {
	private IProjectIndexer indexer;

	public IndexRequest(IProjectIndexer indexer) {
		this.indexer = indexer;
	}

	public IProjectIndexer.Internal getIndexer() {
		return (Internal) indexer;
	}

	/**
	 * Returns all the document names. Read lock is acquired automatically.
	 *
	 * @param index
	 * @return
	 * @throws IOException
	 */
	protected String[] queryDocumentNames(final Index index)
			throws IOException {
		final ReadWriteMonitor monitor = index.monitor;
		monitor.enterRead();
		try {
			return index.queryDocumentNames(null);
		} finally {
			monitor.exitRead();
		}
	}

	protected Map<String, ISourceModule> collectSourceModulePaths(
			Collection<ISourceModule> modules, IPath containerPath) {
		final Map<String, ISourceModule> paths = new HashMap<>();
		for (Iterator<ISourceModule> i = modules.iterator(); i.hasNext();) {
			final ISourceModule module = i.next();
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
	 * @param parentFolders
	 *            - will be filled if cache folder timestamp update are
	 *            performed.
	 * @return
	 * @throws ModelException
	 * @throws IOException
	 */
	protected List<Object> checkChanges(Index index,
			Collection<ISourceModule> modules, IPath containerPath,
			IEnvironment environment, Set<IFileHandle> parentFolders)
			throws ModelException, IOException {
		IContentCache coreCache = ModelManager.getModelManager().getCoreCache();
		final String[] documents = queryDocumentNames(index);
		if (documents != null && documents.length != 0) {
			// final long indexLastModified =
			// index.getIndexFile().lastModified();
			final List<Object> changes = new ArrayList<>();
			final Map<String, ISourceModule> m = collectSourceModulePaths(
					modules, containerPath);
			if (DEBUG) {
				log("documents.length=" + documents.length); //$NON-NLS-1$
				log("modules.size=" + modules.size()); //$NON-NLS-1$
				log("map.size=" + m.size()); //$NON-NLS-1$
			}
			for (int i = 0; i < documents.length; ++i) {
				final String document = documents[i];
				final ISourceModule module = m.remove(document);
				if (module == null) {
					changes.add(document);
				} else if (environment != null) {
					// final IFileHandle handle = environment
					// .getFile(EnvironmentPathUtils.getLocalPath(module
					// .getPath()));
					IFileHandle handle = EnvironmentPathUtils.getFile(module,
							false);
					if (handle != null) {
						// Check content cache for file changes
						String indexed = coreCache.getCacheEntryAttributeString(
								handle, "indexed", true);
						if (indexed == null) {
							IFileHandle parent = handle.getParent();
							if (parentFolders.add(parent)
									&& documents.length > 1) {
								coreCache.updateFolderTimestamps(parent);
							}
							changes.add(module);
							coreCache.setCacheEntryAttribute(handle, "indexed",
									"");
						}
					}
				}
			}
			if (!m.isEmpty()) {
				changes.addAll(m.values());
			}
			return changes;
		} else {
			return new ArrayList<>(modules);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((indexer == null) ? 0 : indexer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexRequest other = (IndexRequest) obj;
		if (indexer == null) {
			if (other.indexer != null)
				return false;
		} else if (!indexer.equals(other.indexer))
			return false;
		return true;
	}
}
