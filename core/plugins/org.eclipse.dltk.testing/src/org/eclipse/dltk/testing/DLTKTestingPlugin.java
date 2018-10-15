/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *   Julien Ruaux: jruaux@octo.com
 * 	 Vincent Massol: vmassol@octo.com
 *     David Saff (saff@mit.edu) - bug 102632: [JUnit] Support for JUnit 4.
 *******************************************************************************/

package org.eclipse.dltk.testing;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.internal.testing.model.DLTKTestingModel;
import org.eclipse.dltk.testing.model.ITestRunSession;
import org.eclipse.dltk.testing.model.ITestingModel;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * The plug-in runtime class for the JUnit plug-in.
 */
public class DLTKTestingPlugin extends AbstractUIPlugin {

	/**
	 * The single instance of this plug-in runtime class.
	 */
	private static DLTKTestingPlugin fgPlugin = null;

	public static final String PLUGIN_ID = "org.eclipse.dltk.testing"; //$NON-NLS-1$

	/**
	 * The class path variable referring to the junit home location
	 */
	public final static String JUNIT_HOME = "JUNIT_HOME"; //$NON-NLS-1$

	private static final IPath ICONS_PATH = new Path("$nl$/icons/full"); //$NON-NLS-1$
	private static final String HISTORY_DIR_NAME = "history"; //$NON-NLS-1$

	private final DLTKTestingModel fTestingModel = new DLTKTestingModel();

	/**
	 * List storing the registered test run listeners
	 */
	private ListenerList<TestRunListener> fNewTestRunListeners;

	private BundleContext fBundleContext;

	private static boolean fIsStopped = false;

	public DLTKTestingPlugin() {
		fgPlugin = this;
		fNewTestRunListeners = new ListenerList<>();
	}

