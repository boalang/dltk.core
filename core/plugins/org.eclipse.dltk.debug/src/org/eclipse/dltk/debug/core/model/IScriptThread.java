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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.dltk.dbgp.IDbgpSession;
import org.eclipse.dltk.dbgp.breakpoints.IDbgpBreakpoint;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationEngine;
import org.eclipse.dltk.internal.debug.core.model.IScriptStreamProxy;

public interface IScriptThread extends IThread /* , IFilteredStep */{
	int ERR_THREAD_NOT_SUSPENDED = -3;

	IDbgpSession getDbgpSession();

	IDbgpBreakpoint getDbgpBreakpoint(String id);

	IScriptStreamProxy getStreamProxy();

	IScriptEvaluationEngine getEvaluationEngine();

	int getModificationsCount();

	void sendTerminationRequest() throws DebugException;

	int getPropertyPageSize();

	boolean retrieveGlobalVariables();

	boolean retrieveClassVariables();

	boolean retrieveLocalVariables();

	void updateStackFrames();
}
