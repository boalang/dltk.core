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
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchRequestor;

/**
 * Collects the results returned by a <code>SearchEngine</code>. If a
 * {@link ReferencesInBinaryContext} is passed, matches that are inside a binary
 * element are not collected (but added to the context if they are accurate).
 */
public class CollectingSearchRequestor extends SearchRequestor {
	private final List<SearchMatch> fFound;
	private final List<SearchMatch> fBinaryRefs;

	public CollectingSearchRequestor() {
		this(false);
	}

	public CollectingSearchRequestor(boolean binaryRefs) {
		fFound = new ArrayList<>();
		fBinaryRefs = binaryRefs ? new ArrayList<>() : null;
	}

	/**
	 * The default implementation calls {@link #collectMatch(SearchMatch)} for all
	 * matches that make it through {@link #filterMatch(SearchMatch)}.
	 *
	 * @param match
	 *            the found match
	 * @throws CoreException
	 *
	 * @see org.eclipse.jdt.core.search.SearchRequestor#acceptSearchMatch(org.eclipse.jdt.core.search.SearchMatch)
	 */
	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		if (!filterMatch(match))
			collectMatch(match);
	}

	public void collectMatch(SearchMatch match) {
		fFound.add(match);
	}

	/**
	 * Returns whether the given match should be filtered out. The default
	 * implementation filters out matches in binaries iff
	 * {@link #CollectingSearchRequestor(ReferencesInBinaryContext)} has been called
	 * with a non-<code>null</code> argument. Accurate binary matches are added to
	 * the {@link ReferencesInBinaryContext}.
	 *
	 * @param match
	 *            the match to test
	 * @return <code>true</code> iff the given match should <em>not</em> be
	 *         collected
	 * @throws CoreException
	 */
	public boolean filterMatch(SearchMatch match) throws CoreException {
		if (fBinaryRefs == null)
			return false;

		if (match.getAccuracy() == SearchMatch.A_ACCURATE && isBinaryElement(match.getElement())) {
			// binary classpaths are often incomplete -> avoiding false positives from
			// inaccurate matches
			fBinaryRefs.add(match);
			return true;
		}

		return false;
	}

	private static boolean isBinaryElement(Object element) throws ModelException {
		if (element instanceof IMember) {
			return ((IMember) element).getSourceModule().isBinary();
		} else if (element instanceof ISourceModule) {
			return ((ISourceModule) element).isBinary();
		} else if (element instanceof IProjectFragment) {
			return ((IProjectFragment) element).isBinary();
		}
		return false;

	}

	/**
	 * @return a List of {@link SearchMatch}es (not sorted)
	 */
	public List<SearchMatch> getResults() {
		return fFound;
	}
}
