/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.core.util.MementoTokenizer;
import org.eclipse.dltk.utils.CorePrinter;

public class Model extends Openable implements IScriptModel {
	/**
	 * A set of java.io.Files used as a cache of external files that are known to be
	 * existing. Note this cache is kept for the whole session.
	 */
	public static HashSet<IFileHandle> existingExternalFiles = new HashSet<>();

	/**
	 * A set of external files ({@link #existingExternalFiles}) which have been
	 * confirmed as file (ie. which returns true to {@link java.io.File#isFile()}.
	 * Note this cache is kept for the whole session.
	 */
	public static HashSet<IFileHandle> existingExternalConfirmedFiles = new HashSet<>();

	protected Model() {
		super(null);
	}

	@Override
	protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm, Map newElements,
			IResource underlyingResource) /*
											 * throws ModelException
											 */ {
		// determine my children
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		int length = projects.length;
		IModelElement[] children = new IModelElement[length];
		int index = 0;
		for (int i = 0; i < length; i++) {
			IProject project = projects[i];
			if (ScriptProject.hasScriptNature(project)) {
				children[index++] = getScriptProject(project);
			}
		}
		if (index < length)
			System.arraycopy(children, 0, children = new IModelElement[index], 0, index);
		info.setChildren(children);

		newElements.put(this, info);
		return true;
	}

	/**
	 * Helper method - returns the targeted item (IResource if internal or
	 * IFileHandle if external), or null if unbound Internal items must be referred
	 * to using container relative paths.
	 */
	public static Object getTarget(IContainer container, IPath path, boolean checkResourceExistence) {
		if (path == null)
			return null;
		// lookup - inside the container
		if (path.getDevice() == null) { // container relative paths should not
			// contain a device
			// (see http://dev.eclipse.org/bugs/show_bug.cgi?id=18684)
			// (case of a workspace rooted at d:\ )
			IResource resource = container.findMember(path);
			if (resource != null) {
				if (!checkResourceExistence || resource.exists())
					return resource;
				return null;
			}
		}
		// if path is relative, it cannot be an external path
		// (see http://dev.eclipse.org/bugs/show_bug.cgi?id=22517)
		if (!path.isAbsolute())
			return null;
		// lookup - outside the container
		IFileHandle externalFile = EnvironmentPathUtils.getFile(path);
		if (externalFile != null) {
			if (!checkResourceExistence) {
				return externalFile;
			} else if (existingExternalFiles.contains(externalFile)) {
				return externalFile;
			} else {
				if (ModelManager.ZIP_ACCESS_VERBOSE) {
					System.out.println("(" + Thread.currentThread() //$NON-NLS-1$
							+ ") [Model.getTarget(...)] Checking existence of " //$NON-NLS-1$
							+ path.toString());
				}
				if (externalFile.exists()) {
					// cache external file
					existingExternalFiles.add(externalFile);
					return externalFile;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the active script project associated with the specified resource, or
	 * <code>null</code> if no script project yet exists for the resource.
	 *
	 * @exception IllegalArgumentException if the given resource is not one of an
	 *                                     IProject, IFolder, or IFile.
	 */
	public IScriptProject getScriptProject(IResource resource) {
		switch (resource.getType()) {
		case IResource.FOLDER:
			return new ScriptProject(((IFolder) resource).getProject(), this);
		case IResource.FILE:
			return new ScriptProject(((IFile) resource).getProject(), this);
		case IResource.PROJECT:
			return new ScriptProject((IProject) resource, this);
		default:
			throw new IllegalArgumentException(Messages.Model_invalidResourceForTheProject);
		}
	}

	/**
	 * @see IScriptModel
	 */
	@Override
	public IScriptProject getScriptProject(String projectName) {
		return new ScriptProject(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName), this);
	}

	/**
	 * @see IScriptModel
	 */
	@Override
	public IScriptProject[] getScriptProjects() throws ModelException {
		final List<IModelElement> list = getChildrenOfType(SCRIPT_PROJECT);
		return list.toArray(new IScriptProject[list.size()]);
	}

	@Override
	public IScriptProject[] getScriptProjects(String nature) throws ModelException {
		final List<IModelElement> list = getChildrenOfType(SCRIPT_PROJECT);
		final List<IScriptProject> result = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			IScriptProject project = (IScriptProject) list.get(i);
			IDLTKLanguageToolkit toolkit = DLTKLanguageManager.getLanguageToolkit(project);
			if (toolkit.getNatureId().equals(nature)) {
				result.add(project);
			}
		}

		return result.toArray(new IScriptProject[result.size()]);
	}

	@Override
	public void copy(IModelElement[] elements, IModelElement[] containers, IModelElement[] siblings, String[] renamings,
			boolean force, IProgressMonitor monitor) throws ModelException {
		if (elements != null && elements.length > 0 && elements[0] != null
				&& elements[0].getElementType() < IModelElement.TYPE) {
			runOperation(new CopyResourceElementsOperation(elements, containers, force), elements, siblings, renamings,
					monitor);
		} else {
			runOperation(new CopyElementsOperation(elements, containers, force), elements, siblings, renamings,
					monitor);
		}
	}

	@Override
	protected Object createElementInfo() {
		return new ModelInfo();
	}

	@Override
	public int getElementType() {
		return SCRIPT_MODEL;
	}

	@Override
	public IResource getResource() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	@Override
	public IPath getPath() {
		return Path.ROOT;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Model))
			return false;
		return super.equals(o);
	}

	/**
	 * Flushes the cache of external files known to be existing.
	 */
	public static void flushExternalFileCache() {
		existingExternalFiles = new HashSet<>();
		existingExternalConfirmedFiles = new HashSet<>();
	}

	/**
	 * Configures and runs the <code>MultiOperation</code>.
	 */
	protected void runOperation(MultiOperation op, IModelElement[] elements, IModelElement[] siblings,
			String[] renamings, IProgressMonitor monitor) throws ModelException {
		op.setRenamings(renamings);
		if (siblings != null) {
			for (int i = 0; i < elements.length; i++) {
				op.setInsertBefore(elements[i], siblings[i]);
			}
		}
		op.runOperation(monitor);
	}

	/**
	 * @private Debugging purposes
	 */
	@Override
	protected void toStringInfo(int tab, StringBuilder buffer, Object info, boolean showResolvedInfo) {
		buffer.append(this.tabString(tab));
		buffer.append("Model"); //$NON-NLS-1$
		if (info == null) {
			buffer.append(" (not open)"); //$NON-NLS-1$
		}
	}

	@Override
	public void printNode(CorePrinter output) {
		output.formatPrint("DLTK Model:" + getElementName()); //$NON-NLS-1$
		output.indent();
		try {
			IModelElement modelElements[] = this.getChildren();
			for (int i = 0; i < modelElements.length; ++i) {
				IModelElement element = modelElements[i];
				if (element instanceof ModelElement) {
					((ModelElement) element).printNode(output);
				} else {
					output.print("Unknown element:" + element); //$NON-NLS-1$
				}
			}
		} catch (ModelException ex) {
			output.formatPrint(ex.getLocalizedMessage());
		}
		output.dedent();
	}

	@Override
	public void delete(IModelElement[] elements, boolean force, IProgressMonitor monitor) throws ModelException {
		if (elements != null && elements.length > 0 && elements[0] != null
				&& elements[0].getElementType() < IModelElement.TYPE) {
			new DeleteResourceElementsOperation(elements, force).runOperation(monitor);
		} else {
			// new DeleteElementsOperation(elements,
			// force).runOperation(monitor);
		}
		if (DLTKCore.DEBUG) {
			System.err.println("Add Delete operations"); //$NON-NLS-1$
		}
	}

	/**
	 * Helper method - returns the file item (ie. which returns true to
	 * {@link java.io.File#isFile()}, or null if unbound
	 */
	public static synchronized IFileHandle getFile(Object target) {
		if (existingExternalConfirmedFiles.contains(target))
			return (IFileHandle) target;
		if (target instanceof IFileHandle) {
			IFileHandle f = (IFileHandle) target;
			if (f.isFile()) {
				existingExternalConfirmedFiles.add(f);
				return f;
			}
		}
		return null;
	}

	/**
	 * Helper method - returns whether an object is afile (ie. which returns true to
	 * {@link java.io.File#isFile()}.
	 */
	public static boolean isFile(Object target) {
		return getFile(target) != null;
	}

	/*
	 * @see IScriptModel
	 */
	@Override
	public boolean contains(IResource resource) {
		switch (resource.getType()) {
		case IResource.ROOT:
		case IResource.PROJECT:
			return true;
		}
		// file or folder
		IScriptProject[] projects;
		try {
			projects = this.getScriptProjects();
		} catch (ModelException e) {
			return false;
		}
		for (int i = 0, length = projects.length; i < length; i++) {
			ScriptProject project = (ScriptProject) projects[i];
			if (!project.contains(resource)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public IModelElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner owner) {
		switch (token.charAt(0)) {
		case JEM_SCRIPTPROJECT:
			if (!memento.hasMoreTokens())
				return this;
			String projectName = memento.nextToken();
			ModelElement project = (ModelElement) getScriptProject(projectName);
			return project.getHandleFromMemento(memento, owner);
		case JEM_USER_ELEMENT:
			return MementoModelElementUtil.getHandleFromMemento(memento, this, owner);
		}
		return null;
	}

	@Override
	protected char getHandleMementoDelimiter() {
		Assert.isTrue(false, "Should not be called"); //$NON-NLS-1$
		return 0;
	}

	@Override
	public Object[] getForeignResources() throws ModelException {
		return ((ModelInfo) getElementInfo()).getForeignResources();
	}

	@Override
	public IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	@Override
	public void move(IModelElement[] elements, IModelElement[] containers, IModelElement[] siblings, String[] renamings,
			boolean force, IProgressMonitor monitor) throws ModelException {
		if (elements != null && elements.length > 0 && elements[0] != null
				&& elements[0].getElementType() < IModelElement.TYPE) {
			runOperation(new MoveResourceElementsOperation(elements, containers, force), elements, siblings, renamings,
					monitor);
		} else {
			if (DLTKCore.DEBUG) {
				System.err.println("TODO:Add move elements operation"); //$NON-NLS-1$
			}
			// runOperation(new MoveElementsOperation(elements, containers,
			// force), elements, siblings, renamings, monitor);
		}
	}

	@Override
	public IResource getUnderlyingResource() {
		return null;
	}

	@Override
	public void getHandleMemento(StringBuilder buff) {
		buff.append(getElementName());
	}

	@Override
	public void rename(IModelElement[] elements, IModelElement[] destinations, String[] renamings, boolean force,
			IProgressMonitor monitor) throws ModelException {
		MultiOperation op;
		if (elements != null && elements.length > 0 && elements[0] != null
				&& elements[0].getElementType() < IModelElement.TYPE) {
			op = new RenameResourceElementsOperation(elements, destinations, renamings, force);
		} else {
			op = new RenameElementsOperation(elements, destinations, renamings, force);
		}

		op.runOperation(monitor);
	}
}
