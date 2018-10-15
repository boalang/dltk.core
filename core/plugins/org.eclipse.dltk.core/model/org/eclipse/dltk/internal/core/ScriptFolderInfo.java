/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ModelException;

public class ScriptFolderInfo extends OpenableElementInfo {
	private Object[] foreignResources;

	void setForeignResources(Object[] resources) {
		foreignResources = resources;
	}

	public Object[] getForeignResources(IResource resource,
			IProjectFragment projectFragment) {
		if (this.foreignResources == null) {
			try {
				char[][] inclusionPatterns = null;
				if (projectFragment instanceof ProjectFragment) {
					inclusionPatterns = ((ProjectFragment) projectFragment)
							.fullInclusionPatternChars();
				}
				char[][] exclusionPatterns = null;
				if (projectFragment instanceof ProjectFragment) {
					exclusionPatterns = ((ProjectFragment) projectFragment)
							.fullExclusionPatternChars();
				}
				this.foreignResources = ProjectFragmentInfo
						.computeFolderForeignResources(
								(ScriptProject) projectFragment
										.getScriptProject(),
								(IContainer) resource, inclusionPatterns,
								exclusionPatterns);
			} catch (ModelException e) {
				// root doesn't exist: consider package has no
				// nonScriptResources
				this.foreignResources = NO_NON_SCRIPT_RESOURCES;
			}
		}
		return this.foreignResources;
	}

	public boolean containsScriptResources() {
		return size() != 0;
	}
}
