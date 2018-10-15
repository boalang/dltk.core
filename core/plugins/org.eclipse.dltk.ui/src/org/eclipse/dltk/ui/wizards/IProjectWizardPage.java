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

import org.eclipse.jface.wizard.IWizardPage;

/**
 * @since 2.0
 */
public interface IProjectWizardPage extends IWizardPage {

	/**
	 * This method is called initially to configure project create steps.
	 * 
	 * @param creator
	 */
	void initProjectWizardPage();

	/**
	 * This methods is called for all previous pages to update project create
	 * steps.
	 * 
	 * @param creator
	 */
	void updateProjectWizardPage();

	/**
	 * This method is called when project being created was deleted as a result
	 * of project wizard cancellation or returning to the 1st page.
	 * 
	 * @param creator
	 */
	void resetProjectWizardPage();

}
