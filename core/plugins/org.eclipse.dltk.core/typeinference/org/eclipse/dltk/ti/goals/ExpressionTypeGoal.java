/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.ti.goals;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ti.IContext;

public class ExpressionTypeGoal extends AbstractTypeGoal {

	private final ASTNode expression;

	public ExpressionTypeGoal(IContext context, ASTNode expression) {
		super(context);
		this.expression = expression;
	}

	public ASTNode getExpression() {
		return expression;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((expression == null) ? 0 : expression.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpressionTypeGoal other = (ExpressionTypeGoal) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExpressionTypeGoal: " //$NON-NLS-1$
				+ ((expression != null) ? expression.toString() : "null") //$NON-NLS-1$
				+ " context: " //$NON-NLS-1$
				+ ((context != null) ? context.toString() : "null"); //$NON-NLS-1$
	}
}
