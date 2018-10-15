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

import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.matching.MatchLocator;
import org.eclipse.dltk.core.search.matching.PatternLocator;

public class MethodDeclarationLocator extends PatternLocator {
	protected MethodDeclarationPattern pattern;

	public MethodDeclarationLocator(MethodDeclarationPattern pattern) {
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
	public int match(MethodDeclaration node, MatchingNodeSet nodeSet) {
		// Verify method name
		if (!matchesName(this.pattern.simpleName, node.getName().toCharArray()))
			return IMPOSSIBLE_MATCH;

		// Verify parameters types
		List arguments = node.getArguments();
		if (this.pattern.parameterNames != null) {
			int length = this.pattern.parameterNames.length;
			int argsLength = arguments == null ? 0 : arguments.size();
			if (length != argsLength)
				return IMPOSSIBLE_MATCH;
		}

		// check type names
		String declaringType = node.getDeclaringTypeName();
		if (!checkTypeName(declaringType)) {
			return INACCURATE_MATCH;
		}

		// Method declaration may match pattern
		return nodeSet.addMatch(node, ACCURATE_MATCH);
	}

	// public int match(TypeDeclaration node, MatchingNodeSet nodeSet) - SKIP IT
	// public int match(TypeReference node, MatchingNodeSet nodeSet) - SKIP IT
	@Override
	public int matchContainer() {
		return COMPILATION_UNIT_CONTAINER | CLASS_CONTAINER | METHOD_CONTAINER;
	}

	@Override
	public SearchMatch newDeclarationMatch(ASTNode reference,
			IModelElement element, int accuracy, MatchLocator locator) {
		return super.newDeclarationMatch(reference, element, accuracy, locator);
	}

	@Override
	protected int referenceType() {
		return IModelElement.METHOD;
	}

	@Override
	public String toString() {
		return "Locator for " + this.pattern.toString(); //$NON-NLS-1$
	}

	@Override
	public int match(CallExpression node, MatchingNodeSet nodeSet) {
		// this locator matches only declarations
		return IMPOSSIBLE_MATCH;
	}

	@Override
	protected void matchReportReference(ASTNode reference,
			IModelElement element, int accuracy, MatchLocator locator) {
		// Since this is the declaration locator - it is not interested in
		// references.
	}

	/**
	 * Tests if the specified {@code declaringType} matches the search pattern.
	 * Returns <code>true</code> if matches and <code>false</code> otherwise.
	 * 
	 * @param declaringType
	 * @return
	 */
	private boolean checkTypeName(String declaringType) {
		if (this.pattern.enclosingTypeNames != null
				&& this.pattern.enclosingTypeNames.length > 0) {
			if (declaringType != null) {
				ISearchPatternProcessor processor = DLTKLanguageManager
						.getSearchPatternProcessor(this.pattern.getToolkit());
				char[] delimeter = processor != null ? processor
						.getDelimiterReplacementString().toCharArray()
						: new char[] { '.' };
				char[] typeName = this.pattern.enclosingTypeNames[0];
				if (typeName == null) {
					return true;
				}
				for (int i = 1; i < this.pattern.enclosingTypeNames.length; ++i) {
					typeName = CharOperation.concatWithSeparator(typeName,
							this.pattern.enclosingTypeNames[i], delimeter);
				}
				if (!matchesName(typeName, declaringType.toCharArray())) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}
}
