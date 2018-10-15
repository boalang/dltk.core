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
 *     xored software, Inc. - initial API and Implementation (Yuri Strot)
 *******************************************************************************/
package org.eclipse.dltk.ast.expressions;

import org.eclipse.dltk.ast.ASTNode;

public class MethodCallExpression extends CallExpression {
	public MethodCallExpression(ASTNode receiver, String name,
			CallArgumentsList args) {
		super(receiver, name, args);
	}

	public MethodCallExpression(int start, int end, ASTNode receiver,
			String name, CallArgumentsList args) {
		super(start, end, receiver, name, args);
	}

	private String declaringTypeName;

	public String getDeclaringTypeName() {
		return declaringTypeName;
	}

	public void setDeclaringTypeName(String declaringTypeName) {
		this.declaringTypeName = declaringTypeName;
	}

}
