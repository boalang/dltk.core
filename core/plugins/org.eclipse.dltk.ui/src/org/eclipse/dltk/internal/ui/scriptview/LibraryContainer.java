/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.scriptview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.navigator.ProjectFragmentContainer;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.resource.ImageDescriptor;

public class LibraryContainer extends ProjectFragmentContainer {

	public LibraryContainer(IScriptProject project) {
		super(project);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LibraryContainer) {
			LibraryContainer other = (LibraryContainer) obj;
			return getScriptProject().equals(other.getScriptProject());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getScriptProject().hashCode();
	}

	@Override
	public IAdaptable[] getChildren() {
		return getProjectFragments();
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return DLTKPluginImages.DESC_OBJS_LIBRARY;
	}

	@Override
	public String getLabel() {
		return ScriptMessages.LibraryContainer_libraries;
	}

	@Override
	public IProjectFragment[] getProjectFragments() {
		List<IProjectFragment> list = new ArrayList<>();
		try {
			IProjectFragment[] roots = getScriptProject().getProjectFragments();
			for (int i = 0; i < roots.length; i++) {
				IProjectFragment root = roots[i];
				IBuildpathEntry rawBuildpathEntry = root.getRawBuildpathEntry();
				if (rawBuildpathEntry != null) {
					int classpathEntryKind = rawBuildpathEntry.getEntryKind();
					if (classpathEntryKind == IBuildpathEntry.BPE_LIBRARY
					/* || classpathEntryKind == IBuildpathEntry.BPE_VARIABLE */) {
						list.add(root);
					}
				}
			}
		} catch (ModelException e) {
			// fall through
		}
		return list.toArray(new IProjectFragment[list.size()]);
	}
}
