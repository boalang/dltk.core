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

import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.ti.types.IEvaluatedType;

public class ModelClassType implements IClassType {

	private IType fClass;

	public ModelClassType(IType classElement) {
		this.fClass = classElement;
	}

	@Override
	public String getTypeName() {
		if (fClass != null) {
			return "class:" + fClass.getElementName(); //$NON-NLS-1$
		}
		return "class: !!unknown!!"; //$NON-NLS-1$
	}

	public IType getTypeDeclaration() {
		return this.fClass;
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
		result = prime * result + ((fClass == null) ? 0 : fClass.hashCode());
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
		ModelClassType other = (ModelClassType) obj;
		if (fClass == null) {
			if (other.fClass != null)
				return false;
		} else if (!fClass.equals(other.fClass))
			return false;
		return true;
	}
}
