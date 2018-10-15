/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/

package org.eclipse.dltk.internal.corext.refactoring.reorg;


public interface ICreateTargetQuery {
	/**
	 * Creates and returns a new target.
	 * 
	 * @param selection the current destination
	 * @return the newly created target
	 */
	Object getCreatedTarget(Object selection);

	/**
	 * @return the label for the "Create ***..." button
	 */
	String getNewButtonLabel();
}
