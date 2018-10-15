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

import java.io.StringWriter;

public class FormatterTextNode extends AbstractFormatterNode implements
		IFormatterTextNode {

	private final int startOffset;
	private final int endOffset;

	/**
	 * @param text
	 */
	public FormatterTextNode(IFormatterDocument document, int startOffset,
			int endOffset) {
		super(document);
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}

	@Override
	public String getText() {
		return getDocument().get(startOffset, endOffset);
	}

	@Override
	public void accept(IFormatterContext context, IFormatterWriter visitor)
			throws Exception {
		visitor.write(context, getStartOffset(), getEndOffset());
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public int getEndOffset() {
		return endOffset;
	}

	@Override
	public int getStartOffset() {
		return startOffset;
	}

	@Override
	public String toString() {
		final StringWriter w = new StringWriter();
		escapeJavaStyleString(w, getText());
		return w.toString();
	}

	private static void escapeJavaStyleString(StringWriter out, String str) {
		if (str == null) {
			return;
		}
		int sz;
		sz = str.length();
		for (int i = 0; i < sz; i++) {
			char ch = str.charAt(i);
			if (ch > 0xfff) {
				out.write("\\u" + hex(ch)); //$NON-NLS-1$
			} else if (ch > 0xff) {
				out.write("\\u0" + hex(ch)); //$NON-NLS-1$
			} else if (ch > 0x7f) {
				out.write("\\u00" + hex(ch)); //$NON-NLS-1$
			} else if (ch < 32) {
				switch (ch) {
				case '\b':
					out.write('\\');
					out.write('b');
					break;
				case '\n':
					out.write('\\');
					out.write('n');
					break;
				case '\t':
					out.write('\\');
					out.write('t');
					break;
				case '\f':
					out.write('\\');
					out.write('f');
					break;
				case '\r':
					out.write('\\');
					out.write('r');
					break;
				default:
					if (ch > 0xf) {
						out.write("\\u00" + hex(ch)); //$NON-NLS-1$
					} else {
						out.write("\\u000" + hex(ch)); //$NON-NLS-1$
					}
					break;
				}
			} else {
				switch (ch) {
				case '\\':
					out.write('\\');
					out.write('\\');
					break;
				default:
					out.write(ch);
					break;
				}
			}
		}
	}

	private static String hex(char ch) {
		return Integer.toHexString(ch).toUpperCase();
	}

}
