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
package org.eclipse.dltk.ui.dialogs;

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;

public interface IProjectTemplate {

	/**
	 * Returns the list of pages to be added to the project wizard
	 * 
	 * @since 2.0
	 */
	List<IWizardPage> getPages();

	/**
	 * Test if buildpath detection should be performed by the project wizard.
	 * This is usually the case when project is created at existing location.
	 * 
	 * @return <code>true</code> if buildpath detection should be performed and
	 *         <code>false</code> if default buildpath should be used.
	 * @since 2.0
	 */
	boolean getDetect();

}
