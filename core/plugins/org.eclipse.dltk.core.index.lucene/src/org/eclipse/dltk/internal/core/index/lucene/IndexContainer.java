/*******************************************************************************
 * Copyright (c) 2016 Zend Technologies and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Zend Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.core.index.lucene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SleepingLockWrapper;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.core.index.lucene.LucenePlugin;

/**
 * Class responsible for handling container index data.
 * 
 * @author Bartlomiej Laczkowski
 */
class IndexContainer {

	private final class IndexCleaner extends Job {

		public IndexCleaner() {
			super(""); //$NON-NLS-1$
			setUser(false);
			setSystem(true);
		}

		@Override
		public boolean belongsTo(Object family) {
			return family == LucenePlugin.LUCENE_JOB_FAMILY;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			doClean();
			return Status.OK_STATUS;
		}

		void clean(boolean fork) {
			if (fork) {
				schedule();
			} else {
				doClean();
			}
		}

		private void doClean() {
			close();
			Path containerPath = Paths.get(fIndexRoot, getId());
			try {
				Utils.delete(containerPath);
			} catch (IOException e) {
				Logger.logException(e);
			}
		}

	}

	private static final String TIMESTAMPS_DIR = "timestamps"; //$NON-NLS-1$
	private static final long WRITE_LOCK_TIMEOUT = 3000;

	private final String fIndexRoot;
	private final String fContainerId;
	private IndexWriter fTimestampsWriter;
	private SearcherManager fTimestampsSearcher;
	private Map<IndexType, Map<Integer, IndexWriter>> fIndexWriters;
	private Map<IndexType, Map<Integer, SearcherManager>> fIndexSearchers;

	public IndexContainer(String indexRoot, String containerId) {
		fIndexRoot = indexRoot;
		fContainerId = containerId;
		initialize();
	}

	private void initialize() {
		fIndexWriters = new HashMap<>();
		fIndexWriters.put(IndexType.DECLARATIONS,
				new HashMap<Integer, IndexWriter>());
		fIndexWriters.put(IndexType.REFERENCES,
				new HashMap<Integer, IndexWriter>());
		fIndexSearchers = new HashMap<>();
		fIndexSearchers.put(IndexType.DECLARATIONS,
				new HashMap<Integer, SearcherManager>());
		fIndexSearchers.put(IndexType.REFERENCES,
				new HashMap<Integer, SearcherManager>());
	}

	private void purgeLocks(Path path) {
		/*
		 * Checks if any write locks exist (might be not removed if JVM crashed
		 * or was terminated abnormally) and simply deletes them.
		 */
		Path writeLockPath = path.resolve(IndexWriter.WRITE_LOCK_NAME);
		if (writeLockPath.toFile().exists()) {
			try {
				Files.delete(writeLockPath);
			} catch (IOException e) {
				Logger.logException(e);
			}
		}
	}

