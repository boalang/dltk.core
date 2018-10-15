/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.testing.ui;

import org.eclipse.ui.PlatformUI;

import org.eclipse.dltk.testing.model.ITestElement;
import org.eclipse.jface.action.Action;

/**
 * Requests to rerun a test.
 */
public class RerunAction extends Action {
	private final ITestElement fTestElement;
	private TestRunnerViewPart fTestRunner;
	private String fLaunchMode;
	
	/**
	 * Constructor for RerunAction.
	 */
	public RerunAction(String actionName, TestRunnerViewPart runner, ITestElement testElement,
			String launchMode) {
		super(actionName); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IDLTKTestingHelpContextIds.RERUN_ACTION);
		fTestRunner= runner;
		fTestElement = testElement;
		fLaunchMode= launchMode;
	}

	@Override
	public void run() {
		fTestRunner.rerunTest(fTestElement, fLaunchMode);
	}
}
