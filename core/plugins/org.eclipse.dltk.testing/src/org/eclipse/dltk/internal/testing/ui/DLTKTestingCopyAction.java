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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.core.runtime.Assert;

import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionListenerAction;

import org.eclipse.dltk.internal.testing.model.TestElement;
import org.eclipse.dltk.testing.DLTKTestingMessages;
import org.eclipse.dltk.testing.ITestRunnerUI;
import org.eclipse.dltk.testing.ITestRunnerUIExtension2;

/**
 * Copies a test failure stack trace to the clipboard.
 */
public class DLTKTestingCopyAction extends SelectionListenerAction {
	private FailureTrace fView;
	
	private final Clipboard fClipboard;

	private TestElement fTestElement;

	/**
	 * Constructor for CopyTraceAction.
	 * @param view 
	 * @param clipboard 
	 */
	public DLTKTestingCopyAction(FailureTrace view, Clipboard clipboard) {
		super(DLTKTestingMessages.CopyTrace_action_label);  
		Assert.isNotNull(clipboard);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IDLTKTestingHelpContextIds.COPYTRACE_ACTION);
		fView= view;
		fClipboard= clipboard;
	}

	/*
	 * @see IAction#run()
	 */
	@Override
	public void run() {
		String trace= fView.getTrace();
		final ITestRunnerUI runnerUI = fView.getTestRunnerUI();
		if (runnerUI != null && runnerUI instanceof ITestRunnerUIExtension2) {
			trace = ((ITestRunnerUIExtension2) runnerUI)
					.prepareStackTraceCopy(trace);
		}
		String source= null;
		if (trace != null) {
			source= convertLineTerminators(trace);
		} else if (fTestElement != null) {
			source= fTestElement.getTestName();
		}
		if (source == null || source.length() == 0)
			return;
		
		TextTransfer plainTextTransfer = TextTransfer.getInstance();
		try{
			fClipboard.setContents(
				new String[]{ convertLineTerminators(source) }, 
				new Transfer[]{ plainTextTransfer });
		}  catch (SWTError e){
			if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) 
				throw e;
			if (MessageDialog.openQuestion(fView.getComposite().getShell(), DLTKTestingMessages.CopyTraceAction_problem, DLTKTestingMessages.CopyTraceAction_clipboard_busy))  
				run();
		}
	}


	public void handleTestSelected(TestElement test) {
		fTestElement= test;
	}
	
	private String convertLineTerminators(String in) {
		StringWriter stringWriter= new StringWriter();
		PrintWriter printWriter= new PrintWriter(stringWriter);
		StringReader stringReader= new StringReader(in);
		BufferedReader bufferedReader= new BufferedReader(stringReader);		
		String line;
		try {
			while ((line= bufferedReader.readLine()) != null) {
				printWriter.println(line);
			}
		} catch (IOException e) {
			return in; // return the trace unfiltered
		}
		return stringWriter.toString();
	}
}
