/*******************************************************************************
 * Copyright (c) 2009, 2016 xored software, Inc. and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.utils;

public abstract class ExecutableOperation implements IExecutableOperation {

	private final String operationName;

	public ExecutableOperation(String operationName) {
		this.operationName = operationName;
	}

	@Override
	public String getOperationName() {
		return operationName;
	}

}
