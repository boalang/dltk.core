/*******************************************************************************
 * Copyright (c) 2012 NumberFour AG
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
package org.eclipse.dltk.testing;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.testing.model.ITestElement;

/**
 * Extension interface for {@link ITestRunnerUI}.
 * 
 * <p>
 * It introduces finer control over test reruns.
 * </p>
 */
public interface ITestRunnerUIExtension {

	/**
	 * @param testElement
	 * @param launchMode
	 * @return
	 */
	boolean canRerun(ITestElement testElement, String launchMode);

	/**
	 * Advises this client to rerun the specified test element.
	 */
	boolean rerunTest(ILaunch launch, ITestElement element, String launchMode)
			throws CoreException;

}
