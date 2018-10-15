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
package org.eclipse.dltk.compiler.task;

import java.util.List;

import org.eclipse.dltk.core.DLTKCore;

public interface ITodoTaskPreferences {

	public static final String CASE_SENSITIVE = DLTKCore.PLUGIN_ID
			+ "tasks.case_sensitive"; //$NON-NLS-1$
	public static final String TAGS = DLTKCore.PLUGIN_ID + "tasks.tags"; //$NON-NLS-1$
	public static final String ENABLED = DLTKCore.PLUGIN_ID + "tasks.enabled"; //$NON-NLS-1$

	/**
	 * Checks if the tags are enabled
	 * 
	 * @return
	 */
	boolean isEnabled();

	/**
	 * Checks if the tags are case sensitive
	 * 
	 * @return
	 */
	boolean isCaseSensitive();

	/**
	 * returns task tags
	 * 
	 * @return list of {@link TodoTask}
	 */
	List<TodoTask> getTaskTags();

	/**
	 * returns just the names of the tags
	 * 
	 * @return
	 */
	String[] getTagNames();

}
