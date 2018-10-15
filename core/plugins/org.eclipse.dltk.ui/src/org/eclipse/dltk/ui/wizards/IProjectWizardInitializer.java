/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
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
package org.eclipse.dltk.ui.wizards;

import org.eclipse.dltk.core.environment.IEnvironment;

/**
 * @since 2.0
 */
public interface IProjectWizardInitializer {

	public interface IProjectWizardState {

		String MODE_WORKSPACE = "org.eclipse.dltk.ui.projectWizard.workspace"; //$NON-NLS-1$

		String MODE_EXTERNAL = "org.eclipse.dltk.ui.projectWizard.external"; //$NON-NLS-1$

		String getScriptNature();

		String getProjectName();

		void setProjectName(String name);

		String getToolTipText(String mode);

		void setToolTipText(String mode, String tooltip);

		String getMode();

		void setMode(String mode);

		IEnvironment getEnvironment();

		void setEnvironment(IEnvironment environment);

		String getExternalLocation();

		void setExternalLocation(String path);

		/**
		 * Returns the value of a client defined attribute.
		 * 
		 * @param key
		 *            the attribute key
		 * @return value the String attribute value, or <code>null</code> if
		 *         undefined
		 */
		String getString(String key);

		/**
		 * Sets the value of a client defined attribute.
		 * 
		 * @param key
		 *            the attribute key
		 * @param value
		 *            the attribute value
		 */
		void setString(String key, String value);

	}

	void initialize(IProjectWizardState state);

}
