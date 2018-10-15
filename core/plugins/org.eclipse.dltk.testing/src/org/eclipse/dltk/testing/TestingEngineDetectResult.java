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

import org.eclipse.core.runtime.IStatus;

public class TestingEngineDetectResult {

	private final ITestingEngine engine;
	private final IStatus status;

	/**
	 * @param engine
	 * @param status
	 */
	public TestingEngineDetectResult(ITestingEngine engine, IStatus status) {
		this.engine = engine;
		this.status = status;
	}

	/**
	 * @return the engine
	 */
	public ITestingEngine getEngine() {
		return engine;
	}

	/**
	 * @return the status
	 */
	public IStatus getStatus() {
		return status;
	}

}
