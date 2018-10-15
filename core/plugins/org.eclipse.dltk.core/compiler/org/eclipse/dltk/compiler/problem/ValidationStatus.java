/*******************************************************************************
 * Copyright (c) 2012 NumberFour AG
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.compiler.problem;

public class ValidationStatus implements IValidationStatus {
	private final IProblemIdentifier identifier;
	private final String message;
	private final int start;
	private final int end;

	public ValidationStatus(IProblemIdentifier identifier, String message) {
		this(identifier, message, -1, -1);
	}

	public ValidationStatus(IProblemIdentifier identifier, String message,
			int start, int end) {
		this.identifier = identifier;
		this.message = message;
		this.start = start;
		this.end = end;
	}

	public IProblemIdentifier identifier() {
		return identifier;
	}

	public String message() {
		return message;
	}

	@Override
	public String toString() {
		return identifier + " " + message;
	}

	public boolean hasRange() {
		return start >= 0 && end >= 0;
	}

	public int start() {
		return start;
	}

	public int end() {
		return end;
	}

	private static enum ValidationStatusResult implements IValidationStatus {
		OK
	}

	/**
	 * @since 4.1
	 */
	public static final IValidationStatus OK = ValidationStatusResult.OK;

}
