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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.references.Reference;
import org.eclipse.dltk.ast.references.TypeReference;
import org.eclipse.dltk.compiler.env.lookup.Scope;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.matching.MatchLocator;
import org.eclipse.dltk.core.search.matching.PatternLocator;

public class OrLocator extends PatternLocator {

	protected PatternLocator[] patternLocators;

	public OrLocator(OrPattern pattern) {
		super(pattern);

		SearchPattern[] patterns = pattern.patterns;
		int length = patterns.length;
		this.patternLocators = new PatternLocator[length];
		for (int i = 0; i < length; i++)
			this.patternLocators[i] = PatternLocator
					.patternLocator(patterns[i], null);
	}

	@Override
	public void initializePolymorphicSearch(MatchLocator locator) {
		for (int i = 0, length = this.patternLocators.length; i < length; i++)
			this.patternLocators[i].initializePolymorphicSearch(locator);
	}

	@Override
	public int match(ASTNode node, MatchingNodeSet nodeSet) {
		int level = IMPOSSIBLE_MATCH;
		for (int i = 0, length = this.patternLocators.length; i < length; i++) {
			int newLevel = this.patternLocators[i].match(node, nodeSet);
			if (newLevel > level) {
				if (newLevel == ACCURATE_MATCH)
					return ACCURATE_MATCH;
				level = newLevel;
			}
		}
		return level;
	}

	@Override
	public int match(Expression node, MatchingNodeSet nodeSet) {
		int level = IMPOSSIBLE_MATCH;
		for (int i = 0, length = this.patternLocators.length; i < length; i++) {
			int newLevel = this.patternLocators[i].match(node, nodeSet);
			if (newLevel > level) {
				if (newLevel == ACCURATE_MATCH)
					return ACCURATE_MATCH;
				level = newLevel;
			}
		}
		return level;
	}

	@Override
	public int match(FieldDeclaration node, MatchingNodeSet nodeSet) {
		int level = IMPOSSIBLE_MATCH;
		for (int i = 0, length = this.patternLocators.length; i < length; i++) {
			int newLevel = this.patternLocators[i].match(node, nodeSet);
			if (newLevel > level) {
				if (newLevel == ACCURATE_MATCH)
					return ACCURATE_MATCH;
				level = newLevel;
			}
		}
		return level;
	}

	@Override
	public int match(MethodDeclaration node, MatchingNodeSet nodeSet) {
		int level = IMPOSSIBLE_MATCH;
		for (int i = 0, length = this.patternLocators.length; i < length; i++) {
			int newLevel = this.patternLocators[i].match(node, nodeSet);
			if (newLevel > level) {
				if (newLevel == ACCURATE_MATCH)
					return ACCURATE_MATCH;
				level = newLevel;
			}
		}
		return level;
	}

	@Override
	public int match(Reference node, MatchingNodeSet nodeSet) {
		int level = IMPOSSIBLE_MATCH;
		for (int i = 0, length = this.patternLocators.length; i < length; i++) {
			int newLevel = this.patternLocators[i].match(node, nodeSet);
			if (newLevel > level) {
				if (newLevel == ACCURATE_MATCH)
					return ACCURATE_MATCH;
				level = newLevel;
			}
		}
		return level;
	}

	@Override
	public int match(TypeDeclaration node, MatchingNodeSet nodeSet) {
		int level = IMPOSSIBLE_MATCH;
		for (int i = 0, length = this.patternLocators.length; i < length; i++) {
			int newLevel = this.patternLocators[i].match(node, nodeSet);
			if (newLevel > level) {
				if (newLevel == ACCURATE_MATCH)
					return ACCURATE_MATCH;
				level = newLevel;
			}
		}
		return level;
	}

	@Override
	public int match(TypeReference node, MatchingNodeSet nodeSet) {
		int level = IMPOSSIBLE_MATCH;
		for (int i = 0, length = this.patternLocators.length; i < length; i++) {
			int newLevel = this.patternLocators[i].match(node, nodeSet);
			if (newLevel > level) {
				if (newLevel == ACCURATE_MATCH)
					return ACCURATE_MATCH;
				level = newLevel;
			}
		}
		return level;
	}
	
	@Override
	public int match(CallExpression node, MatchingNodeSet nodeSet) {
		int level = IMPOSSIBLE_MATCH;
		for (int i = 0, length = this.patternLocators.length; i < length; i++) {
			int newLevel = this.patternLocators[i].match(node, nodeSet);
			if (newLevel > level) {
				if (newLevel == ACCURATE_MATCH)
					return ACCURATE_MATCH;
				level = newLevel;
			}
		}
		return level;
	}

	@Override
	public void matchReportReference(ASTNode reference, IModelElement element,
			Scope scope, int accuracy, MatchLocator locator)
			throws CoreException {
		for (int i = 0, length = this.patternLocators.length; i < length; i++) {
			this.patternLocators[i].matchReportReference(reference, element,
					scope, accuracy, locator);
		}
	}

	@Override
	public int matchContainer() {
		int result = 0;
		for (int i = 0, length = this.patternLocators.length; i < length; i++)
			result |= this.patternLocators[i].matchContainer();
		return result;
	}


	@Override
	public SearchMatch newDeclarationMatch(ASTNode reference,
			IModelElement element, int accuracy,
			MatchLocator locator) {
		PatternLocator closestPattern = null;
		int level = ACCURATE_MATCH;
		for (int i = 0, pl = this.patternLocators.length; i < pl; i++) {
			PatternLocator patternLocator = this.patternLocators[i];
			int newLevel = patternLocator.resolveLevel(reference);
			if (newLevel > level) {
				closestPattern = patternLocator;
				if (newLevel == ACCURATE_MATCH)
					break;
				level = newLevel;
			}
		}
		if (closestPattern != null) {
			return closestPattern.newDeclarationMatch(reference, element,
					accuracy, locator);
		}
		// super implementation...
		return locator.newDeclarationMatch(element, accuracy,
				reference.matchStart(), reference.matchLength());
	}

	@Override
	public int resolveLevel(ASTNode node) {
		int level = IMPOSSIBLE_MATCH;
		for (int i = 0, length = this.patternLocators.length; i < length; i++) {
			int newLevel = this.patternLocators[i].resolveLevel(node);
			if (newLevel > level) {
				if (newLevel == ACCURATE_MATCH)
					return ACCURATE_MATCH;
				level = newLevel; // want to answer the stronger match
			}
		}
		return level;
	}
}
