/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
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
package org.eclipse.dltk.ui.formatter;

public class FormatterException extends Exception {

	private static final long serialVersionUID = -1771588890906618803L;

	public FormatterException() {
		// empty
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FormatterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public FormatterException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FormatterException(Throwable cause) {
		super(cause);
	}

}
