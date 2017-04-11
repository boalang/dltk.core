/*******************************************************************************
 * Copyright (c) 2016, 2017 xored software, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core.caching;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.RuntimePerformanceMonitor;
import org.eclipse.dltk.core.RuntimePerformanceMonitor.PerformanceNode;
import org.eclipse.dltk.core.caching.cache.CacheEntry;
import org.eclipse.dltk.core.caching.cache.CacheEntryAttribute;
import org.eclipse.dltk.core.caching.cache.CacheIndex;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

/**
 * This cache provider checks for folder .index files and load such files into
 * required cache.
 *
 * @author Andrei Sobolev
 */
public class ArchiveContentCacheProvider implements IContentCacheProvider {
	private static final String CHECK_TIMEOUT = ".dltk.core.archive.cache.lastaccess";
	private IContentCache cache;
	private static final long CACHE_UPDATE_TIMEOUT = 1000 * 60 * 60; // One hour

	public ArchiveContentCacheProvider() {
	}

	@Override
	public InputStream getAttributeAndUpdateCache(IFileHandle handle,
			String attribute) {
		if (handle == null) {
			return null;
		}
		IFileHandle parent = handle.getParent();
		if (parent == null) {
			return null;
		}
		String DLTK_INDEX_FILE = ".dltk.index";
		// Check for additional indexes
		if (processIndexFile(handle, attribute, parent,
				parent.getChild(DLTK_INDEX_FILE), cache)) {
			return cache.getCacheEntryAttribute(handle, attribute, true);
		}
		long lastAccess = cache.getCacheEntryAttributeLong(parent,
				CHECK_TIMEOUT, true);
		if (lastAccess + CACHE_UPDATE_TIMEOUT > System.currentTimeMillis()) {
			return null; // no entry at all
		}
		IFileHandle[] children = parent.getChildren();
		if (children != null) {
			for (IFileHandle fileHandle : children) {
				String fileName = fileHandle.getName();
				if (fileName.startsWith(DLTK_INDEX_FILE)
						&& !fileName.equals(DLTK_INDEX_FILE)) {
					if (processIndexFile(handle, attribute, parent, fileHandle,
							cache)) {
						return cache.getCacheEntryAttribute(handle, attribute);
					}
				}
			}
		}
		cache.setCacheEntryAttribute(parent, CHECK_TIMEOUT,
				System.currentTimeMillis());
		return null;
	}

	public static void processFolderIndexes(IFileHandle folder,
			IContentCache cache, IProgressMonitor monitor) {
		// cache.get
		String DLTK_INDEX_FILE = ".dltk.index";
		// Check for additional indexes
		IFileHandle[] children = folder.getChildren();
		if (children != null) {
			List<IFileHandle> indexFiles = new ArrayList<>();
			for (IFileHandle fileHandle : children) {
				String fileName = fileHandle.getName();
				if (fileName.startsWith(DLTK_INDEX_FILE)
						&& !fileName.equals(DLTK_INDEX_FILE)) {
					indexFiles.add(fileHandle);
				}
			}
			SubProgressMonitor processingIndexes = new SubProgressMonitor(
					monitor, 1);
			processingIndexes.beginTask("Processing index files",
					indexFiles.size());
			for (IFileHandle fileHandle : indexFiles) {
				processingIndexes
						.subTask("Processing:" + fileHandle.toOSString());
				processIndexFile(null, null, folder, fileHandle, cache);
				processingIndexes.worked(1);
			}
			processingIndexes.done();
		}
	}

	private static boolean processIndexFile(IFileHandle handle,
			String attribute, IFileHandle parent, IFileHandle indexFile,
			IContentCache cache) {
		if (indexFile != null && indexFile.exists()) {
			String stamp = cache.getCacheEntryAttributeString(indexFile,
					"timestamp", true);
			String fStamp = Long.toString(indexFile.lastModified() / 1000);
			if (stamp != null) {
				if (fStamp.equals(stamp)) {
					return false;
				}
			}
			try {
				boolean found = processIndexFile(handle, attribute, parent,
						indexFile, fStamp, cache);
				return found;
			} catch (IOException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static boolean processIndexFile(IFileHandle handle, String attribute,
			IFileHandle parent, IFileHandle indexFile, String fStamp,
			IContentCache cache) throws IOException, ZipException {
		File zipFileHandle = cache.getEntryAsFile(indexFile, "handle");

		if (!zipFileHandle.exists()) {
			BufferedInputStream inp = new BufferedInputStream(
					indexFile.openInputStream(new NullProgressMonitor()), 4096);
			PerformanceNode p = RuntimePerformanceMonitor.begin();
			Util.copy(zipFileHandle, inp);
			inp.close();
			p.done("#", "Indexes read", zipFileHandle.length(),
					indexFile.getEnvironment());
		}
		ZipFile zipFile = new ZipFile(zipFileHandle);

		ZipEntry entry = zipFile.getEntry(".index");
		Resource indexResource = new XMIResourceImpl(
				URI.createURI("dltk_cache://zipIndex"));
		indexResource.load(
				new BufferedInputStream(zipFile.getInputStream(entry), 8096),
				null);
		EList<EObject> contents = indexResource.getContents();
		boolean found = false;
		for (EObject eObject : contents) {
			CacheIndex cacheIndex = (CacheIndex) eObject;
			EList<CacheEntry> entries = cacheIndex.getEntries();
			for (CacheEntry cacheEntry : entries) {
				String path = cacheEntry.getPath();
				IFileHandle entryHandle = new WrapTimeStampHandle(
						parent.getChild(path), cacheEntry.getTimestamp());
				// cache.setCacheEntryAttribute(entryHandle, "timestamp",
				// cacheEntry.getTimestamp());
				EList<CacheEntryAttribute> attributes = cacheEntry
						.getAttributes();
				for (CacheEntryAttribute cacheEntryAttribute : attributes) {
					if (handle != null && attribute != null) {
						if (attribute.equals(cacheEntryAttribute.getName())
								&& cacheEntry.getPath()
										.equals(handle.getName())) {
							found = true;
						}
					}
					OutputStream stream = null;
					stream = cache.getCacheEntryAttributeOutputStream(
							entryHandle, cacheEntryAttribute.getName());
					String location = cacheEntryAttribute.getLocation();
					ZipEntry zipEntry = zipFile.getEntry(location);
					InputStream inputStream;
					try {
						inputStream = new BufferedInputStream(
								zipFile.getInputStream(zipEntry), 8096);
						Util.copy(inputStream, stream);
						stream.close();
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		cache.setCacheEntryAttribute(indexFile, "timestamp", fStamp);
		return found;
	}

	@Override
	public void setCache(IContentCache cache) {
		this.cache = cache;
	}
}
