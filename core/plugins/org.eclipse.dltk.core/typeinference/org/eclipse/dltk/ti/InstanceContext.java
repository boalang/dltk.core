/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.ti;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ti.types.IEvaluatedType;

public class InstanceContext extends BasicContext implements IInstanceContext {

	private final IEvaluatedType instanceType;

	public InstanceContext(ISourceModule sourceModule,
			ModuleDeclaration rootNode, IEvaluatedType instanceType) {
		super(sourceModule, rootNode);
		this.instanceType = instanceType;
	}

	public InstanceContext(ISourceModuleContext parent,
			IEvaluatedType instanceType) {
		super(parent);
		this.instanceType = instanceType;
	}

	@Override
	public IEvaluatedType getInstanceType() {
		return instanceType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((instanceType == null) ? 0 : instanceType.hashCode());
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
		InstanceContext other = (InstanceContext) obj;
		if (instanceType == null) {
			if (other.instanceType != null)
				return false;
		} else if (!instanceType.equals(other.instanceType))
			return false;
		return true;
	}
}
