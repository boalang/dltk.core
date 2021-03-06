/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.search;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.internal.ui.browsing.LogicalPackage;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;

public class DLTKSearchScopeFactory {

	private static DLTKSearchScopeFactory fgInstance;
	private final IDLTKSearchScope EMPTY_SCOPE = SearchEngine.createSearchScope(new IModelElement[] {}, null);

	private DLTKSearchScopeFactory() {
	}

	public static DLTKSearchScopeFactory getInstance() {
		if (fgInstance == null) {
			synchronized (DLTKSearchScopeFactory.class) {
				if (fgInstance == null) {
					fgInstance = new DLTKSearchScopeFactory();
				}
			}
		}
		return fgInstance;
	}

	public IWorkingSet[] queryWorkingSets() throws ModelException {
		Shell shell = DLTKUIPlugin.getActiveWorkbenchShell();
		if (shell == null)
			return null;
		IWorkingSetSelectionDialog dialog = PlatformUI.getWorkbench().getWorkingSetManager()
				.createWorkingSetSelectionDialog(shell, true);
		if (dialog.open() == Window.OK) {
			IWorkingSet[] workingSets = dialog.getSelection();
			if (workingSets.length > 0)
				return workingSets;
		}
		return null;
	}

	public IDLTKSearchScope createSearchScope(IWorkingSet[] workingSets, boolean includeInterp,
			IDLTKLanguageToolkit toolkit) {
		if (workingSets == null || workingSets.length < 1)
			return EMPTY_SCOPE;

		Set modelElements = new HashSet(workingSets.length * 10);
		for (int i = 0; i < workingSets.length; i++) {
			IWorkingSet workingSet = workingSets[i];
			if (workingSet.isEmpty() && workingSet.isAggregateWorkingSet()) {
				return createWorkspaceScope(includeInterp, toolkit);
			}
			addModelElements(modelElements, workingSet);
		}
		return createSearchScope(modelElements, includeInterp, toolkit);
	}

	public IDLTKSearchScope createSearchScope(IWorkingSet workingSet, boolean includeInterp,
			IDLTKLanguageToolkit toolkit) {
		Set modelElements = new HashSet(10);
		if (workingSet.isEmpty() && workingSet.isAggregateWorkingSet()) {
			return createWorkspaceScope(includeInterp, toolkit);
		}
		addModelElements(modelElements, workingSet);
		return createSearchScope(modelElements, includeInterp, toolkit);
	}

	public IDLTKSearchScope createSearchScope(IResource[] resources, boolean includeInterp,
			IDLTKLanguageToolkit toolkit) {
		if (resources == null)
			return EMPTY_SCOPE;
		Set modelElements = new HashSet(resources.length);
		addModelElements(modelElements, resources);
		return createSearchScope(modelElements, includeInterp, toolkit);
	}

	public IDLTKSearchScope createSearchScope(ISelection selection, boolean includeInterp,
			IDLTKLanguageToolkit toolkit) {
		return createSearchScope(getModelElements(selection), includeInterp, toolkit);
	}

	public IDLTKSearchScope createProjectSearchScope(String[] projectNames, boolean includeInterp,
			IDLTKLanguageToolkit toolkit) {
		ArrayList res = new ArrayList();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		for (int i = 0; i < projectNames.length; i++) {
			IScriptProject project = DLTKCore.create(root.getProject(projectNames[i]));
			if (project.exists()) {
				res.add(project);
			}
		}
		return createSearchScope(res, includeInterp, toolkit);
	}

	public IDLTKSearchScope createProjectSearchScope(IScriptProject project, boolean includeInterp) {
		return SearchEngine.createSearchScope(project, getSearchFlags(includeInterp));
	}

	public IDLTKSearchScope createProjectSearchScope(IEditorInput editorInput, boolean includeInterp) {
		IModelElement elem = DLTKUIPlugin.getEditorInputModelElement(editorInput);
		if (elem != null) {
			IScriptProject project = elem.getScriptProject();
			if (project != null) {
				return createProjectSearchScope(project, includeInterp);
			}
		}
		return EMPTY_SCOPE;
	}

	public String getWorkspaceScopeDescription(boolean includeInterp) {
		return includeInterp ? SearchMessages.WorkspaceScope : SearchMessages.WorkspaceScopeNoInterpreterEnvironment;
	}

	public String getProjectScopeDescription(String[] projectNames, boolean includeInterp) {
		if (projectNames.length == 0) {
			return SearchMessages.DLTKSearchScopeFactory_undefined_projects;
		}
		String scopeDescription;
		if (projectNames.length == 1) {
			String label = includeInterp ? SearchMessages.EnclosingProjectScope
					: SearchMessages.EnclosingProjectScopeNoInterpreterEnvironment;
			scopeDescription = MessageFormat.format(label, projectNames[0]);
		} else if (projectNames.length == 2) {
			String label = includeInterp ? SearchMessages.EnclosingProjectsScope2
					: SearchMessages.EnclosingProjectsScope2NoInterpreterEnvironment;
			scopeDescription = MessageFormat.format(label, projectNames[0], projectNames[1]);
		} else {
			String label = includeInterp ? SearchMessages.EnclosingProjectsScope
					: SearchMessages.EnclosingProjectsScopeNoInterpreterEnvironment;
			scopeDescription = MessageFormat.format(label, projectNames[0], projectNames[1]);
		}
		return scopeDescription;
	}

