/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.evaluation.types;

import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.ti.types.IEvaluatedType;

public class ModelFunctionType implements IFunctionType {

	private IMethod fMethod;

	public ModelFunctionType(IMethod method) {
		this.fMethod = method;
	}

	@Override
	public String getTypeName() {
		if (fMethod != null) {
			return "function:" + fMethod.getElementName(); //$NON-NLS-1$
		}
		return "function: !!unknown!!"; //$NON-NLS-1$
	}

	public IMethod getFunction() {
		return this.fMethod;
	}

	@Override
	public boolean subtypeOf(IEvaluatedType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fMethod == null) ? 0 : fMethod.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelFunctionType other = (ModelFunctionType) obj;
		if (fMethod == null) {
			if (other.fMethod != null)
				return false;
		} else if (!fMethod.equals(other.fMethod))
			return false;
		return true;
	}
}
