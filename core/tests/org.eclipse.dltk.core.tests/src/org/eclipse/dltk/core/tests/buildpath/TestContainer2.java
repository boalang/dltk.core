/*******************************************************************************
 * Copyright (c) 2013 NumberFour AG
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.tests.buildpath;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;

class TestContainer2 implements IBuildpathContainer {

	static final IPath CONTAINER_ID = new Path(TestContainer2.class.getName());

	private final IBuildpathEntry[] entries;

	public TestContainer2() {
		entries = new IBuildpathEntry[0];
	}

	public TestContainer2(File folder) {
		entries = new IBuildpathEntry[] { DLTKCore
				.newExtLibraryEntry(EnvironmentPathUtils.getFullPath(folder)) };
	}

	@Override
	public IBuildpathEntry[] getBuildpathEntries() {
		return entries;
	}

	@Override
	public String getDescription() {
		return getClass().getSimpleName();
	}

	@Override
	public int getKind() {
		return K_APPLICATION;
	}

	@Override
	public IPath getPath() {
		return CONTAINER_ID;
	}

}
