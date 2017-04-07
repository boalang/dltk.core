/*******************************************************************************
 * Copyright (c) 2009, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.internal.core.index.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkitExtension;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.index.sql.Container;
import org.eclipse.dltk.core.index.sql.DbFactory;
import org.eclipse.dltk.core.index.sql.File;
import org.eclipse.dltk.core.index.sql.IElementDao;
import org.eclipse.dltk.core.index.sql.SqlIndex;
import org.eclipse.dltk.core.index2.AbstractIndexer;
import org.eclipse.dltk.core.index2.search.ISearchEngine;
import org.eclipse.dltk.internal.core.ExternalSourceModule;
import org.eclipse.dltk.internal.core.SourceModule;
import org.eclipse.dltk.internal.core.util.Util;
import org.eclipse.osgi.util.NLS;

/**
 * Implementation of SQL-based indexer.
 * 
 * @author michael
 * 
 */
public class SqlIndexer extends AbstractIndexer {

	private Connection connection;
	private File file;
	private String natureId;
	private IElementDao elementDao;

	public SqlIndexer() {
		final DbFactory db = DbFactory.getInstance();
		if (db == null) {
			throw new IllegalStateException("DbFactory not available");
		}
		elementDao = db.getElementDao();
	}

	@Override
	public void addDeclaration(DeclarationInfo info) {

		try {
			elementDao.insert(connection, info.elementType, info.flags,
					info.offset, info.length, info.nameOffset, info.nameLength,
					info.elementName, info.metadata, info.doc, info.qualifier,
					info.parent, file.getId(), natureId, false);

		} catch (SQLException e) {
			SqlIndex.error(
					"An exception was thrown while inserting model element declaration",
					e);
		}
	}

	@Override
	public void addReference(ReferenceInfo info) {

		try {
			elementDao.insert(connection, info.elementType, 0, info.offset,
					info.length, 0, 0, info.elementName, info.metadata, null,
					info.qualifier, null, file.getId(), natureId, true);

		} catch (SQLException e) {
			SqlIndex.error(
					"An exception was thrown while inserting model element reference",
					e);
		}
	}

	@Override
	public void indexDocument(ISourceModule sourceModule) {

		final IFileHandle fileHandle = EnvironmentPathUtils
				.getFile(sourceModule);

		try {
			DbFactory dbFactory = DbFactory.getInstance();
			connection = dbFactory.createConnection();
			try {
				connection.setAutoCommit(false);

				IDLTKLanguageToolkit toolkit = DLTKLanguageManager
						.getLanguageToolkit(sourceModule);
				if (toolkit == null) {
					return;
				}

				natureId = toolkit.getNatureId();

				IPath containerPath;
				if (sourceModule instanceof SourceModule) {
					containerPath = sourceModule.getScriptProject().getPath();
				} else {
					containerPath = sourceModule
							.getAncestor(IModelElement.PROJECT_FRAGMENT)
							.getPath();
				}
				Container container = dbFactory.getContainerDao()
						.insert(connection, containerPath.toString());

				String relativePath;
				if (toolkit instanceof IDLTKLanguageToolkitExtension
						&& ((IDLTKLanguageToolkitExtension) toolkit)
								.isArchiveFileName(
										sourceModule.getPath().toString())) {
					relativePath = ((ExternalSourceModule) sourceModule)
							.getFullPath().toString();
				} else {
					relativePath = Util.relativePath(sourceModule.getPath(),
							containerPath.segmentCount());
				}

				long lastModified = fileHandle == null ? 0
						: fileHandle.lastModified();

				File existing = dbFactory.getFileDao().select(connection,
						relativePath, container.getId());
				if (existing != null) {
					if (existing.getTimestamp() == lastModified) {
						// File is not updated - nothing to do
						return;
					}
					// Re-index:
					dbFactory.getFileDao().deleteById(connection,
							existing.getId());
				}
				file = dbFactory.getFileDao().insert(connection, relativePath,
						lastModified, container.getId());

				super.indexDocument(sourceModule);

			} finally {
				elementDao.commitInsertions();
				connection.commit();
				connection.close();
			}
		} catch (Exception e) {
			SqlIndex.error("An exception was thrown while indexing document",
					e);
		}
	}

	@Override
	public Map<String, Long> getDocuments(IPath containerPath) {
		try {
			DbFactory dbFactory = DbFactory.getInstance();

			try (Connection connection = dbFactory.createConnection()) {
				Container containerDao = dbFactory.getContainerDao()
						.selectByPath(connection, containerPath.toString());
				if (containerDao != null) {

					File[] files = dbFactory.getFileDao().selectByContainerId(
							connection, containerDao.getId());
					Map<String, Long> paths = new HashMap<>();
					for (File fileDao : files) {
						paths.put(fileDao.getPath(), fileDao.getTimestamp());
					}
					return paths;
				}
			}
		} catch (SQLException e) {
			SqlIndex.error(
					"An exception thrown while analyzing source module changes",
					e);
		}
		return null;
	}

	@Override
	public void removeContainer(IPath containerPath) {
		try {
			DbFactory dbFactory = DbFactory.getInstance();
			try (Connection connection = dbFactory.createConnection()) {
				dbFactory.getContainerDao().deleteByPath(connection,
						containerPath.toString());
			}
		} catch (SQLException e) {
			SqlIndex.error(NLS.bind(
					"An exception thrown while removing container ''{0}'' from index",
					containerPath.toString()), e);
		}
	}

	@Override
	public void removeDocument(IPath containerPath, String relativePath) {
		try {
			DbFactory dbFactory = DbFactory.getInstance();
			try (Connection connection = dbFactory.createConnection()) {
				Container containerDao = dbFactory.getContainerDao()
						.selectByPath(connection, containerPath.toString());
				if (containerDao != null) {
					dbFactory.getFileDao().delete(connection, relativePath,
							containerDao.getId());
				}
			}
		} catch (SQLException e) {
			SqlIndex.error(NLS.bind(
					"An exception thrown while removing file ''{0}'' from index",
					containerPath.append(relativePath).toString()), e);
		}
	}

	@Override
	public ISearchEngine createSearchEngine() {
		return new SqlSearchEngine();
	}
}
