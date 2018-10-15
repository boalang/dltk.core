/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.testing;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.internal.testing.util.TestingConsoleListener;

/**
 * Class for accessing JUnit support; all functionality is provided by static
 * methods.
 * <p>
 * This class is not intended to be subclassed or instantiated by clients.
 * </p>
 * 
 * @since 2.1
 */
public class DLTKTestingCore {

	/**
	 * Adds a listener for test runs.
	 * 
	 * @param listener
	 *            the listener to be added
	 * @since 3.3
	 */
	public static void addTestRunListener(TestRunListener listener) {
		DLTKTestingPlugin.getDefault().getNewTestRunListeners().add(listener);
	}

	/**
	 * Removes a listener for test runs.
	 * 
	 * @param listener
	 *            the listener to be removed
	 * @since 3.3
	 */
	public static void removeTestRunListener(TestRunListener listener) {
		DLTKTestingPlugin.getDefault().getNewTestRunListeners()
				.remove(listener);
	}

	private static int index = 0;

	public static void registerTestingProcessor(final ILaunch launch,
			ITestingProcessor processor) {
		final String launchKey = "#" + Integer.toString(++index); //$NON-NLS-1$
		launch.setAttribute(DLTKTestingConstants.LAUNCH_ATTR_KEY, launchKey);
		final TestingConsoleListener listener = new TestingConsoleListener(
				launchKey, launch, processor);
		listener.install();
		// DebugPlugin.getDefault().getLaunchManager().addLaunchListener(new
		// TestingOutputListener(launch, processor));
	}
}
