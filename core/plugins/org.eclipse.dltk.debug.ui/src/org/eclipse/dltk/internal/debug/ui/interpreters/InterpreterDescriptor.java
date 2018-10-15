/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.debug.ui.interpreters;

import org.eclipse.dltk.launching.IInterpreterInstall;

/**
 * Used to provide a description for interpreter selections in the installed
 * interpreters block.
 */
public abstract class InterpreterDescriptor {

	/**
	 * Returns a description of the interpreter setting.
	 * 
	 * @return description of the interpreter setting
	 */
	public abstract String getDescription();

	/**
	 * Returns the interpreter setting.
	 * 
	 * @return interpreter setting
	 */
	public abstract IInterpreterInstall getInterpreter();

}
