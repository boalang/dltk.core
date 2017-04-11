/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core.internal.environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.RuntimePerformanceMonitor;
import org.eclipse.dltk.core.RuntimePerformanceMonitor.PerformanceNode;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.FileHandles;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.environment.IFileStoreProvider;

public class EFSFileHandle implements IFileHandle, IFileStoreProvider {

	private static Map<String, Long> timestamps = new HashMap<>();
	private static Map<String, Long> lastaccess = new HashMap<>();

	private IFileStore file;
	private IEnvironment environment;

	public EFSFileHandle(IEnvironment env, IFileStore file) {
		this.environment = env;
		this.file = file;
	}

	@Override
	public boolean exists() {
		try {
			return file.fetchInfo().exists();
		} catch (RuntimeException e) {
			return false;
		}
	}

	@Override
	public String toOSString() {
		return this.environment.convertPathToString(getPath());
	}

	@Override
	public String getCanonicalPath() {
		return this.environment.getCanonicalPath(getPath());
	}

	@Override
	public IFileHandle getChild(final String childname) {
		return new EFSFileHandle(environment,
				file.getFileStore(new Path(childname)));
	}

	@Override
	public IFileHandle[] getChildren() {
		try {
			IFileStore[] files = file.childStores(EFS.NONE, null);
			IFileHandle[] children = new IFileHandle[files.length];
			for (int i = 0; i < files.length; i++)
				children[i] = new EFSFileHandle(environment, files[i]);
			return children;
		} catch (CoreException e) {
			if (DLTKCore.DEBUG)
				e.printStackTrace();
			return null;
		}
	}

	@Override
	public IEnvironment getEnvironment() {
		return environment;
	}

	@Override
	public URI toURI() {
		return file.toURI();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public IFileHandle getParent() {
		IFileStore parent = file.getParent();
		if (parent == null)
			return null;
		return new EFSFileHandle(environment, parent);
	}

	@Override
	public IPath getPath() {
		// Try to get the path from the existing java.io.File object, which is
		// twice faster than the file.toURI()
		try {
			File localFile = file.toLocalFile(EFS.NONE, null);
			if (localFile != null) {
				return new Path(localFile.getPath());
			}
		} catch (CoreException e) {
		}
		return new Path(file.toURI().getPath());
	}

	@Override
	public boolean isDirectory() {
		return file.fetchInfo().isDirectory();
	}

	@Override
	public boolean isFile() {
		final IFileInfo info = file.fetchInfo();
		return info.exists() && !info.isDirectory();
	}

	@Override
	public boolean isSymlink() {
		return file.fetchInfo().getAttribute(EFS.ATTRIBUTE_SYMLINK);
	}

	@Override
	public InputStream openInputStream(IProgressMonitor monitor)
			throws IOException {
		try {
			return file.openInputStream(EFS.NONE, monitor);
		} catch (CoreException e) {
			if (DLTKCore.DEBUG)
				e.printStackTrace();
			throw new IOException(e.getLocalizedMessage());
		}
	}

	@Override
	public OutputStream openOutputStream(IProgressMonitor monitor)
			throws IOException {
		try {
			return file.openOutputStream(EFS.NONE, monitor);
		} catch (CoreException e) {
			if (DLTKCore.DEBUG)
				e.printStackTrace();
			throw new IOException(e.getLocalizedMessage());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EFSFileHandle) {
			EFSFileHandle anotherFile = (EFSFileHandle) obj;
			return this.file.equals(anotherFile.file);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public String toString() {
		return toOSString();
	}

	@Override
	public long lastModified() {
		String n = toString();
		long c = 0;
		boolean flag = !environment.isLocal();
		if (flag) {
			if (timestamps.containsKey(n)) {
				c = System.currentTimeMillis();
				Long last = lastaccess.get(n);
				if (last != null
						&& (c - last.longValue()) < 1000 * 60 * 60 * 24) {
					return timestamps.get(n);
				}
			}
		}
		PerformanceNode p = RuntimePerformanceMonitor.begin();
		long lm = file.fetchInfo().getLastModified();
		if (flag) {
			timestamps.put(n, lm);
			if (c == 0) {
				c = System.currentTimeMillis();
			}
			lastaccess.put(n, c);
		}
		p.done("#", "Return file timestamp", 0);
		return lm;

	}

	// public long lastModifiedFromCache() {
	// if (this.environment.isLocal()) {
	// return file.fetchInfo().getLastModified();
	// }
	// // Try to restore timestamp from local cache.
	// IContentCache coreCache = ModelManager.getModelManager().getCoreCache();
	// File asFile = coreCache.getEntryAsFile(this, "timestamp");
	// if (asFile.exists()) {
	// try {
	// DataInputStream dis = new DataInputStream(new FileInputStream(
	// asFile));
	// long timestamp = dis.readLong();
	// dis.close();
	// return timestamp;
	// } catch (IOException e) {
	// }
	// }
	// long lastModified = file.fetchInfo().getLastModified();
	// coreCache.setCacheEntryAttribute(this, "timestamp", lastModified);
	// return lastModified;
	// }

	@Override
	public long length() {
		return file.fetchInfo().getLength();
	}

	@Override
	public IPath getFullPath() {
		return EnvironmentPathUtils.getFullPath(environment, getPath());
	}

	@Override
	public String getEnvironmentId() {
		return environment.getId();
	}

	/**
	 * @since 2.0
	 */
	@Override
	public IFileStore getFileStore() {
		return this.file;
	}

	@Override
	public void move(IFileHandle destination) throws CoreException {
		final IFileStore destStore = FileHandles.asFileStore(destination);
		file.move(destStore, EFS.OVERWRITE, null);
	}
}
