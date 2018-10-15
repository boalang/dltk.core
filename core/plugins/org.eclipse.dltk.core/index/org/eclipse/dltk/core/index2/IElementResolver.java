/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
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

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;

/**
 * Element resolver restores DLTK model element from index entry.
 * 
 * @author michael
 * @since 2.0
 * 
 */
public interface IElementResolver {

	/**
	 * Resolves model element from the index entry
	 * 
	 * @param elementType
	 *            Element type
	 * @param flags
	 *            Element modifiers
	 * @param offset
	 *            Element offset
	 * @param length
	 *            Element length
	 * @param nameOffset
	 *            Element name offset
	 * @param nameLength
	 *            Element name length
	 * @param elementName
	 *            Element name
	 * @param metadata
	 *            Various metadata attached to the element
	 * @param doc
	 *            DOC information (for declarations)
	 * @param qualifier
	 *            Element qualifier (package name, for example)
	 * @param parent
	 *            Element parent (declaring type, for example)
	 * @param sourceModule
	 *            Source module where this element is declared
	 * @since 3.0
	 */
	public IModelElement resolve(int elementType, int flags, int offset,
			int length, int nameOffset, int nameLength, String elementName,
			String metadata, String doc, String qualifier, String parent,
			ISourceModule sourceModule);
}
