/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
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
package org.eclipse.dltk.core.builder;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IBuildParticipantExtension {

	int FULL_BUILD = IBuildContext.FULL_BUILD;
	int INCREMENTAL_BUILD = IBuildContext.INCREMENTAL_BUILD;
	int RECONCILE_BUILD = IBuildContext.RECONCILE_BUILD;

	/**
	 * Notifies the build participant about starting of the build operation.
	 * Returns <code>true</code> if participant should be called during build or
	 * <code>false</code> if this participant should be skipped.
	 * 
	 * @param buildType
	 * @return
	 */
	boolean beginBuild(int buildType);

	void endBuild(IProgressMonitor monitor);

}
