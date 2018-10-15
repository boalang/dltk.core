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
package org.eclipse.dltk.internal.testing.model;

public interface ITestRunnerClient {

	/**
	 * Answers if this client is still running.
	 */
	boolean isRunning();

	/**
	 * 
	 */
	void stopTest();

	/**
	 * Advises this client to stop waiting for additional events after the test
	 * run was completed. Is called by the {@link TestRunSession} when the
	 * corresponding ILaunch is terminated.
	 */
	void stopWaiting();

	/**
	 * @param testId
	 * @param className
	 * @param testName
	 */
	void rerunTest(String testId, String className, String testName);

	/**
	 * @param listeners
	 */
	void startListening(ITestRunListener2 listener);

}
