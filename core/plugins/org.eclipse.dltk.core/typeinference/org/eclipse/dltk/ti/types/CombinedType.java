/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.ti.types;

import java.util.Collection;
import java.util.HashSet;

/**
 * Combination of some set of simple types.
 */
public class CombinedType implements IEvaluatedType {

	private Collection<IEvaluatedType> types = new HashSet<>();

	@Override
	public String getTypeName() {
		return null;
	}

	public void appendType(IEvaluatedType type) {
		if (type instanceof CombinedType) {
			CombinedType combinedType = (CombinedType) type;
			types.addAll(combinedType.types);
		} else {
			types.add(type);
		}
	}

	public IEvaluatedType[] getTypes() {
		return types.toArray(new IEvaluatedType[types.size()]);
	}

	/**
	 * Combines types and returns one concrete type. If set contains only one
	 * type, it will be returned. If set contains no types, then
	 * <code>null</code> will be return. In other cases
	 * <code>MostSpecificType</code> are returned. If you want to use more
	 * complicated combination algorithm, for ex. intersection of types or their
	 * union, you may derive this class and override this method.
	 */
	public IEvaluatedType getCombinedType() {
		if (types.size() == 0) {
			return null;
		}
		if (types.size() == 1) {
			return types.iterator().next();
		}
		return MostSpecificType.getInstance();
	}

	@Override
	public boolean subtypeOf(IEvaluatedType type) {
		IEvaluatedType combinedType = getCombinedType();
		if (combinedType == null) {
			return false;
		}
		return combinedType.subtypeOf(type);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((types == null) ? 0 : types.hashCode());
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
		CombinedType other = (CombinedType) obj;
		if (types == null) {
			if (other.types != null)
				return false;
		} else if (!types.equals(other.types))
			return false;
		return true;
	}
}
