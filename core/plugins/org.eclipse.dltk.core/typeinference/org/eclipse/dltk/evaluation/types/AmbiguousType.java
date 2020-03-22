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

import java.util.Arrays;

import org.eclipse.dltk.ti.types.IEvaluatedType;

public class AmbiguousType implements IEvaluatedType {

	private final IEvaluatedType[] possibleTypes;

	public AmbiguousType(IEvaluatedType[] possibleTypes) {
		this.possibleTypes = possibleTypes;
	}

	@Override
	public String getTypeName() {
		StringBuilder result = new StringBuilder();
		result.append("Ambigous <"); //$NON-NLS-1$
		for (int i = 0; i < possibleTypes.length; i++) {
			IEvaluatedType type = possibleTypes[i];
			if (i > 0) {
				result.append(", "); //$NON-NLS-1$
			}
			result.append(type.getTypeName());
		}
		result.append(">"); //$NON-NLS-1$
		return result.toString();
	}

	public IEvaluatedType[] getPossibleTypes() {
		return possibleTypes;
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
		result = prime * result + Arrays.hashCode(possibleTypes);
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
		AmbiguousType other = (AmbiguousType) obj;
		if (!Arrays.equals(possibleTypes, other.possibleTypes))
			return false;
		return true;
	}
}
