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
package org.eclipse.dltk.testing;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.testing.model.ITestElement;

/**
 * This interface is obtained as adapter from {@link ITestRunnerUI}.
 */
public interface ITestElementResolver extends IAdaptable {

	TestElementResolution resolveElement(ITestElement element);

}
