/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search.matching;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.references.Reference;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.matching.MatchLocator;
import org.eclipse.dltk.core.search.matching.PatternLocator;

//TODO REVIEW IT
public class FieldLocator extends PatternLocator {
	
	@Override
	public int match(FieldDeclaration node, MatchingNodeSet nodeSet) {		
		if (!this.pattern.findDeclarations)
			return IMPOSSIBLE_MATCH;
		// Verify field name
		if (!matchesName(this.pattern.name, node.getName().toCharArray()))
		return nodeSet.addMatch(node, IMPOSSIBLE_MATCH);
		else return nodeSet.addMatch(node, ACCURATE_MATCH);
		
	}
	protected FieldPattern pattern;

	public FieldLocator(FieldPattern pattern) {
		super(pattern);
		this.pattern = pattern;
	}

	/*
	 * Clear caches
	 */
	@Override
	protected void clear() {
	}

	@Override
	public void initializePolymorphicSearch(MatchLocator locator) {
	}

	@Override
	public int match(ASTNode node, MatchingNodeSet nodeSet) {
//		int declarationsLevel = IMPOSSIBLE_MATCH;
		if (this.pattern.findReferences) {
//			if (node instanceof ImportReference) {
//				// With static import, we can have static method reference in
//				// import reference
//				ImportReference importRef = (ImportReference) node;
//				int length = importRef.tokens.length - 1;
//				if (importRef.isStatic() && !importRef.onDemand && matchesName(this.pattern.selector, importRef.tokens[length])) {
//					char[][] compoundName = new char[length][];
//					System.arraycopy(importRef.tokens, 0, compoundName, 0, length);
//					char[] declaringType = CharOperation.concat(pattern.declaringQualification, pattern.declaringSimpleName, '.');
//					if (matchesName(declaringType, CharOperation.concatWith(compoundName, '.'))) {
//						declarationsLevel = ((InternalSearchPattern) this.pattern).mustResolve ? POSSIBLE_MATCH : ACCURATE_MATCH;
//					}
//				}
//			}
		}
//		return nodeSet.addMatch(node, declarationsLevel);
		return IMPOSSIBLE_MATCH;
	}

	
	// public int match(TypeDeclaration node, MatchingNodeSet nodeSet) - SKIP IT
	// public int match(TypeReference node, MatchingNodeSet nodeSet) - SKIP IT
	@Override
	public int matchContainer() {
		if (this.pattern.findReferences) {
			// need to look almost everywhere to find in javadocs and static
			// import
			return ALL_CONTAINER;
		}
		return COMPILATION_UNIT_CONTAINER | CLASS_CONTAINER | METHOD_CONTAINER;
	}

	@Override
	public SearchMatch newDeclarationMatch(ASTNode reference, IModelElement element, int accuracy, 
			MatchLocator locator) {		
		return super.newDeclarationMatch(reference, element, accuracy, locator);
	}

	@Override
	protected int referenceType() {
		return IModelElement.FIELD;
	}

	@Override
	public String toString() {
		return "Locator for " + this.pattern.toString(); //$NON-NLS-1$
	}
	@Override
	public int match(Reference node, MatchingNodeSet nodeSet) { // interested in NameReference & its subtypes
		if (!this.pattern.findReferences) return IMPOSSIBLE_MATCH;
		if (!(node instanceof SimpleReference)) return IMPOSSIBLE_MATCH;	
		if (this.pattern.name == null)
			return nodeSet.addMatch(node, POSSIBLE_MATCH);
		
		if (matchesName(this.pattern.name, ((SimpleReference) node).getName().toCharArray()))
			return nodeSet.addMatch(node, ACCURATE_MATCH);		
		return IMPOSSIBLE_MATCH;
	}	
}
