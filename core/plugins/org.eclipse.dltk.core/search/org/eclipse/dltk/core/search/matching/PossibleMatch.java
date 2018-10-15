/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.core.search.matching;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.compiler.env.ISourceModule;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.search.SearchDocument;
import org.eclipse.dltk.internal.core.Openable;
import org.eclipse.dltk.internal.core.SourceModule;
import org.eclipse.dltk.internal.core.search.matching.MatchingNodeSet;

public class PossibleMatch implements ISourceModule {
	public static final String NO_SOURCE_FILE_NAME = "NO SOURCE FILE NAME"; //$NON-NLS-1$
	public IResource resource;
	public Openable openable;
	public MatchingNodeSet nodeSet;
	public char[][] compoundName;
	public ModuleDeclaration parsedUnit;
	public SearchDocument document;
	private String source;

	public PossibleMatch(MatchLocator locator, IResource resource,
			org.eclipse.dltk.core.ISourceModule openable,
			SearchDocument document) {
		this(locator, resource, (Openable) openable, document);
	}

	public PossibleMatch(MatchLocator locator, IResource resource,
			Openable openable, SearchDocument document) {
		this.resource = resource;
		this.openable = openable;
		this.document = document;
		this.nodeSet = new MatchingNodeSet();
		char[] qualifiedName = getQualifiedName();
		if (qualifiedName != null)
			this.compoundName = CharOperation.splitOn('.', qualifiedName);
	}

	public void cleanUp() {
		this.source = null;
		if (this.parsedUnit != null) {
			// this.parsedUnit.cleanUp();
			this.parsedUnit = null;
		}
		this.nodeSet = null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.compoundName == null)
			return super.equals(obj);
		if (!(obj instanceof PossibleMatch))
			return false;
		// By using the compoundName of the source file, multiple .class files
		// (A, A$M...) are considered equal
		// Even .class files for secondary types and their nested types
		return CharOperation.equals(this.compoundName,
				((PossibleMatch) obj).compoundName);
	}

	@Override
	public String getSourceContents() {
		if (this.source != null)
			return this.source;
		return this.source = this.document.getContents();
	}

	@Override
	public char[] getContentsAsCharArray() {
		return getSourceContents().toCharArray();
	}

	/**
	 * The exact openable file name. In particular, will be the originating
	 * .class file for binary openable with attached source.
	 * 
	 */
	@Override
	public String getFileName() {
		return this.openable.getElementName();
	}

	public char[] getMainTypeName() {
		// The file is no longer opened to get its name => remove fix for bug
		// 32182
		return this.compoundName[this.compoundName.length - 1];
	}

	public char[][] getPackageName() {
		int length = this.compoundName.length;
		if (length <= 1)
			return CharOperation.NO_CHAR_CHAR;
		return CharOperation.subarray(this.compoundName, 0, length - 1);
	}

	/*
	 * Returns the fully qualified name of the main type of the compilation unit
	 * or the main type of the .java file that defined the class file.
	 */
	private char[] getQualifiedName() {
		if (this.openable instanceof SourceModule) {
			// get file name
			String fileName = this.openable.getElementName(); // working copy
			// on a .class
			// file may not
			// have a
			// resource, so
			// use the
			// element name
			// get main type name
			String mainTypeName = fileName;
			SourceModule cu = (SourceModule) this.openable;
			String fqName = cu.getType(mainTypeName).getFullyQualifiedName();
			if (fqName != null) {
				// can be null on errors
				return fqName.toCharArray();
			}
		}
		if (DLTKCore.DEBUG) {
			System.err.println("TODO: Code review here..."); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public int hashCode() {
		if (this.compoundName == null)
			return super.hashCode();
		int hashCode = 0;
		for (int i = 0, length = this.compoundName.length; i < length; i++)
			hashCode += CharOperation.hashCode(this.compoundName[i]);
		return hashCode;
	}

	@Override
	public String toString() {
		return this.openable == null ? "Fake PossibleMatch" : this.openable.toString(); //$NON-NLS-1$
	}

	@Override
	public IModelElement getModelElement() {
		return openable;
	}

}
