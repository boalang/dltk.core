/*******************************************************************************
 * Copyright (c) 2009, 2018 xored software, Inc. and others.
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
package org.eclipse.dltk.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.internal.core.ScriptProject;

/**
 * @since 2.0
 */
public class ScriptProjectNaturePropertyTester extends PropertyTester {

	private static final String P_HAS_SCRIPT_NATURE = "hasScriptNature"; //$NON-NLS-1$

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof IProject) {
			if (P_HAS_SCRIPT_NATURE.equals(property)) {
				final boolean result = ScriptProject
						.hasScriptNature((IProject) receiver);
				if (expectedValue instanceof Boolean) {
					return result == ((Boolean) expectedValue).booleanValue();
				}
				return result;
			}
		}
		return false;
	}

}
