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

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ti.types.IEvaluatedType;

public class ClassInstanceType implements IClassType {

	private TypeDeclaration fClass;
	private ModuleDeclaration fModule;

	public ClassInstanceType(ModuleDeclaration module, TypeDeclaration method) {
		this.fClass = method;
		this.fModule = module;
	}

	public TypeDeclaration getTypeDeclaration() {
		return this.fClass;
	}

	@Override
	public String getTypeName() {
		if (fClass != null) {
			return "class:" + fClass.getName() + " instance"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return "class instance: !!unknown!!"; //$NON-NLS-1$
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
		result = prime * result + ((fModule == null) ? 0 : fModule.hashCode());
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
		ClassInstanceType other = (ClassInstanceType) obj;
		if (fClass == null) {
			if (other.fClass != null)
				return false;
		} else if (!fClass.equals(other.fClass))
			return false;
		if (fModule == null) {
			if (other.fModule != null)
				return false;
		} else if (!fModule.equals(other.fModule))
			return false;
		return true;
	}
}
