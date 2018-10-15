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
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.eclipse.dltk.debug.core.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.internal.debug.core.model.ScriptThread;
import org.eclipse.dltk.internal.debug.core.model.operations.DbgpDebugger;

/**
 * This class called to configure advanced thread parameters. It could be
 * registered from debugger runner to ScriptDebugTarget. One instance per
 * target.
 */
public interface IScriptDebugThreadConfigurator {
	void configureThread(DbgpDebugger engine, ScriptThread scriptThread);

	void initializeBreakpoints(IScriptThread thread, IProgressMonitor monitor);
}
