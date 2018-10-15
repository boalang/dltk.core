/*******************************************************************************
 * Copyright (c) 2002, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation
 *******************************************************************************/
package org.eclipse.dltk.ast.expressions;

import org.eclipse.dltk.ast.DLTKToken;
import org.eclipse.dltk.utils.CorePrinter;

/**
 * Boolean literal representation.
 * 
 */
public class BooleanLiteral extends Literal {

	private boolean value;

	/**
	 * Construct from ANTLR token.
	 * 
	 * @param t
	 */
	public BooleanLiteral(DLTKToken t) {
		super(t);
	}

	public BooleanLiteral(int start, int end, boolean value) {
		super(start, end);
		this.value = value;
		this.fLiteralValue = Boolean.toString(value);
	}

	public boolean boolValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
		this.fLiteralValue = Boolean.toString(value);
	}

	/**
	 * Return expression kind.
	 */
	@Override
	public int getKind() {
		return BOOLEAN_LITERAL;
	}

	/**
	 * Testing purposes only. Print boolean value.
	 */
	@Override
	public void printNode(CorePrinter output) {
		output.formatPrintLn("Boolean:" + this.getValue()); //$NON-NLS-1$

	}

}
