/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
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
package org.eclipse.dltk.core.search.matching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchDocument;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;

public interface IMatchLocator {

	void initialize(SearchPattern pattern, IDLTKSearchScope scope);

	void setRequestor(SearchRequestor requestor);

	void setProgressMonitor(IProgressMonitor progressMonitor);

	void locateMatches(SearchDocument[] searchDocuments) throws CoreException;

}