	private IndexWriter createWriter(Path path) throws IOException {

		Directory indexDir = FSDirectory.open(path);
		purgeLocks(path);
		IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());
		ConcurrentMergeScheduler mergeScheduler = new ConcurrentMergeScheduler();
		mergeScheduler.setDefaultMaxMergesAndThreads(true);
		config.setMergeScheduler(mergeScheduler);
		config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		config.setCommitOnClose(false);
		return new IndexWriter(
				new SleepingLockWrapper(indexDir, WRITE_LOCK_TIMEOUT), config);
	}

	private IndexWriter getWriter(Path path) {
		IndexWriter indexWriter = null;
		try {
			indexWriter = createWriter(path);
		} catch (IOException e) {
			// Try to recover possibly corrupted index
			IndexRecovery.tryRecover(this, path, e);
			try {
				indexWriter = createWriter(path);
			} catch (IOException ex) {
				Logger.logException(ex);
			}
		}
		return indexWriter;
	}

	public final String getId() {
		return fContainerId;
	}

	public synchronized IndexWriter getTimestampsWriter() {
		if (fTimestampsWriter == null) {
			Path writerPath = Paths.get(fIndexRoot, fContainerId,
					TIMESTAMPS_DIR);
			fTimestampsWriter = getWriter(writerPath);
		}
		return fTimestampsWriter;
	}

	public synchronized SearcherManager getTimestampsSearcher() {
		try {
			if (fTimestampsSearcher == null) {
				fTimestampsSearcher = new SearcherManager(getTimestampsWriter(),
						true, false, new SearcherFactory());
			}
			// Try to achieve the up-to-date index state
			fTimestampsSearcher.maybeRefresh();
		} catch (IOException e) {
			Logger.logException(e);
		}
		return fTimestampsSearcher;
	}

	private Path getPath(IndexType dataType, int elementType) {
		return Paths.get(fIndexRoot, fContainerId, dataType.getDirectory(),
				String.valueOf(elementType));
	}

	public synchronized IndexWriter getIndexWriter(IndexType dataType,
			int elementType) {
		IndexWriter writer = fIndexWriters.get(dataType).get(elementType);
		if (writer == null) {
			Path writerPath = getPath(dataType, elementType);
			writer = getWriter(writerPath);
			fIndexWriters.get(dataType).put(elementType, writer);
			fIndexSearchers.get(dataType).put(elementType, null);
		}
		return writer;
	}

	public synchronized SearcherManager getIndexSearcher(IndexType dataType,
			int elementType) {

		SearcherManager searcher = fIndexSearchers.get(dataType)
				.get(elementType);
		try {
			if (searcher != null) {
				try {
					searcher.maybeRefresh();
				} catch (AlreadyClosedException closed) {
					searcher = null;
				}
			}
			if (searcher == null) {
				searcher = new SearcherManager(
						FSDirectory.open(getPath(dataType, elementType)),
						new SearcherFactory());
				fIndexSearchers.get(dataType).put(elementType, searcher);
			}

		} catch (IndexNotFoundException e) {
			return null;
		} catch (IOException e) {
			Logger.logException(e);
		}
		return searcher;
	}

	public synchronized void delete(String sourceModule) {
		Term term = new Term(IndexFields.F_PATH, sourceModule);
		try {
			// Cleanup related time stamp
			getTimestampsWriter().deleteDocuments(term);
			// Cleanup all related documents in data writers
			for (Map<Integer, IndexWriter> dataWriters : fIndexWriters
					.values()) {
				for (IndexWriter writer : dataWriters.values()) {
					writer.deleteDocuments(term);
				}
			}
		} catch (IOException e) {
			Logger.logException(e);
		}
	}

	public synchronized void delete(boolean wait) {
		// Delete container entry entirely
		(new IndexCleaner()).clean(!wait);
	}

	public synchronized void close() {
		try {
			// Close time stamps searcher & writer
			if (fTimestampsSearcher != null)
				fTimestampsSearcher.close();
			if (fTimestampsWriter != null)
				fTimestampsWriter.close();
			// Close all data searchers
			for (Map<Integer, SearcherManager> dataSearchers : fIndexSearchers
					.values()) {
				for (SearcherManager searcher : dataSearchers.values()) {
					if (searcher != null)
						searcher.close();
				}
			}
			// Close all data writers
			for (Map<Integer, IndexWriter> dataWriters : fIndexWriters
					.values()) {
				for (IndexWriter writer : dataWriters.values()) {
					if (writer != null && writer.isOpen())
						writer.close();
				}
			}
		} catch (IOException e) {
			Logger.logException(e);
		}
	}

	synchronized boolean hasChanges() {
		for (Map<Integer, IndexWriter> dataWriters : fIndexWriters.values()) {
			for (IndexWriter writer : dataWriters.values()) {
				if (writer != null && writer.hasUncommittedChanges()) {
					return true;
				}
			}
			if (fTimestampsWriter != null) {
				return fTimestampsWriter.hasUncommittedChanges();
			}
		}
		return false;
	}

	synchronized void commit(IProgressMonitor monitor, boolean mergeDeletes) {
		int ticks = 1;
		for (Map<?, ?> dataWriters : fIndexWriters.values()) {
			ticks += dataWriters.size() * 2;
		}
		SubMonitor subMonitor = SubMonitor.convert(monitor, ticks);
		try {
			for (Entry<IndexType, Map<Integer, IndexWriter>> entry : fIndexWriters
					.entrySet()) {
				Map<Integer, IndexWriter> dataWriters = entry.getValue();
				Map<Integer, SearcherManager> dataSearchers = fIndexSearchers
						.get(entry.getKey());
				for (Entry<Integer, IndexWriter> writerEntry : dataWriters
						.entrySet()) {
					IndexWriter writer = writerEntry.getValue();
					if (writer != null && !subMonitor.isCanceled()) {
						writer.forceMergeDeletes(mergeDeletes);
						writer.commit();
						subMonitor.worked(1);
						if (dataSearchers != null) {
							SearcherManager m = dataSearchers
									.get(writerEntry.getKey());
							if (m != null) {
								try {
									m.maybeRefreshBlocking();
								} catch (IOException e) {
									dataSearchers.put(writerEntry.getKey(),
											null);
								}
							}
						}
						subMonitor.worked(1);
					}
				}
			}
			if (fTimestampsWriter != null && !subMonitor.isCanceled()) {
				fTimestampsWriter.forceMergeDeletes(mergeDeletes);
				fTimestampsWriter.commit();
				subMonitor.worked(1);
			}
			subMonitor.done();
		} catch (IOException e) {
			Logger.logException(e);
		}
	}

}
