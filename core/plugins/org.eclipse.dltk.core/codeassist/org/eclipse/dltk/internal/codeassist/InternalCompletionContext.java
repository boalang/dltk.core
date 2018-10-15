/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.codeassist;

/**
 * Internal completion context
 */
public class InternalCompletionContext {

	protected int tokenStart = -1;
	protected int tokenEnd = -1;
	protected char[] token = null;
	protected int tokenKind;

	public void setTokenRange(int start, int end) {
		this.setTokenRange(start, end, -1);
	}

	protected void setTokenRange(int start, int end, int endOfEmptyToken) {
		this.tokenStart = start;
		this.tokenEnd = endOfEmptyToken > end ? endOfEmptyToken : end;

		// Work around for bug 132558
		// (https://bugs.eclipse.org/bugs/show_bug.cgi?id=132558).
		// completionLocation can be -1 if the completion occur at the start of
		// a file or
		// the start of a code snippet but this API isn't design to support
		// negative position.
		if (this.tokenEnd == -1) {
			this.tokenEnd = 0;
		}
	}

	protected void setToken(char[] token) {
		this.token = token;
	}

	public void setTokenKind(int tokenKind) {
		this.tokenKind = tokenKind;
	}
}
