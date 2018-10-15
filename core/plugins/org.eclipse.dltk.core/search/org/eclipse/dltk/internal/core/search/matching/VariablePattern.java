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

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;

public abstract class VariablePattern extends DLTKSearchPattern {

	public boolean findDeclarations;
	public boolean findReferences;
	protected boolean readAccess;
	protected boolean writeAccess;

	public char[] name;

	public VariablePattern(int patternKind, boolean findDeclarations,
			boolean readAccess, boolean writeAccess, char[] name,
			int matchRule, IDLTKLanguageToolkit toolkit) {
		super(patternKind, matchRule, toolkit);

		this.findDeclarations = findDeclarations; // set to find declarations
		// & all occurences
		this.readAccess = readAccess; // set to find any reference, read only
		// references & all occurences
		this.writeAccess = writeAccess; // set to find any reference, write only
		// references & all occurences
		this.findReferences = readAccess || writeAccess;

		this.name = (isCaseSensitive() || isCamelCase()) ? name : CharOperation
				.toLowerCase(name);
	}

	/*
	 * Returns whether a method declaration or message send will need to be
	 * resolved to find out if this method pattern matches it.
	 */
	protected boolean mustResolve() {
		// would like to change this so that we only do it if generic references
		// are found
		return this.findReferences; // always resolve (in case of a simple name
		// reference being a potential match)
	}
}
