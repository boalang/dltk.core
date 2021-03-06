/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.annotations.Internal;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceModuleInfoCache;

/**
 * Used to cache some source module information. All information related to
 * source module are removed, then source module are changed.
 */
public class SourceModuleInfoCache implements ISourceModuleInfoCache,
		IResourceChangeListener, IResourceDeltaVisitor {
	@Internal
	final int capacity = ModelCache.DEFAULT_ROOT_SIZE * 50;

	private final ReferenceQueue<ISourceModuleInfo> queue = new ReferenceQueue<>();

	@SuppressWarnings("serial")
	private final Map<ISourceModule, CacheReference> map = new LinkedHashMap<ISourceModule, CacheReference>(
			16, 0.9f, true) {
		@Override
		protected boolean removeEldestEntry(
				Map.Entry<ISourceModule, CacheReference> eldest) {
			return size() > capacity;
		}
	};

	private static class CacheReference
			extends SoftReference<ISourceModuleInfo> {
		final long modificationStamp;
		final ISourceModule module;

		public CacheReference(ISourceModule module, ISourceModuleInfo referent,
				ReferenceQueue<? super ISourceModuleInfo> q) {
			super(referent, q);
			this.module = module;
			this.modificationStamp = getModificationStamp(module);
		}

		private static long getModificationStamp(ISourceModule module) {
			final IResource resource = module.getResource();
			return resource != null ? resource.getModificationStamp()
					: IResource.NULL_STAMP;
		}

		public boolean isValid(ISourceModule module) {
			final IResource resource = module.getResource();
			return resource == null
					|| resource.getModificationStamp() == modificationStamp;
		}

	}

	public void start() {
		DLTKCore.addPreProcessingResourceChangedListener(this,
				IResourceChangeEvent.POST_CHANGE);
	}

	public void stop() {
		DLTKCore.removePreProcessingResourceChangedListener(this);
	}

	private void expungeStaleEntries() {
		for (CacheReference r; (r = (CacheReference) queue.poll()) != null;) {
			if (DEBUG) {
				System.out.println(
						"[Cache] expunge " + r.module.getElementName());
			}
			map.remove(r.module);
		}
	}

	@Override
	public synchronized ISourceModuleInfo get(ISourceModule module) {
		expungeStaleEntries();
		final CacheReference ref = map.get(module);
		if (ref != null) {
			final ISourceModuleInfo info = ref.get();
			if (info != null && ref.isValid(module)) {
				return info;
			}
		}
		final ISourceModuleInfo info = new SourceModuleInfo();
		map.put(module, new CacheReference(module, info, queue));
		return info;
	}

	@Override
	public synchronized void resourceChanged(IResourceChangeEvent event) {
		expungeStaleEntries();
		final IResourceDelta delta = event.getDelta();
		try {
			delta.accept(this);
		} catch (CoreException e) {
			DLTKCore.error(e);
		}
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		final int kind = delta.getKind();
		if (kind == IResourceDelta.ADDED) {
			return false;
		}
		final IResource resource = delta.getResource();
		switch (kind) {
		case IResourceDelta.CHANGED:
			switch (resource.getType()) {
			case IResource.PROJECT:
				if ((delta.getFlags() & IResourceDelta.OPEN) != 0) {
					final IProject project = (IProject) resource;
					if (!project.isOpen()) {
						removeByProject(project);
						return false;
					}
				}
				return true;
			case IResource.FOLDER:
				return true;
			case IResource.FILE:
				if ((delta.getFlags() & IResourceDelta.CONTENT) != 0) {
					remove((IFile) resource);
				}
				break;
			}
			break;
		case IResourceDelta.REMOVED:
			switch (resource.getType()) {
			case IResource.PROJECT:
				removeByProject((IProject) resource);
				return false;
			case IResource.FOLDER:
				return true;
			case IResource.FILE:
				remove((IFile) resource);
				return false;
			}
		}
		return true;
	}

	@Internal
	static class SourceModuleInfo implements ISourceModuleInfo {
		private Map<Object, Object> map;

		@Override
		public synchronized Object get(String key) {
			if (map == null) {
				return null;
			}
			return map.get(key);
		}

		@Override
		public synchronized void put(String key, Object value) {
			if (map == null) {
				map = new HashMap<>();
			}
			map.put(key, value);
		}

		@Override
		public synchronized void remove(String key) {
			if (map != null) {
				map.remove(key);
			}
		}

		@Override
		public synchronized boolean isEmpty() {
			return this.map == null || this.map.isEmpty();
		}
	}

	/**
	 * Not synchronized here, as it's called only from
	 * {@link #resourceChanged(IResourceChangeEvent)} which is already
	 * synchronized.
	 */
	private void removeByProject(IProject project) {
		for (Iterator<ISourceModule> i = map.keySet().iterator(); i
				.hasNext();) {
			final ISourceModule module = i.next();
			if (project.equals(module.getScriptProject().getProject())) {
				i.remove();
			}
		}
	}

	public void remove(IFile file) {
		remove(DLTKCore.createSourceModuleFrom(file));
	}

	@Override
	public synchronized void remove(ISourceModule module) {
		if (DEBUG) {
			System.out.println("[Cache] remove " + module.getElementName()); //$NON-NLS-1$
		}
		map.remove(module);
	}

	private static final boolean DEBUG = false;

	@Override
	public synchronized void clear() {
		// clear out reference queue.
		while (queue.poll() != null)
			;
		map.clear();
	}

	@Override
	public synchronized int size() {
		return map.size();
	}

	@Override
	public int capacity() {
		return capacity;
	}

}
