/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.debug.core.model;

import java.net.URI;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.dbgp.IDbgpSession;
import org.eclipse.dltk.debug.core.IDebugOptions;
import org.eclipse.dltk.internal.debug.core.model.IScriptStreamProxy;

public interface IScriptDebugTarget extends IDebugTarget {
	boolean isInitialized();

	// 
	String getSessionId();

	// Listener
	void addListener(IScriptDebugTargetListener listener);

	void removeListener(IScriptDebugTargetListener listener);

	// Request timeout
	void setRequestTimeout(int timeout);

	int getRequestTimeout();

	// Stream proxy management
	void setStreamProxy(IScriptStreamProxy proxy);

	IScriptStreamProxy getStreamProxy();

	// Run to line
	void runToLine(URI uri, int lineNumber) throws DebugException;

	void setFilters(String[] activeFilters);

	String[] getFilters();

	void setUseStepFilters(boolean useStepFilters);

	boolean isUseStepFilters();

	IDLTKLanguageToolkit getLanguageToolkit();

	/**
	 * Returns <code>true</code> if the thread should break on the first
	 * executable line of code, <code>false</code> otherwise.
	 */
	boolean breakOnFirstLineEnabled();

	void toggleGlobalVariables(boolean enabled);

	void toggleClassVariables(boolean enabled);

	void toggleLocalVariables(boolean enabled);

	boolean retrieveGlobalVariables();

	boolean retrieveClassVariables();

	boolean retrieveLocalVariables();

	String getConsoleEncoding();

	IDebugOptions getOptions();

	IDbgpSession[] getSessions();

	IScriptBreakpointPathMapper getPathMapper();

	/**
	 * @since 2.0
	 */
	boolean isRemote();
}
