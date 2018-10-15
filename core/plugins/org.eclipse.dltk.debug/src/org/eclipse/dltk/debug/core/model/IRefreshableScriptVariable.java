/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

/**
 * {@link IScriptVariable}s could implement this interface to support value
 * change check.
 */
public interface IRefreshableScriptVariable {

	/**
	 * Refreshes the value of the variable
	 * 
	 * @param newVariable
	 * @return <code>this</code> if value was successfully refreshed or
	 *         <code>newVariable</code> if it was not possible to refresh value.
	 * @throws DebugException
	 */
	IVariable refreshVariable(IVariable newVariable) throws DebugException;

}
