/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.debug.ui.display.messages"; //$NON-NLS-1$
	public static String DebugScriptInterpreter_NoDebugger;
	public static String DebugScriptInterpreter_NeedToBeSuspended;
	public static String DebugScriptInterpreter_null;
	public static String DebugScriptInterpreter_unknownEvaluationError;
	public static String ResetOnLaunchAction_text;
	public static String OpenInputFieldAction_openCodeField;
	public static String RunInputFieldAction_runCode;
	public static String ScriptDisplayView_consoleName;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
