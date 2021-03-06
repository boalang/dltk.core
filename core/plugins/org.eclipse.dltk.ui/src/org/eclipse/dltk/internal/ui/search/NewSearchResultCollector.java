/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.search;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.search.FieldReferenceMatch;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchRequestor;


public class NewSearchResultCollector extends SearchRequestor {
	private DLTKSearchResult fSearch;
	private boolean fIgnorePotentials;

	public NewSearchResultCollector(DLTKSearchResult search, boolean ignorePotentials) {
		super();
		fSearch= search;
		fIgnorePotentials= ignorePotentials;
	}

	@Override
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		IModelElement enclosingElement= (IModelElement) match.getElement();
		if (enclosingElement != null) {
			if (fIgnorePotentials && (match.getAccuracy() == SearchMatch.A_INACCURATE))
				return;
			boolean isWriteAccess= false;
			boolean isReadAccess= false;
			if (match instanceof FieldReferenceMatch) {
				FieldReferenceMatch fieldRef= ((FieldReferenceMatch)match);
				isWriteAccess= fieldRef.isWriteAccess();
				isReadAccess= fieldRef.isReadAccess();
			}
			fSearch.addMatch(new DLTKElementMatch(enclosingElement, match.getRule(), match.getOffset(), match.getLength(), match.getAccuracy(), isReadAccess, isWriteAccess, match.isInsideDocComment()));
		}
	}

	@Override
	public void beginReporting() {
	}

	@Override
	public void endReporting() {
	}

	@Override
	public void enterParticipant(SearchParticipant participant) {
	}

	@Override
	public void exitParticipant(SearchParticipant participant) {
	}


}
