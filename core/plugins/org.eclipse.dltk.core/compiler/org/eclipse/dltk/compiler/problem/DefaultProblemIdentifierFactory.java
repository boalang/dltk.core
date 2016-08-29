/*******************************************************************************
 * Copyright (c) 2011 xored software, Inc. and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.compiler.problem;

public class DefaultProblemIdentifierFactory implements
		IProblemIdentifierFactory {

	@Override
	public IProblemIdentifier valueOf(String localName)
			throws IllegalArgumentException {
		return DefaultProblemIdentifier.valueOf(localName);
	}

	@Override
	public IProblemIdentifier[] values() {
		return DefaultProblemIdentifier.values();
	}

}
