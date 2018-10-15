/*******************************************************************************
 * Copyright (c) 2008, 2016 xored software, Inc.
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
package org.eclipse.dltk.core.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;
import org.eclipse.osgi.util.NLS;

/**
 * {@link File} as {@link IFileHandle} wrapper
 */
public class FileAsFileHandle implements IFileHandle {

	private final IEnvironment environment;
	private final File file;

	public FileAsFileHandle(File file) {
		this(LocalEnvironment.getInstance(), file);
	}

	public FileAsFileHandle(IEnvironment environment, File file) {
		this.environment = environment;
		this.file = file;
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	@Override
	public String getCanonicalPath() {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			return file.getPath();
		}
	}

	@Override
	public IFileHandle getChild(String path) {
		return new FileAsFileHandle(environment, new File(file, path));
	}

	@Override
	public IFileHandle[] getChildren() {
		final File[] children = file.listFiles();
		if (children != null) {
			IFileHandle[] result = new IFileHandle[children.length];
			for (int i = 0; i < children.length; ++i) {
				result[i] = new FileAsFileHandle(environment, children[i]);
			}
			return result;
		} else {
			return null;
		}
	}

	@Override
	public IEnvironment getEnvironment() {
		return environment;
	}

	@Override
	public String getEnvironmentId() {
		return environment.getId();
	}

	@Override
	public IPath getFullPath() {
		return EnvironmentPathUtils.getFullPath(environment, getPath());
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public IFileHandle getParent() {
		final File parentFile = file.getParentFile();
		if (parentFile != null) {
			return new FileAsFileHandle(environment, parentFile);
		} else {
			return null;
		}
	}

	@Override
	public IPath getPath() {
		return new Path(file.getPath());
	}

	@Override
	public boolean isDirectory() {
		return file.isDirectory();
	}

	@Override
	public boolean isFile() {
		return file.isFile();
	}

	@Override
	public boolean isSymlink() {
		return EFS.getLocalFileSystem().getStore(file.toURI()).fetchInfo()
				.getAttribute(EFS.ATTRIBUTE_SYMLINK);
	}

	@Override
	public long lastModified() {
		return file.lastModified();
	}

	@Override
	public long length() {
		return file.length();
	}

	@Override
	public InputStream openInputStream(IProgressMonitor monitor)
			throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public OutputStream openOutputStream(IProgressMonitor monitor)
			throws IOException {
		return new FileOutputStream(file);
	}

	@Override
	public String toOSString() {
		return file.getPath();
	}

	@Override
	public URI toURI() {
		return file.toURI();
	}

	@Override
	public void move(IFileHandle destination) throws CoreException {
		final File destFile = FileHandles.asFile(destination);
		if (!file.renameTo(destFile)) {
			throw new CoreException(new Status(IStatus.ERROR,
					DLTKCore.PLUGIN_ID, NLS.bind("Rename {0} to {1} failed", //$NON-NLS-1$
							file, destFile)));
		}
	}

	File getFile() {
		return file;
	}

	@Override
	public String toString() {
		return file.toString();
	}

}