	public String getProjectScopeDescription(IScriptProject project, boolean includeInterp) {
		if (includeInterp) {
			return MessageFormat.format(SearchMessages.ProjectScope, project.getElementName());
		}
		return MessageFormat.format(SearchMessages.ProjectScopeNoInterpreterEnvironment, project.getElementName());
	}

	public String getProjectScopeDescription(IEditorInput editorInput, boolean includeInterp) {
		IModelElement elem = DLTKUIPlugin.getEditorInputModelElement(editorInput);
		if (elem != null) {
			IScriptProject project = elem.getScriptProject();
			if (project != null) {
				return getProjectScopeDescription(project, includeInterp);
			}
		}
		return MessageFormat.format(SearchMessages.ProjectScope, ""); //$NON-NLS-1$
	}

	public String getHierarchyScopeDescription(IType type) {
		return MessageFormat.format(SearchMessages.HierarchyScope, type.getElementName());
	}

	public String getSelectionScopeDescription(IModelElement[] modelElements, boolean includeInterp) {
		if (modelElements.length == 0) {
			return SearchMessages.DLTKSearchScopeFactory_undefined_selection;
		}
		String scopeDescription;
		if (modelElements.length == 1) {
			String label = includeInterp ? SearchMessages.SingleSelectionScope
					: SearchMessages.SingleSelectionScopeNoInterpreterEnvironment;
			scopeDescription = MessageFormat.format(label, modelElements[0].getElementName());
		} else if (modelElements.length == 1) {
			String label = includeInterp ? SearchMessages.DoubleSelectionScope
					: SearchMessages.DoubleSelectionScopeNoInterpreterEnvironment;
			scopeDescription = MessageFormat.format(label, modelElements[0].getElementName(),
					modelElements[1].getElementName());
		} else {
			String label = includeInterp ? SearchMessages.SelectionScope
					: SearchMessages.SelectionScopeNoInterpreterEnvironment;
			scopeDescription = MessageFormat.format(label, modelElements[0].getElementName(),
					modelElements[1].getElementName());
		}
		return scopeDescription;
	}

	public String getWorkingSetScopeDescription(IWorkingSet[] workingSets, boolean includeInterp) {
		if (workingSets.length == 0) {
			return SearchMessages.DLTKSearchScopeFactory_undefined_workingsets;
		}
		if (workingSets.length == 1) {
			String label = includeInterp ? SearchMessages.SingleWorkingSetScope
					: SearchMessages.SingleWorkingSetScopeNoInterpreterEnvironment;
			return MessageFormat.format(label, workingSets[0].getLabel());
		}
		Arrays.sort(workingSets, new WorkingSetComparator());
		if (workingSets.length == 2) {
			String label = includeInterp ? SearchMessages.DoubleWorkingSetScope
					: SearchMessages.DoubleWorkingSetScopeNoInterpreterEnvironment;
			return MessageFormat.format(label, workingSets[0].getLabel(), workingSets[1].getLabel());
		}
		String label = includeInterp ? SearchMessages.WorkingSetsScope
				: SearchMessages.WorkingSetsScopeNoInterpreterEnvironment;
		return MessageFormat.format(label, workingSets[0].getLabel(), workingSets[1].getLabel());
	}

