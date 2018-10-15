/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.search;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.search.indexing.InternalSearchDocument;

/**
 * A search document encapsulates a content to be either indexed or searched in.
 * A search particpant creates a search document.
 * <p>
 * This class is intended to be subclassed by clients.
 * </p>
 * 
 * 
 */
public abstract class SearchDocument extends InternalSearchDocument {
	private String documentPath;
	private SearchParticipant participant;
	private IProject project;

	// public IPath fullPath;

	/**
	 * Creates a new search document. The given document path is a string that
	 * uniquely identifies the document. Most of the time it is a
	 * workspace-relative path, but it can also be a file system path, or a path
	 * inside a zip file.
	 * 
	 * @param documentPath
	 *            the path to the document, or <code>null</code> if none
	 * @param participant
	 *            the participant that creates the search document
	 */
	protected SearchDocument(String documentPath,
			SearchParticipant participant, IProject project) {
		this.documentPath = documentPath;
		this.participant = participant;
		this.project = project;
	}

	/**
	 * Returns the contents of this document. Contents may be different from
	 * actual resource at corresponding document path, in case of preprocessing.
	 * <p>
	 * This method must be implemented in subclasses.
	 * </p>
	 * <p>
	 * Note: some implementation may choose to cache the contents directly on
	 * the document for performance reason. However, this could induce
	 * scalability issues due to the fact that collections of documents are
	 * manipulated throughout the search operation, and cached contents would
	 * then consume lots of memory until they are all released at once in the
	 * end.
	 * </p>
	 * 
	 * @return the contents of this document, or <code>null</code> if none
	 */
	// public abstract byte[] getByteContents();
	/**
	 * Returns the contents of this document. Contents may be different from
	 * actual resource at corresponding document path due to preprocessing.
	 * <p>
	 * This method must be implemented in subclasses.
	 * </p>
	 * <p>
	 * Note: some implementation may choose to cache the contents directly on
	 * the document for performance reason. However, this could induce
	 * scalability issues due to the fact that collections of documents are
	 * manipulated throughout the search operation, and cached contents would
	 * then consume lots of memory until they are all released at once in the
	 * end.
	 * </p>
	 * 
	 * @return the contents of this document, or <code>null</code> if none
	 */
	public abstract char[] getCharContents();

	public String getContents() {
		char[] contents = getCharContents();
		if (contents == null) {
			return ""; //$NON-NLS-1$
		}
		String ret = new String(contents);
		return ret;
	}

	/**
	 * Returns the encoding for this document.
	 * <p>
	 * This method must be implemented in subclasses.
	 * </p>
	 * 
	 * @return the encoding for this document, or <code>null</code> if none
	 */
	public abstract String getEncoding();

	/**
	 * Returns the participant that created this document.
	 * 
	 * @return the participant that created this document
	 */
	public final SearchParticipant getParticipant() {
		return this.participant;
	}

	/**
	 * Returns the path to the original document to publicly mention in index or
	 * search results. This path is a string that uniquely identifies the
	 * document. Most of the time it is a workspace-relative path, but it can
	 * also be a file system path, or a path inside a zip file.
	 * 
	 * @return the path to the document
	 */
	@Override
	public final String getPath() {
		return this.documentPath;
	}

	public abstract boolean isExternal();

	public IProject getProject() {
		return this.project;
	}
}
