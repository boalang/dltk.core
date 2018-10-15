/*******************************************************************************
 * Copyright (c) 2009, 2016 xored software, Inc. and others.
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
package org.eclipse.dltk.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class PreferencesDelegate extends PreferencesLookupDelegate implements
		IPreferencesSaveDelegate {

	/**
	 * @param project
	 */
	public PreferencesDelegate(IProject project) {
		super(project);
	}

	private IEclipsePreferences getNode(String qualifier) {
		return getTopScopeContext().getNode(qualifier);
	}

	@Override
	public void setBoolean(String qualifier, String key, boolean value) {
		getNode(qualifier).putBoolean(key, value);
	}

	@Override
	public void setInt(String qualifier, String key, int value) {
		getNode(qualifier).putInt(key, value);
	}

	@Override
	public void setString(String qualifier, String key, String value) {
		if (value != null) {
			getNode(qualifier).put(key, value);
		} else {
			getNode(qualifier).remove(key);
		}
	}

}
