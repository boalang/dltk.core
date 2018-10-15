/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core.internal.environment;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IEnvironmentProvider;

public class LocalEnvironmentProvider implements IEnvironmentProvider {

	/**
	 * @since 2.0
	 */
	public static final String FILE_SCHEME = "file"; //$NON-NLS-1$

	public LocalEnvironmentProvider() {
	}

	@Override
	public String getProviderName() {
		return LocalEnvironment.getInstance().getName();
	}

	@Override
	public IEnvironment getEnvironment(String envId) {
		if (LocalEnvironment.ENVIRONMENT_ID.equals(envId)) {
			return LocalEnvironment.getInstance();
		}
		return null;
	}

	@Override
	public IEnvironment[] getEnvironments() {
		return new IEnvironment[] { LocalEnvironment.getInstance() };
	}

	@Override
	public boolean isInitialized() {
		return true;
	}

	@Override
	public void waitInitialized() {
	}

	@Override
	public IEnvironment getProjectEnvironment(IProject project) {
		if (project.isAccessible()) {
			IPath location = project.getLocation();
			if (location != null) {
				File file = new File(location.makeAbsolute().toOSString());
				if (file.exists()) {
					return LocalEnvironment.getInstance();
				}
			}
		}
		return null;
	}

	@Override
	public IEnvironment getEnvironment(URI locationURI) {
		if (FILE_SCHEME.equals(locationURI.getScheme())) {
			return LocalEnvironment.getInstance();
		} else {
			return null;
		}
	}
}
