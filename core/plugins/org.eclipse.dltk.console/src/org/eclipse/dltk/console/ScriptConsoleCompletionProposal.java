/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.console;

public class ScriptConsoleCompletionProposal {
	private final String insert;

	private final String display;

	private final String type;

	public ScriptConsoleCompletionProposal(String insert, String display,
			String type) {
		this.insert = insert;
		this.display = display;
		this.type = type;
	}

	public String getDisplay() {
		return display;
	}

	public String getInsert() {
		return insert;
	}

	public String getType() {
		return type;
	}
}
