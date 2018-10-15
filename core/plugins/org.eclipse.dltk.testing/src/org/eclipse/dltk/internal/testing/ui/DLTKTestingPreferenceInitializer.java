/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Sebastian Davids <sdavids@gmx.de> - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.testing.ui;

import java.util.List;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import org.eclipse.dltk.testing.DLTKTestingPlugin;

/**
 * Default preference value initialization for the
 * <code>org.eclipse.jdt.junit</code> plug-in.
 */
public class DLTKTestingPreferenceInitializer extends AbstractPreferenceInitializer {

	/** {@inheritDoc} */
	@Override
	public void initializeDefaultPreferences() {
		Preferences prefs= DLTKTestingPlugin.getDefault().getPluginPreferences();
		prefs.setDefault(DLTKTestingPreferencesConstants.DO_FILTER_STACK, true);
		prefs.setDefault(DLTKTestingPreferencesConstants.SHOW_ON_ERROR_ONLY, false);
		prefs.setDefault(DLTKTestingPreferencesConstants.ENABLE_ASSERTIONS, false);

		List<String> defaults= DLTKTestingPreferencesConstants.createDefaultStackFiltersList();
		String[] filters= defaults.toArray(new String[defaults.size()]);
		String active= DLTKTestingPreferencesConstants.serializeList(filters);
		prefs.setDefault(DLTKTestingPreferencesConstants.PREF_ACTIVE_FILTERS_LIST, active);
		prefs.setDefault(DLTKTestingPreferencesConstants.PREF_INACTIVE_FILTERS_LIST, ""); //$NON-NLS-1$
		prefs.setDefault(DLTKTestingPreferencesConstants.MAX_TEST_RUNS, 10);
	}
}
