/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core;

import org.eclipse.core.resources.IMarker;
import org.eclipse.dltk.internal.core.util.Util;

public class CorrectionEngine {
	public static String[] getProblemArguments(IMarker problemMarker) {
		String argumentsString = problemMarker.getAttribute(
				IScriptModelMarker.ARGUMENTS, null);
		return Util.getProblemArgumentsFromMarker(argumentsString);
	}

	/**
	 * returns <code>null</code> or string array
	 * 
	 * @param arguments
	 * @return
	 */
	public static String[] decodeArguments(String arguments) {
		if (arguments != null && arguments.length() != 0) {
			return Util.getProblemArgumentsFromMarker(arguments);
		} else {
			return null;
		}
	}

	/**
	 * returns <code>null</code> or string
	 * 
	 * @param arguments
	 * @return
	 */
	public static String encodeArguments(String[] arguments) {
		if (arguments != null && arguments.length != 0) {
			return Util.getProblemArgumentsForMarker(arguments);
		} else {
			return null;
		}
	}
}
