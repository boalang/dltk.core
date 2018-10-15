/*******************************************************************************
 * Copyright (c) 2009, 2017 IBM Corporation and others.
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
package org.eclipse.dltk.internal.core.index.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkitExtension;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.core.index.sql.Container;
import org.eclipse.dltk.core.index.sql.DbFactory;
import org.eclipse.dltk.core.index.sql.Element;
import org.eclipse.dltk.core.index.sql.File;
import org.eclipse.dltk.core.index.sql.IElementHandler;
import org.eclipse.dltk.core.index.sql.SqlIndex;
import org.eclipse.dltk.core.index2.search.ISearchEngineExtension;
import org.eclipse.dltk.core.index2.search.ISearchRequestor;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.internal.core.ArchiveFolder;
import org.eclipse.dltk.internal.core.BuiltinScriptFolder;
import org.eclipse.dltk.internal.core.ExternalScriptFolder;
import org.eclipse.dltk.internal.core.ProjectFragment;
import org.eclipse.dltk.internal.core.search.DLTKSearchScope;
import org.eclipse.dltk.internal.core.search.DLTKWorkspaceScope;

/**
 * Search engine implementation for SQL-based index.
 * 
 * @author michael
 * @since 2.0
 */
public class SqlSearchEngine implements ISearchEngineExtension {

	@Override
	public void search(int elementType, String qualifier, String elementName,
			int trueFlags, int falseFlags, int limit, SearchFor searchFor,
			MatchRule matchRule, IDLTKSearchScope scope,
			ISearchRequestor requestor, IProgressMonitor monitor) {
		search(elementType, qualifier, elementName, null, trueFlags, falseFlags,
				limit, searchFor, matchRule, scope, requestor, monitor);
	}

	@Override
	public void search(int elementType, String qualifier, String elementName,
			String parent, int trueFlags, int falseFlags, int limit,
			SearchFor searchFor, MatchRule matchRule, IDLTKSearchScope scope,
			final ISearchRequestor requestor, IProgressMonitor monitor) {

		try {
			DbFactory dbFactory = DbFactory.getInstance();
			if (dbFactory == null) {
				return;
			}
			Connection connection = dbFactory.createConnection();
			if (connection == null) {
				return;
			}
			try {
				String natureId = scope.getLanguageToolkit().getNatureId();
				ElementHandler elementHandler = new ElementHandler(connection,
						scope, requestor);

				// Calculate filtering by container:
				int[] containersId = null;
				int[] filesId = null;
				if (!(scope instanceof DLTKWorkspaceScope)) {
					// Calculate container IDs:
					IPath[] containerPaths = scope.enclosingProjectsAndZips();
					List<Integer> containerIdsList = new LinkedList<>();
					for (IPath containerPath : containerPaths) {
						Container container = dbFactory.getContainerDao()
								.selectByPath(connection,
										containerPath.toString());
						if (container != null) {
							containerIdsList.add(container.getId());
						}
					}
					if (containerIdsList.size() > 0) {
						containersId = new int[containerIdsList.size()];
						for (int i = 0; i < containerIdsList.size(); ++i) {
							containersId[i] = containerIdsList.get(i);
						}
					}

					// Calculate file IDs:
					if (scope instanceof DLTKSearchScope) {
						List<Integer> fileIdsList = new LinkedList<>();
						String[] relativePaths = ((DLTKSearchScope) scope)
								.getRelativePaths();
						String[] fileExtensions = ScriptModelUtil
								.getFileExtensions(scope.getLanguageToolkit());

						// XXX - need a better way do differentiate between file
						// and container scopes
						for (String relativePath : relativePaths) {
							if (relativePath.length() > 0) {
								if (fileExtensions != null) {
									boolean isScriptFile = false;
									for (String ext : fileExtensions) {
										if (relativePath.endsWith("." + ext)) {
											isScriptFile = true;
											break;
										}
									}
									if (!isScriptFile) {
										break;
									}
								}
								for (Integer containerId : containerIdsList) {
									File file = dbFactory.getFileDao().select(
											connection, relativePath,
											containerId);
									if (file != null) {
										fileIdsList.add(file.getId());
									}
								}
							}
						}
						if (fileIdsList.size() > 0) {
							filesId = new int[fileIdsList.size()];
							for (int i = 0; i < fileIdsList.size(); ++i) {
								filesId[i] = fileIdsList.get(i);
							}
						}
					}
				}

				boolean searchForDecls = searchFor == SearchFor.DECLARATIONS
						|| searchFor == SearchFor.ALL_OCCURRENCES;
				boolean searchForRefs = searchFor == SearchFor.REFERENCES
						|| searchFor == SearchFor.ALL_OCCURRENCES;

				if (searchForDecls) {
					dbFactory.getElementDao().search(connection, elementName,
							matchRule, elementType, trueFlags, falseFlags,
							qualifier, parent, filesId, containersId, natureId,
							limit, false, elementHandler, monitor);
				}
				if (searchForRefs) {
					dbFactory.getElementDao().search(connection, elementName,
							matchRule, elementType, trueFlags, falseFlags,
							qualifier, parent, filesId, containersId, natureId,
							limit, true, elementHandler, monitor);
				}
			} finally {
				connection.close();
			}
		} catch (SQLException e) {
			SqlIndex.error("An exception has thrown while performing a search",
					e);
		}
	}

