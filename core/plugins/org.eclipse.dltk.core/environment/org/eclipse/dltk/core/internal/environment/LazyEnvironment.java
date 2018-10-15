/*******************************************************************************
 * Copyright (c) 2009, 2016 xored software, Inc.  
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
package org.eclipse.dltk.core.internal.environment;

import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;

/**
 * @since 2.0
 */
public abstract class LazyEnvironment implements IEnvironment {

	private static final boolean DEBUG = false;

	private final String environmentId;
	private IEnvironment environment;

	public LazyEnvironment(String environmentId) {
		this.environmentId = environmentId;
		if (DEBUG)
			System.out.println(getClass().getName() + " - created for " //$NON-NLS-1$
					+ environmentId);
	}

	private void initialize() {
		if (environment == null) {
			environment = resolveEnvironment(environmentId);
			if (DEBUG)
				if (environment != null)
					System.out
							.println(getClass().getName()
									+ " - resolved " + environmentId + " to " + environment.getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	protected abstract IEnvironment resolveEnvironment(String envId);

	@Override
	public boolean connect() {
		initialize();
		return environment != null && environment.connect();
	}

	@Override
	public String convertPathToString(IPath path) {
		initialize();
		return environment != null ? environment.convertPathToString(path)
				: path.toString();
	}

	@Override
	public String getCanonicalPath(IPath path) {
		initialize();
		return environment != null ? environment.getCanonicalPath(path) : path
				.toString();
	}

	@Override
	public IFileHandle getFile(IPath path) {
		return new LazyFileHandle(environmentId, path);
	}

	@Override
	public IFileHandle getFile(URI locationURI) {
		initialize();
		return environment != null ? environment.getFile(locationURI) : null;
	}

	@Override
	public String getId() {
		return environmentId;
	}

	@Override
	public String getName() {
		initialize();
		return environment != null ? environment.getName() : generateName();
	}

	private String generateName() {
		return getClass().getSimpleName() + '[' + environmentId + ']';
	}

	@Override
	public String getPathsSeparator() {
		initialize();
		return environment != null ? environment.getPathsSeparator() : ":"; //$NON-NLS-1$
	}

	@Override
	public char getPathsSeparatorChar() {
		initialize();
		return environment != null ? environment.getPathsSeparatorChar() : ':';
	}

	@Override
	public String getSeparator() {
		initialize();
		return environment != null ? environment.getSeparator() : "/"; //$NON-NLS-1$
	}

	@Override
	public char getSeparatorChar() {
		initialize();
		return environment != null ? environment.getSeparatorChar() : '/';
	}

	@Override
	public URI getURI(IPath location) {
		initialize();
		return environment != null ? environment.getURI(location) : null;
	}

	@Override
	public boolean isConnected() {
		initialize();
		return environment != null ? environment.isConnected() : false;
	}

	@Override
	public boolean isLocal() {
		initialize();
		return environment != null ? environment.isLocal() : false;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		initialize();
		return environment != null ? environment.getAdapter(adapter) : null;
	}

	@Override
	public int hashCode() {
		return environmentId.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IEnvironment) {
			IEnvironment other = (IEnvironment) obj;
			return environmentId.equals(other.getId());
		}
		return false;
	}

}
