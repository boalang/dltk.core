/*******************************************************************************
 * Copyright (c) 2008, 2017 xored software, Inc. and others.
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
package org.eclipse.dltk.ui.templates;

public class TabExpandScriptTemplateIndenter implements IScriptTemplateIndenter {

	private final int tabSize;

	public TabExpandScriptTemplateIndenter(int tabSize) {
		this.tabSize = tabSize;
	}

	@Override
	public void indentLine(StringBuffer sb, String indent, String line) {
		sb.append(indent);
		int i = 0;
		while (i < line.length() && line.charAt(i) == '\t') {
			++i;
		}
		if (i > 0) {
			int spaceCount = i * tabSize;
			while (spaceCount > 0) {
				sb.append(' ');
				--spaceCount;
			}
		}
		if (i < line.length()) {
			sb.append(line.substring(i));
		}
	}

}
