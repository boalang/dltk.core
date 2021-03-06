/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.viewsupport;

import org.eclipse.dltk.ui.actions.MemberFilterActionGroup;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

/**
 * Action used to enable / disable method filter properties
 */
public class MemberFilterAction extends Action {

	private final AbstractModelElementFilter fFilter;

	private final MemberFilterActionGroup fFilterActionGroup;

	/**
	 * Construct an action for MemberFilterActioGroup
	 * 
	 * @param actionGroup
	 *            object of MemberFilterActioGroup
	 * @param title
	 *            title of action
	 * @param property
	 *            One of MemberFilter.FILTER_*
	 * @param contextHelpId
	 *            context id for help
	 * @param initValue
	 *            initial state of filter
	 */
	public MemberFilterAction(MemberFilterActionGroup actionGroup,
			String title, AbstractModelElementFilter filter,
			String contextHelpId, boolean initValue) {
		super(title);
		fFilterActionGroup = actionGroup;
		fFilter = filter;
		if (contextHelpId != null && contextHelpId.length() != 0) {
			PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
					contextHelpId);
		}
		setChecked(initValue);
	}

	/**
	 * Returns this action's filter property.
	 */
	public AbstractModelElementFilter getFilter() {
		return fFilter;
	}

	@Override
	public void run() {
		fFilterActionGroup.processMemberFilterAction(this);
	}
}
