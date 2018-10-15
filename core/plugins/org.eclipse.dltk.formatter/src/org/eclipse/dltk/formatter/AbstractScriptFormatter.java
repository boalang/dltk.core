/*******************************************************************************
 * Copyright (c) 2008, 2016 xored software, Inc. and others.
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
package org.eclipse.dltk.formatter;

import java.util.Map;

import org.eclipse.dltk.formatter.internal.FormatterIndentGenerator;
import org.eclipse.dltk.formatter.internal.FormatterMixedIndentGenerator;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.formatter.IScriptFormatter;
import org.eclipse.jface.text.IDocument;

/**
 * Abstract base class for the {@link IScriptFormatter} implementations.
 */
public abstract class AbstractScriptFormatter implements IScriptFormatter {

	private final Map<String, ? extends Object> preferences;

	/**
	 * @param preferences
	 */
	protected AbstractScriptFormatter(Map<String, ? extends Object> preferences) {
		this.preferences = preferences;
	}

	protected boolean getBoolean(String key) {
		Object value = preferences.get(key);
		if (value != null) {
			if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue();
			}
			if (value instanceof Number) {
				return ((Number) value).intValue() != 0;
			}
			return Boolean.valueOf(value.toString()).booleanValue();
		}
		return false;
	}

	protected int getInt(String key) {
		return toInt(preferences.get(key));
	}

	private static int toInt(Object value) {
		if (value != null) {
			if (value instanceof Number) {
				return ((Number) value).intValue();
			}
			try {
				return Integer.parseInt(value.toString());
			} catch (NumberFormatException e) {
				// ignore
			}
		}
		return 0;
	}

	protected String getString(String key) {
		Object value = preferences.get(key);
		if (value != null) {
			return value.toString();
		}
		return null;
	}

	/**
	 * @since 2.0
	 */
	protected IFormatterIndentGenerator createIndentGenerator() {
		final int tabSize = getInt(CodeFormatterConstants.FORMATTER_TAB_SIZE);
		final int indentSize = getInt(CodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
		final String indentType = getString(CodeFormatterConstants.FORMATTER_TAB_CHAR);
		if (CodeFormatterConstants.SPACE.equals(indentType)) {
			return new FormatterIndentGenerator(' ', indentSize, tabSize);
		} else if (CodeFormatterConstants.MIXED.equals(indentType)) {
			return new FormatterMixedIndentGenerator(indentSize, tabSize);
		} else {
			return new FormatterIndentGenerator('\t', 1, tabSize);
		}
	}

	@Override
	public int detectIndentationLevel(IDocument document, int offset) {
		return 0;
	}

}
