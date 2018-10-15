/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.testing.launcher.NullTestingEngine;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;

/**
 * Attribute keys used by the IJUnitLaunchConfiguration. Note that these
 * constants are not API and might change in the future.
 */
public class DLTKTestingConstants {

	public static final String MODE_RUN_QUIETLY_MODE = "runQuietly"; //$NON-NLS-1$
	//public static final String ID_JUNIT_APPLICATION = "org.eclipse.jdt.junit.launchconfig"; //$NON-NLS-1$

	public static final String ATTR_NO_DISPLAY = DLTKTestingPlugin.PLUGIN_ID
			+ ".NO_DISPLAY"; //$NON-NLS-1$

	public static final String ATTR_PORT = DLTKTestingPlugin.PLUGIN_ID
			+ ".PORT"; //$NON-NLS-1$

	/**
	 * The test method, or "" iff running the whole test type.
	 */
	public static final String ATTR_TEST_METHOD_NAME = DLTKTestingPlugin.PLUGIN_ID
			+ ".TESTNAME"; //$NON-NLS-1$

	public static final String ATTR_KEEPRUNNING = DLTKTestingPlugin.PLUGIN_ID
			+ ".KEEPRUNNING_ATTR"; //$NON-NLS-1$
	/**
	 * The launch container, or "" iff running a single test type.
	 */
	public static final String ATTR_TEST_CONTAINER = DLTKTestingPlugin.PLUGIN_ID
			+ ".CONTAINER"; //$NON-NLS-1$

	public static final String ATTR_FAILURES_NAMES = DLTKTestingPlugin.PLUGIN_ID
			+ ".FAILURENAMES"; //$NON-NLS-1$

	public static ITestingEngine getTestingEngine(
			ILaunchConfiguration launchConfiguration) {
		try {
			String loaderId = launchConfiguration.getAttribute(
					DLTKTestingConstants.ATTR_ENGINE_ID, (String) null);
			if (loaderId != null) {
				final ITestingEngine engine = TestingEngineManager
						.getEngine(loaderId);
				if (engine != null) {
					return engine;
				}
			}
		} catch (CoreException e) {
		}
		return NullTestingEngine.getInstance();
	}

	public static IScriptProject getScriptProject(
			ILaunchConfiguration configuration) {
		try {
			String projectName = configuration.getAttribute(
					ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					(String) null);
			if (projectName != null && projectName.length() > 0) {
				return DLTKCore.create(ResourcesPlugin.getWorkspace().getRoot()
						.getProject(projectName));
			}
		} catch (CoreException e) {
		}
		return null;
	}

	public final static String ATTR_ENGINE_ID = DLTKTestingPlugin.PLUGIN_ID
			+ ".engineId"; //$NON-NLS-1$

	/**
	 * This attribute is assigned to the launch to uniquely identify it and
	 * connect console with the testing engine.
	 */
	public static final String LAUNCH_ATTR_KEY = DLTKTestingPlugin.PLUGIN_ID
			+ ".LAUNCH_KEY"; //$NON-NLS-1$

}
