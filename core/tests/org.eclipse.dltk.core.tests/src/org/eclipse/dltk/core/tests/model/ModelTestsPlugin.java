/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.tests.model;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.dltk.core.tests.WorkspaceSetup;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ModelTestsPlugin extends Plugin {

	public static final String PLUGIN_NAME = "org.eclipse.dltk.core.tests";
	public static final String TEST_NATURE = "org.eclipse.dltk.core.tests.testnature";

	public static final WorkspaceSetup WORKSPACE = new WorkspaceSetup(
			PLUGIN_NAME);

	//The shared instance.
	private static ModelTestsPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public ModelTestsPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ModelTestsPlugin getDefault() {
		return plugin;
	}

}
