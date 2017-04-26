/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.console.ui.internal.actions;

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.dltk.console.ui.ScriptConsole;
import org.eclipse.dltk.console.ui.ScriptConsoleUIConstants;
import org.eclipse.dltk.console.ui.ScriptConsoleUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

public class SaveConsoleSessionAction extends Action {
	private ScriptConsole console;

	public SaveConsoleSessionAction(ScriptConsole console, String text, String tooltip) {
		this.console = console;
		setId(ActionFactory.SAVE.getId());
		setText(text);
		setToolTipText(tooltip);
	}

	@Override
	public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		FileDialog dialog = new FileDialog(window.getShell(), SWT.SAVE);

		String file = dialog.open();

		try {
			if (file != null) {
				try (FileWriter writer = new FileWriter(file)) {
					writer.write(console.getSession().toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		setEnabled(true);
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return ScriptConsoleUIPlugin.getDefault().getImageDescriptor(ScriptConsoleUIConstants.SAVE_SESSION_ICON);
	}
}
