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
package org.eclipse.dltk.launching;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IDebugTarget;

public interface ILaunchStatusHandler {

	/**
	 * @param target
	 * @param monitor
	 */
	void initialize(IDebugTarget target, IProgressMonitor monitor);

	/**
	 * @param elapsedTime
	 */
	void updateElapsedTime(long elapsedTime);

	/**
	 * 
	 */
	void dispose();

}