	public static DLTKTestingPlugin getDefault() {
		return fgPlugin;
	}

	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow workBenchWindow = getActiveWorkbenchWindow();
		if (workBenchWindow == null)
			return null;
		return workBenchWindow.getShell();
	}

	/**
	 * Returns the active workbench window
	 *
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		if (fgPlugin == null)
			return null;
		IWorkbench workBench = fgPlugin.getWorkbench();
		if (workBench == null)
			return null;
		return workBench.getActiveWorkbenchWindow();
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null)
			return null;
		return activeWorkbenchWindow.getActivePage();
	}

	public static String getPluginId() {
		return PLUGIN_ID;
	}

	public static void log(Throwable e) {
		log("Error", e); //$NON-NLS-1$
	}

	public static void log(String message, Throwable e) {
		log(new Status(IStatus.ERROR, getPluginId(), IStatus.ERROR, message,
				e));
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static ImageDescriptor getImageDescriptor(String relativePath) {
		IPath path = ICONS_PATH.append(relativePath);
		return createImageDescriptor(getDefault().getBundle(), path, true);
	}

	/**
	 * Sets the three image descriptors for enabled, disabled, and hovered to an
	 * action. The actions are retrieved from the *lcl16 folders.
	 *
	 * @param action
	 *            the action
	 * @param iconName
	 *            the icon name
	 */
	public static void setLocalImageDescriptors(IAction action,
			String iconName) {
		setImageDescriptors(action, "lcl16", iconName); //$NON-NLS-1$
	}

	private static void setImageDescriptors(IAction action, String type,
			String relPath) {
		ImageDescriptor id = createImageDescriptor("d" + type, relPath, false); //$NON-NLS-1$
		if (id != null)
			action.setDisabledImageDescriptor(id);

		ImageDescriptor descriptor = createImageDescriptor("e" + type, relPath, //$NON-NLS-1$
				true);
		action.setHoverImageDescriptor(descriptor);
		action.setImageDescriptor(descriptor);
	}

	/*
	 * Creates an image descriptor for the given prefix and name in the JDT UI
	 * bundle. The path can contain variables like $NL$. If no image could be
	 * found, <code>useMissingImageDescriptor</code> decides if either the
	 * 'missing image descriptor' is returned or <code>null</code>. or
	 * <code>null</code>.
	 */
	private static ImageDescriptor createImageDescriptor(String pathPrefix,
			String imageName, boolean useMissingImageDescriptor) {
		IPath path = ICONS_PATH.append(pathPrefix).append(imageName);
		return createImageDescriptor(DLTKTestingPlugin.getDefault().getBundle(),
				path, useMissingImageDescriptor);
	}

	/**
	 * Creates an image descriptor for the given path in a bundle. The path can
	 * contain variables like $NL$. If no image could be found,
	 * <code>useMissingImageDescriptor</code> decides if either the 'missing
	 * image descriptor' is returned or <code>null</code>.
	 *
	 * @param bundle
	 * @param path
	 * @param useMissingImageDescriptor
	 * @return an {@link ImageDescriptor}, or <code>null</code> iff there's no
	 *         image at the given location and
	 *         <code>useMissingImageDescriptor</code> is <code>true</code>
	 */
	private static ImageDescriptor createImageDescriptor(Bundle bundle,
			IPath path, boolean useMissingImageDescriptor) {
		URL url = FileLocator.find(bundle, path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		if (useMissingImageDescriptor) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
		return null;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		fBundleContext = context;
		fTestingModel.start();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		fIsStopped = true;
		try {
			fTestingModel.stop();
		} finally {
			super.stop(context);
		}
		fBundleContext = null;
	}

	public static ITestingModel getModel() {
		return getDefault().fTestingModel;
	}

	/**
	 * Returns the bundle for a given bundle name, regardless whether the bundle
	 * is resolved or not.
	 *
	 * @param bundleName
	 *            the bundle name
	 * @return the bundle
	 * @since 3.2
	 */
	public Bundle getBundle(String bundleName) {
		Bundle[] bundles = getBundles(bundleName, null);
		if (bundles != null && bundles.length > 0)
			return bundles[0];
		return null;
	}

	/**
	 * Returns the bundles for a given bundle name,
	 *
	 * @param bundleName
	 *            the bundle name
	 * @return the bundles of the given name
	 */
	public Bundle[] getBundles(String bundleName, String version) {
		Bundle[] bundles = Platform.getBundles(bundleName, version);
		if (bundles != null)
			return bundles;

		// Accessing unresolved bundle
		ServiceReference<PackageAdmin> serviceRef = fBundleContext
				.getServiceReference(PackageAdmin.class);
		PackageAdmin admin = fBundleContext.getService(serviceRef);
		bundles = admin.getBundles(bundleName, version);
		if (bundles != null && bundles.length > 0)
			return bundles;
		return null;
	}

	/**
	 * @return a <code>ListenerList</code> of all <code>TestRunListener</code>s
	 */
	public ListenerList<TestRunListener> getNewTestRunListeners() {
		return fNewTestRunListeners;
	}

	public static boolean isStopped() {
		return fIsStopped;
	}

	public IDialogSettings getDialogSettingsSection(String name) {
		IDialogSettings dialogSettings = getDialogSettings();
		IDialogSettings section = dialogSettings.getSection(name);
		if (section == null) {
			section = dialogSettings.addNewSection(name);
		}
		return section;
	}

	public static File getHistoryDirectory() throws IllegalStateException {
		File historyDir = getDefault().getStateLocation()
				.append(HISTORY_DIR_NAME).toFile();
		if (!historyDir.isDirectory()) {
			historyDir.mkdir();
		}
		return historyDir;
	}

	public static ITestRunSession getTestRunSession(ILaunch launch) {
		return getModel().getTestRunSession(launch);
	}

	public static IViewPart showTestRunnerViewPartInActivePage() {
		return getDefault().fTestingModel.showTestRunnerViewPartInActivePage();
	}
}