	private static class FilePathHandler {

		final IPath folderPath;
		final String fileName;

		public FilePathHandler(String filePath) {
			int i = filePath.lastIndexOf('/');
			if (i == -1) {
				i = filePath.lastIndexOf('\\');
			}
			if (i != -1) {
				this.folderPath = new Path(filePath.substring(0, i));
				this.fileName = filePath.substring(i + 1);
			} else {
				this.folderPath = Path.EMPTY;
				this.fileName = filePath;
			}
		}

	}

	class ElementHandler implements IElementHandler, ISearchRequestor {

		private Map<Integer, File> fileCache = new HashMap<>();
		private Map<Integer, Container> containerCache = new HashMap<>();
		private Map<String, IProjectFragment> projectFragmentCache = new HashMap<>();
		private Map<String, ISourceModule> sourceModuleCache = new HashMap<>();
		private Connection connection;
		private ISearchRequestor searchRequestor;
		private IDLTKSearchScope scope;

		public ElementHandler(Connection connection, IDLTKSearchScope scope,
				ISearchRequestor searchRequestor) {

			this.connection = connection;
			this.scope = scope;
			this.searchRequestor = searchRequestor;
		}

		@Override
		public void handle(Element element) {
			try {
				DbFactory dbFactory = DbFactory.getInstance();

				int fileId = element.getFileId();
				File file = fileCache.get(fileId);
				if (file == null) {
					file = dbFactory.getFileDao().selectById(connection,
							fileId);
					if (file == null) {
						return;
					}
					fileCache.put(fileId, file);
				}

				int containerId = file.getContainerId();
				Container container = containerCache.get(containerId);
				if (container == null) {
					container = dbFactory.getContainerDao()
							.selectById(connection, containerId);
					if (container == null) {
						return;
					}
					containerCache.put(containerId, container);
				}

				String containerPath = container.getPath();
				IDLTKLanguageToolkit toolkit = ((DLTKSearchScope) scope)
						.getLanguageToolkit();
				if (toolkit instanceof IDLTKLanguageToolkitExtension
						&& ((IDLTKLanguageToolkitExtension) toolkit)
								.isArchiveFileName(containerPath)) {
					containerPath = containerPath
							+ IDLTKSearchScope.FILE_ENTRY_SEPARATOR;
				}
				if (containerPath.length() != 0 && containerPath.charAt(
						containerPath.length() - 1) != IPath.SEPARATOR) {
					containerPath = containerPath + IPath.SEPARATOR;
				}

				String filePath = file.getPath();
				final String resourcePath = containerPath + filePath;

				IProjectFragment projectFragment = projectFragmentCache
						.get(containerPath);

				if (projectFragment == null) {
					projectFragment = ((DLTKSearchScope) scope)
							.projectFragment(resourcePath);
					if (projectFragment == null) {
						projectFragment = ((DLTKSearchScope) scope)
								.projectFragment(containerPath);
					}
					projectFragmentCache.put(containerPath, projectFragment);
				}
				if (projectFragment == null) {
					return;
				}

				if (!scope.encloses(resourcePath)) {
					return;
				}

				ISourceModule sourceModule = sourceModuleCache
						.get(resourcePath);
				if (sourceModule == null) {
					if (projectFragment.isArchive()) {
						FilePathHandler filePathHandler = new FilePathHandler(
								filePath);
						IScriptFolder scriptFolder = new ArchiveFolder(
								(ProjectFragment) projectFragment,
								filePathHandler.folderPath);
						sourceModule = scriptFolder
								.getSourceModule(filePathHandler.fileName);
					} else if (projectFragment.isExternal()) {
						FilePathHandler filePathHandler = new FilePathHandler(
								filePath);
						IScriptFolder scriptFolder = new ExternalScriptFolder(
								(ProjectFragment) projectFragment,
								filePathHandler.folderPath);
						sourceModule = scriptFolder
								.getSourceModule(filePathHandler.fileName);
					} else if (projectFragment.isBuiltin()) {
						FilePathHandler filePathHandler = new FilePathHandler(
								filePath);
						IScriptFolder scriptFolder = new BuiltinScriptFolder(
								(ProjectFragment) projectFragment,
								filePathHandler.folderPath);
						sourceModule = scriptFolder
								.getSourceModule(filePathHandler.fileName);
					} else {
						IProject project = projectFragment.getScriptProject()
								.getProject();
						sourceModule = DLTKCore.createSourceModuleFrom(
								project.getFile(filePath));
					}
					sourceModuleCache.put(resourcePath, sourceModule);
				}

				match(element.getType(), element.getFlags(),
						element.getOffset(), element.getLength(),
						element.getNameOffset(), element.getNameLength(),
						element.getName(), element.getMetadata(),
						element.getDoc(), element.getQualifier(),
						element.getParent(), sourceModule,
						element.isReference());

			} catch (SQLException e) {
				SqlIndex.error(
						"An exception is thrown while searching elements", e);
			}
		}

		@Override
		public void match(int elementType, int flags, int offset, int length,
				int nameOffset, int nameLength, String elementName,
				String metadata, String doc, String qualifier, String parent,
				ISourceModule sourceModule, boolean isReference) {

			searchRequestor.match(elementType, flags, offset, length,
					nameOffset, nameLength, elementName, metadata, doc,
					qualifier, parent, sourceModule, isReference);
		}
	}
}
