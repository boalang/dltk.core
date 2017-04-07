/*******************************************************************************
 * Copyright (c) 2009, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.core.index.sql;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IShutdownListener;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SqlIndex extends Plugin {

	public static final String PLUGIN_ID = "org.eclipse.dltk.core.index.sql"; //$NON-NLS-1$

	public static final boolean DEBUG = Boolean
			.valueOf(Platform.getDebugOption(PLUGIN_ID + "/debug")) //$NON-NLS-1$
			.booleanValue();

	private static SqlIndex plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	private static final ListenerList<IShutdownListener> shutdownListeners = new ListenerList<>();

	public static void addShutdownListener(IShutdownListener listener) {
		shutdownListeners.add(listener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		for (IShutdownListener listener : shutdownListeners) {
			listener.shutdown();
		}
		shutdownListeners.clear();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SqlIndex getDefault() {
		return plugin;
	}

	public static void error(String message) {
		plugin.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK,
				message, null));
	}

	public static void error(String message, Throwable t) {
		plugin.getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, t));
	}

	public static void warn(String message) {
		warn(message, null);
	}

	public static void warn(String message, Throwable t) {
		plugin.getLog().log(
				new Status(IStatus.WARNING, PLUGIN_ID, IStatus.OK, message, t));
	}
}
