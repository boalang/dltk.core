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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.BuildpathContainerInitializer;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IScriptProject;

public class TestContainer2Initializer extends BuildpathContainerInitializer {
	@Override
	public void initialize(IPath containerPath, IScriptProject project)
			throws CoreException {
		DLTKCore.setBuildpathContainer(containerPath,
				new IScriptProject[] { project },
				new IBuildpathContainer[] { new TestContainer2() }, null);
	}
}
