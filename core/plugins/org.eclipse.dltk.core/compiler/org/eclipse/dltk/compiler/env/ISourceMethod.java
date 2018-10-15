/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.compiler.env;

public interface ISourceMethod extends IGenericMethod {

	/**
	 * Answer the source end position of the method's declaration.
	 */
	int getDeclarationSourceEnd();

	/**
	 * Answer the source start position of the method's declaration.
	 */
	int getDeclarationSourceStart();

	/**
	 * Answer the source end position of the method's selector.
	 */
	int getNameSourceEnd();

	/**
	 * Answer the source start position of the method's selector.
	 */
	int getNameSourceStart();

	/**
	 * Answer the unresolved name of the return type or null if receiver is a
	 * constructor or clinit.
	 * 
	 * The name is a simple name or a qualified, dot separated name. For
	 * example, Hashtable or java.util.Hashtable.
	 * 
	 * @since 2.0
	 */
	String getReturnTypeName();
}
