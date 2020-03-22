/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *

 *******************************************************************************/
package org.eclipse.dltk.internal.core.search.matching;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.index.EntryResult;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.internal.compiler.env.AccessRuleSet;
import org.eclipse.dltk.internal.core.search.DLTKSearchScope;
import org.eclipse.dltk.internal.core.search.IndexQueryRequestor;

/**
 * Internal search pattern implementation
 */
public abstract class InternalSearchPattern {

	/**
	 * The focus element (used for reference patterns)
	 */
	public IModelElement focus;

	public int kind;

	public void acceptMatch(String relativePath, String containerPath, char separator, SearchPattern pattern,
			IndexQueryRequestor requestor, SearchParticipant participant, IDLTKSearchScope scope) {

		if (scope instanceof DLTKSearchScope) {
			DLTKSearchScope javaSearchScope = (DLTKSearchScope) scope;
			// Get document path access restriction from script search scope
			// Note that requestor has to verify if needed whether the document
			// violates the access restriction or not
			AccessRuleSet access = javaSearchScope.getAccessRuleSet(relativePath, containerPath);
			if (access != DLTKSearchScope.NOT_ENCLOSED) { // scope encloses
				// the document path
				String documentPath = documentPath(containerPath, separator, relativePath);
				if (!requestor.acceptIndexMatch(documentPath, pattern, participant, access))
					throw new OperationCanceledException();
			}
		} else {
			String documentPath = documentPath(containerPath, separator, relativePath);
			if (scope.encloses(documentPath))
				if (!requestor.acceptIndexMatch(documentPath, pattern, participant, null))
					throw new OperationCanceledException();

		}
	}

	public SearchPattern currentPattern() {
		return (SearchPattern) this;
	}

	public String documentPath(String containerPath, char separator, String relativePath) {
		StringBuilder buffer = new StringBuilder(containerPath.length() + 1 + relativePath.length());
		buffer.append(containerPath);
		buffer.append(separator);
		buffer.append(relativePath);
		return buffer.toString();
	}

	/**
	 * Query a given index for matching entries. Assumes the sender has opened the
	 * index and will close when finished.
	 */
	public void findIndexMatches(Index index, IndexQueryRequestor requestor, SearchParticipant participant,
			IDLTKSearchScope scope, IProgressMonitor monitor) throws IOException {
		if (participant.isSkipped(index)) {
			return;
		}
		if (monitor != null && monitor.isCanceled())
			throw new OperationCanceledException();
		try {
			index.startQuery();
			SearchPattern pattern = currentPattern();
			EntryResult[] entries = ((InternalSearchPattern) pattern).queryIn(index);
			if (entries == null)
				return;

			final SearchPattern decodedResult = pattern.getBlankPattern();
			final String containerPath = index.getContainerPath();
			final char separator = index.separator;
			for (int i = 0, l = entries.length; i < l; i++) {
				if (monitor != null && monitor.isCanceled())
					throw new OperationCanceledException();

				EntryResult entry = entries[i];
				decodedResult.decodeIndexKey(entry.getWord());
				if (pattern.matchesDecodedKey(decodedResult)) {
					// TODO (kent) some clients may not need the document names
					String[] names = entry.getDocumentNames(index);
					for (int j = 0, n = names.length; j < n; j++)
						acceptMatch(names[j], containerPath, separator, decodedResult, requestor, participant, scope);
				}
			}
		} finally {
			index.stopQuery();
		}
	}

	public boolean isPolymorphicSearch() {
		return false;
	}

	public EntryResult[] queryIn(Index index) throws IOException {
		SearchPattern pattern = (SearchPattern) this;
		return index.query(pattern.getIndexCategories(), pattern.getIndexKey(), pattern.getMatchRule());
	}
}
