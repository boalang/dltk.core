/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IBuildpathEntry;

/**
 * @since 2.0
 */
public interface IBuildpathDetector {

	/**
	 * Executes buildpath detection
	 * 
	 * @param subProgressMonitor
	 * @throws CoreException
	 */
	void detectBuildpath(IProgressMonitor monitor) throws CoreException;

	/**
	 * Returns the detection results
	 * 
	 * @return
	 */
	IBuildpathEntry[] getBuildpath();

}
