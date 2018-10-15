/*******************************************************************************
 * Copyright (c) 2009, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.core.index2;

import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.core.index2.IndexerManager;

/**
 * @since 2.0
 */
public abstract class AbstractIndexer implements IIndexer, IIndexingRequestor {

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void indexDocument(ISourceModule sourceModule) {
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(sourceModule);
		if (toolkit == null) {
			return;
		}
		IIndexerParticipant participant = IndexerManager.getIndexerParticipant(
				this, toolkit.getNatureId());
		if (participant != null) {
			IIndexingParser parser = participant.getIndexingParser();
			if (parser != null) {
				parser.parseSourceModule(sourceModule, this);
			}
		}
	}
}