	public IProject[] getProjects(IDLTKSearchScope scope) {
		IPath[] paths = scope.enclosingProjectsAndZips();
		HashSet<IProject> temp = new HashSet<>();
		for (int i = 0; i < paths.length; i++) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(paths[i]);
			if (resource != null && resource.getType() == IResource.PROJECT)
				temp.add((IProject) resource);
		}
		return temp.toArray(new IProject[temp.size()]);
	}

	public IModelElement[] getModelElements(ISelection selection) {
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			return getModelElements(((IStructuredSelection) selection).toArray());
		}
		return new IModelElement[0];
	}

	private IModelElement[] getModelElements(Object[] elements) {
		if (elements.length == 0)
			return new IModelElement[0];

		Set<IModelElement> result = new HashSet<>(elements.length);
		for (int i = 0; i < elements.length; i++) {
			Object selectedElement = elements[i];
			if (selectedElement instanceof IModelElement) {
				addModelElements(result, (IModelElement) selectedElement);
			} else if (selectedElement instanceof IResource) {
				addModelElements(result, (IResource) selectedElement);
			} else if (selectedElement instanceof LogicalPackage) {
				addModelElements(result, (LogicalPackage) selectedElement);
			} else if (selectedElement instanceof IWorkingSet) {
				IWorkingSet ws = (IWorkingSet) selectedElement;
				addModelElements(result, ws);
			} else if (selectedElement instanceof IAdaptable) {
				IResource resource = ((IAdaptable) selectedElement).getAdapter(IResource.class);
				if (resource != null)
					addModelElements(result, resource);

				IModelElement modelElement = ((IAdaptable) selectedElement).getAdapter(IModelElement.class);
				if (modelElement != null)
					addModelElements(result, modelElement);
			}

		}
		return result.toArray(new IModelElement[result.size()]);
	}

	public IDLTKSearchScope createSearchScope(IModelElement[] modelElements, boolean includeInterp,
			IDLTKLanguageToolkit toolkit) {
		if (modelElements.length == 0)
			return EMPTY_SCOPE;
		return SearchEngine.createSearchScope(modelElements, getSearchFlags(includeInterp), toolkit);
	}

	private IDLTKSearchScope createSearchScope(Collection<IModelElement> modelElements, boolean includeInterp,
			IDLTKLanguageToolkit toolkit) {
		if (modelElements.isEmpty())
			return EMPTY_SCOPE;
		IModelElement[] elementArray = modelElements.toArray(new IModelElement[modelElements.size()]);
		return SearchEngine.createSearchScope(elementArray, getSearchFlags(includeInterp), toolkit);
	}

	private static int getSearchFlags(boolean includeInterp) {
		int flags = IDLTKSearchScope.SOURCES | IDLTKSearchScope.APPLICATION_LIBRARIES;
		if (includeInterp)
			flags |= IDLTKSearchScope.SYSTEM_LIBRARIES;
		return flags;
	}

	private void addModelElements(Set modelElements, IResource[] resources) {
		for (int i = 0; i < resources.length; i++)
			addModelElements(modelElements, resources[i]);
	}

	private void addModelElements(Set modelElements, IResource resource) {
		IModelElement modelElement = resource.getAdapter(IModelElement.class);
		if (modelElement == null)
			// not a Script resource
			return;

		if (modelElement.getElementType() == IModelElement.SCRIPT_FOLDER) {
			// add other possible package fragments
			try {
				addModelElements(modelElements, ((IFolder) resource).members());
			} catch (CoreException ex) {
				// don't add elements
			}
		}

		modelElements.add(modelElement);
	}

	private void addModelElements(Set modelElements, IModelElement modelElement) {
		modelElements.add(modelElement);
	}

	private void addModelElements(Set modelElements, IWorkingSet workingSet) {
		if (workingSet == null)
			return;

		if (workingSet.isAggregateWorkingSet() && workingSet.isEmpty()) {
			try {
				IScriptProject[] projects = DLTKCore.create(ResourcesPlugin.getWorkspace().getRoot())
						.getScriptProjects();
				modelElements.addAll(Arrays.asList(projects));
			} catch (ModelException e) {
				DLTKUIPlugin.log(e);
			}
			return;
		}

		IAdaptable[] elements = workingSet.getElements();
		for (int i = 0; i < elements.length; i++) {
			IModelElement modelElement = elements[i].getAdapter(IModelElement.class);
			if (modelElement != null) {
				addModelElements(modelElements, modelElement);
				continue;
			}
			IResource resource = elements[i].getAdapter(IResource.class);
			if (resource != null) {
				addModelElements(modelElements, resource);
			}

			// else we don't know what to do with it, ignore.
		}
	}

	public void addModelElements(Set modelElements, LogicalPackage selectedElement) {
		IScriptFolder[] packages = selectedElement.getFragments();
		for (int i = 0; i < packages.length; i++)
			addModelElements(modelElements, packages[i]);
	}

	public IDLTKSearchScope createWorkspaceScope(boolean includeInterp, IDLTKLanguageToolkit toolkit) {
		if (!includeInterp) {
			try {
				IScriptProject[] projects = DLTKCore.create(ResourcesPlugin.getWorkspace().getRoot())
						.getScriptProjects();
				return SearchEngine.createSearchScope(projects, getSearchFlags(includeInterp), toolkit);
			} catch (ModelException e) {
				// ignore, use workspace scope instead
			}
		}
		return SearchEngine.createWorkspaceScope(toolkit);
	}

	public boolean isInsideInterpreter(IModelElement element) {
		IProjectFragment root = (IProjectFragment) element.getAncestor(IModelElement.PROJECT_FRAGMENT);
		if (root != null) {
			try {
				IBuildpathEntry entry = root.getRawBuildpathEntry();
				if (entry != null && entry.getEntryKind() == IBuildpathEntry.BPE_CONTAINER) {
					IBuildpathContainer container = DLTKCore.getBuildpathContainer(entry.getPath(),
							root.getScriptProject());
					return container != null && container.getKind() == IBuildpathContainer.K_DEFAULT_SYSTEM;
				}
				return false;
			} catch (ModelException e) {
				DLTKUIPlugin.log(e);
			}
		}
		return true; // include InterpreterEnvironment in doubt
	}
}
