/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.console.ui;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ScriptConsoleUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.dltk.console.ui"; //$NON-NLS-1$

	// The shared instance
	private static ScriptConsoleUIPlugin plugin;

	/**
	 * The constructor
	 */
	public ScriptConsoleUIPlugin() {
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		launchManager.addLaunchListener(ScriptConsoleManager.getInstance());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);

		ScriptConsoleManager manager = ScriptConsoleManager.getInstance();

		DebugPlugin.getDefault().getLaunchManager()
				.removeLaunchListener(manager);
		manager.closeAll();
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ScriptConsoleUIPlugin getDefault() {
		return plugin;
	}

	private static final String[][] IMAGES = new String[][] {
			{ "icons/elcl16/save.gif", //$NON-NLS-1$
					ScriptConsoleUIConstants.SAVE_SESSION_ICON },
			{ "icons/elcl16/terminate-red-square.gif", //$NON-NLS-1$
					ScriptConsoleUIConstants.TERMINATE_ICON } };

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		for (int i = 0; i < IMAGES.length; ++i) {
			URL url = getDefault().getBundle().getEntry(IMAGES[i][0]);
			registry.put(IMAGES[i][1], ImageDescriptor.createFromURL(url));
		}
	}

	public ImageDescriptor getImageDescriptor(String key) {
		return getImageRegistry().getDescriptor(key);
	}

	public Image getImage(String key) {
		return getImageRegistry().get(key);
	}

	public static void error(String msg, Throwable e) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}
}
