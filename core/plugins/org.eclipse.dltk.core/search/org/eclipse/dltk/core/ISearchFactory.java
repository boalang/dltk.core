/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core;

import org.eclipse.dltk.core.search.DLTKSearchParticipant;
import org.eclipse.dltk.core.search.IMatchLocatorParser;
import org.eclipse.dltk.core.search.indexing.SourceIndexerRequestor;
import org.eclipse.dltk.core.search.matching.MatchLocator;

public interface ISearchFactory {

	IMatchLocatorParser createMatchParser(MatchLocator locator); // to ext point

	SourceIndexerRequestor createSourceRequestor(); // to ext point

	// Is this method really need?
	DLTKSearchParticipant createSearchParticipant(); // to ext point

	ISearchPatternProcessor createSearchPatternProcessor();
}
