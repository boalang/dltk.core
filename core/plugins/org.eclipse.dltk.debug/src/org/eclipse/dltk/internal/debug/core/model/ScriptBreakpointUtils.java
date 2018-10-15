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
package org.eclipse.dltk.internal.debug.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.debug.core.model.IScriptBreakpoint;

public class ScriptBreakpointUtils {

	/**
	 * Checks that {@link IScriptBreakpoint#getExpressionState()} is true and
	 * {@link IScriptBreakpoint#getExpression()} is not empty
	 * 
	 * @return
	 * @throws CoreException
	 */
	public static boolean isConditional(IScriptBreakpoint bp)
			throws CoreException {
		return isConditional(bp.getExpressionState(), bp.getExpression());
	}

	/**
	 * Checks that {@link expressionState} is true and {@link expression} is not
	 * empty
	 * 
	 * @return
	 * @throws CoreException
	 */
	public static boolean isConditional(boolean expressionState,
			String expression) {
		return expressionState && !StrUtils.isBlank(expression);
	}

}
