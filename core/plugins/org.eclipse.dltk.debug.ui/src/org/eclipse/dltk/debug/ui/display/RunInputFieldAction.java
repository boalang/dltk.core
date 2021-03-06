/*******************************************************************************
 * Copyright (c) 2008, 2017 xored software, Inc. and others.
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
package org.eclipse.dltk.debug.ui.display;

import org.eclipse.dltk.debug.ui.ScriptDebugImages;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.texteditor.IUpdate;

public class RunInputFieldAction extends Action implements IUpdate {

	private final DebugConsolePage page;

	public RunInputFieldAction(DebugConsolePage page) {
		super(Messages.RunInputFieldAction_runCode);
		this.page = page;
		setImageDescriptor(ScriptDebugImages.getImageDescriptor(
				ScriptDebugImages.IMG_OBJS_SNIPPET_EVALUATING));
	}

	@Override
	public void update() {
		setEnabled(page.canExecuteInputField());
	}

	@Override
	public void run() {
		page.executeInputField();
	}

}
