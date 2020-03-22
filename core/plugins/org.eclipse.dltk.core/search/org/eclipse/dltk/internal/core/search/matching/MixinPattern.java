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

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.indexing.IIndexConstants;

public class MixinPattern extends DLTKSearchPattern implements IIndexConstants {
	protected char[] mixinKey;

	public static char[] createIndexKey(char[] fieldName) {
		return fieldName;
	}

	public MixinPattern(char[] key, int matchRule, IDLTKLanguageToolkit toolkit) {
		super(FIELD_PATTERN, matchRule, toolkit);
		this.mixinKey = key;
	}

	@Override
	public void decodeIndexKey(char[] key) {
		this.mixinKey = key;
	}

	@Override
	public SearchPattern getBlankPattern() {
		return new MixinPattern(null, R_EXACT_MATCH | R_CASE_SENSITIVE, getToolkit());
	}

	@Override
	public char[] getIndexKey() {
		return this.mixinKey;
	}

	@Override
	public char[][] getIndexCategories() {
		return new char[][] { MIXIN };
	}

	@Override
	public boolean matchesDecodedKey(SearchPattern decodedPattern) {
		return true; // index key is not encoded so query results all match
	}

	@Override
	protected StringBuilder print(StringBuilder output) {

		if (mixinKey == null) {
			output.append("*"); //$NON-NLS-1$
		} else {
			output.append(mixinKey);
		}
		return super.print(output);
	}
}
