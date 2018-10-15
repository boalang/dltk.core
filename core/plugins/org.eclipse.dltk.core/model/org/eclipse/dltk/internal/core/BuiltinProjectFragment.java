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

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathContainerExtension;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IBuiltinModuleProvider;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelStatusConstants;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.internal.core.util.MementoTokenizer;
import org.eclipse.dltk.internal.core.util.Util;

/**
 * Project fragment for buildpath script folders and modules.
 */
public class BuiltinProjectFragment extends ProjectFragment {
	static final Object INTERPRETER_CONTAINER = "org.eclipse.dltk.launching.INTERPRETER_CONTAINER"; //$NON-NLS-1$

	protected final IPath fPath;

	IBuiltinModuleProvider builtinProvider;

	/**
	 * Maps folder names and source module names.
	 */

	protected BuiltinProjectFragment(IPath path, ScriptProject project) {
		super(null, project);
		this.fPath = path;
		builtinProvider = getBuiltinProvider(project);
	}

	public static IBuiltinModuleProvider getBuiltinProvider(
			IScriptProject project) {
		try {
			IBuildpathEntry[] entries = project.getRawBuildpath();
			IPath containerPath = null;
			for (int i = 0; i < entries.length; i++) {
				if (entries[i]
						.getEntryKind() == IBuildpathEntry.BPE_CONTAINER) {
					IPath path = entries[i].getPath();
					if (path.segment(0).equals(INTERPRETER_CONTAINER)) {
						containerPath = entries[i].getPath();
						break;
					}
				}
			}
			if (containerPath == null) {
				return null;
			}
			IBuildpathContainer buildpathContainer = ModelManager
					.getModelManager()
					.getBuildpathContainer(containerPath, project);
			if (buildpathContainer != null
					&& buildpathContainer instanceof IBuildpathContainerExtension) {
				return ((IBuildpathContainerExtension) buildpathContainer)
						.getBuiltinProvider();
			}
		} catch (CoreException ex) {
			if (DLTKCore.DEBUG) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	public static boolean isSupported(IScriptProject project) {
		IBuiltinModuleProvider prov = getBuiltinProvider(project);
		return prov != null && prov.getBuiltinModules() != null;
	}

	/**
	 * Compute the package fragment children of this package fragment root.
	 */
	@Override
	protected boolean computeChildren(OpenableElementInfo info, Map newElements)
			throws ModelException {
		ArrayList<IModelElement> vChildren = new ArrayList<>(5);
		char[][] inclusionPatterns = fullInclusionPatternChars();
		char[][] exclusionPatterns = fullExclusionPatternChars();
		computeFolderChildren(this.fPath,
				!Util.isExcluded(this.fPath, inclusionPatterns,
						exclusionPatterns, true),
				vChildren, newElements, inclusionPatterns, exclusionPatterns);
		IModelElement[] children = new IModelElement[vChildren.size()];
		vChildren.toArray(children);
		info.setChildren(children);
		return true;
	}

	/**
	 * Starting at this folder, create folders and add them to the collection of
	 * children.
	 *
	 * @param newElements
	 *
	 * @exception ModelException
	 *                The resource associated with this project fragment does
	 *                not exist
	 */
	protected void computeFolderChildren(IPath path, boolean isIncluded,
			ArrayList<IModelElement> vChildren, Map newElements,
			char[][] inclusionPatterns, char[][] exclusionPatterns)
			throws ModelException {
		BuiltinScriptFolder fldr = (BuiltinScriptFolder) getScriptFolder(
				Path.EMPTY);
		vChildren.add(fldr);
		if (this.builtinProvider == null) {
			return;
		}
		try {
			BuiltinScriptFolderInfo fragInfo = new BuiltinScriptFolderInfo();
			fldr.computeChildren(fragInfo,
					this.builtinProvider.getBuiltinModules());
			fldr.computeForeignResources(fragInfo);
			newElements.put(fldr, fragInfo);
		} catch (IllegalArgumentException e) {
			throw new ModelException(e,
					IModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
			/*
			 * could be thrown by ElementTree when path is not found
			 */
		}
	}

	@Override
	public IScriptFolder getScriptFolder(IPath path) {
		try {
			String portablePath = path.toPortableString();
			IModelElement[] children = getChildren();
			for (int i = 0; i < children.length; ++i) {
				IModelElement child = children[i];
				if (child.getElementType() == SCRIPT_FOLDER
						&& ((IScriptFolder) child).getElementName()
								.equals(portablePath)) {
					return ((IScriptFolder) child);
				}
			}
		} catch (ModelException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		return new BuiltinScriptFolder(this, path);
	}

	@Override
	public IScriptFolder getScriptFolder(String path) {
		return getScriptFolder(new Path(path));
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	protected Object createElementInfo() {
		return new BuiltinProjectFragmentInfo();
	}

	@Override
	public boolean isArchive() {
		return false;
	}

	@Override
	public boolean isExternal() {
		return true;
	}

	@Override
	public boolean isBuiltin() {
		return true;
	}

	@Override
	public IResource getUnderlyingResource() throws ModelException {
		return null;
	}

	@Override
	public int hashCode() {
		return this.fPath.hashCode();
	}

	@Override
	public IPath getPath() {
		if (isExternal()) {
			return this.fPath;
		} else {
			return super.getPath();
		}
	}

	@Override
	public IResource getResource() {
		return null;
	}

	/**
	 * Returns whether the corresponding resource or associated file exists
	 */
	@Override
	protected boolean resourceExists() {
		return true;
	}

	@Override
	protected void toStringAncestors(StringBuffer buffer) {
		if (isExternal())
			return;
		super.toStringAncestors(buffer);
	}

	@Override
	public int getKind() {
		return IProjectFragment.K_SOURCE;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof BuiltinProjectFragment) {
			BuiltinProjectFragment other = (BuiltinProjectFragment) o;
			return this.fPath.equals(other.fPath);
		}
		return false;
	}

	@Override
	public String getElementName() {
		return fPath.toOSString().replace(File.separatorChar,
				JEM_SKIP_DELIMETER);
	}

	@Override
	public void getHandleMemento(StringBuffer buff) {
		((ModelElement) getParent()).getHandleMemento(buff);
		buff.append(getHandleMementoDelimiter());
		escapeMementoName(buff, getElementName());
	}

	@Override
	public IModelElement getHandleFromMemento(String token,
			MementoTokenizer memento, WorkingCopyOwner owner) {
		switch (token.charAt(0)) {
		case JEM_SCRIPTFOLDER:
			String pkgName;
			if (memento.hasMoreTokens()) {
				pkgName = memento.nextToken();
				char firstChar = pkgName.charAt(0);
				if (firstChar == JEM_SOURCEMODULE || firstChar == JEM_COUNT) {
					token = pkgName;
					pkgName = IProjectFragment.DEFAULT_SCRIPT_FOLDER_NAME;
				} else {
					token = null;
				}
			} else {
				pkgName = IScriptFolder.DEFAULT_FOLDER_NAME;
				token = null;
			}
			ModelElement pkg = (ModelElement) getScriptFolder(pkgName);
			if (token == null) {
				return pkg.getHandleFromMemento(memento, owner);
			} else {
				return pkg.getHandleFromMemento(token, memento, owner);
			}
		}
		return null;
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return JEM_PROJECTFRAGMENT;
	}

	@Override
	public IBuildpathEntry getRawBuildpathEntry() throws ModelException {
		IBuildpathEntry rawEntry = null;
		ScriptProject project = (ScriptProject) this.getScriptProject();
		project.getResolvedBuildpath(); // force the reverse rawEntry cache to
		// be populated
		Map<IPath, IBuildpathEntry> rootPathToRawEntries = project
				.getPerProjectInfo().rootPathToRawEntries;
		if (rootPathToRawEntries != null) {
			rawEntry = rootPathToRawEntries.get(this.getPath());
		}
		if (rawEntry == null) {
			throw new ModelException(new ModelStatus(
					IModelStatusConstants.ELEMENT_NOT_ON_BUILDPATH, this));
		}
		return rawEntry;
	}

	@Override
	public IBuildpathEntry getBuildpathEntry() throws ModelException {
		ScriptProject project = (ScriptProject) this.getScriptProject();
		IBuildpathEntry rawEntry = getRawBuildpathEntry();
		// try to guest map from internal element.
		if (rawEntry != null
				&& rawEntry.getEntryKind() == IBuildpathEntry.BPE_CONTAINER) {
			IBuildpathContainer container = DLTKCore
					.getBuildpathContainer(rawEntry.getPath(), project);
			IBuildpathEntry entrys[] = container.getBuildpathEntries();
			for (int i = 0; i < entrys.length; ++i) {
				if (entrys[i].getPath()
						.equals(new Path(this.getPath().segment(0)))) {
					return entrys[i];
				}
			}
		}
		return rawEntry;
	}

	/*
	 * Validate whether this project fragment is on the buildpath of its
	 * project.
	 */
	@Override
	protected IStatus validateOnBuildpath() {
		return Status.OK_STATUS;
	}

	@Override
	public boolean exists() {
		return true;
	}

	public long lastModified() {
		return builtinProvider != null ? builtinProvider.lastModified() : 0;
	}
}
