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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class DLTKSearchContentProvider implements IStructuredContentProvider {
	protected final Object[] EMPTY_ARR= new Object[0];
	protected DLTKSearchResult fResult;
	private DLTKSearchResultPage fPage;

	DLTKSearchContentProvider(DLTKSearchResultPage page) {
		fPage= page;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		initialize((DLTKSearchResult) newInput);
	}

	protected void initialize(DLTKSearchResult result) {
		fResult= result;
	}

	public abstract void elementsChanged(Object[] updatedElements);
	public abstract void clear();

	@Override
	public void dispose() {
		// nothing to do
	}

	DLTKSearchResultPage getPage() {
		return fPage;
	}

}
