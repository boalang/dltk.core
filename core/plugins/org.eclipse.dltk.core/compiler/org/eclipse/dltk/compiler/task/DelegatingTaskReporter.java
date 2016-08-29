/*******************************************************************************
 * Copyright (c) 2010, 2016 xored software, Inc. and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.compiler.task;

import org.eclipse.dltk.core.builder.ISourceLineTracker;

/**
 * @since 3.0
 */
public class DelegatingTaskReporter implements ITaskReporter {

	private final ITaskReporter taskReporter;
	private final ISourceLineTracker lineTracker;
	private int lineOffset = 0;
	private int locationOffset = 0;

	public DelegatingTaskReporter(ITaskReporter taskReporter,
			ISourceLineTracker lineTracker) {
		this.taskReporter = taskReporter;
		this.lineTracker = lineTracker;
	}

	@Override
	public void reportTask(String message, int lineNumber, int priority,
			int charStart, int charEnd) {
		taskReporter.reportTask(message, lineNumber + lineOffset, priority,
				charStart + locationOffset, charEnd + locationOffset);
	}

	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	public void setOffset(int offset) {
		locationOffset = offset;
		lineOffset = lineTracker.getLineNumberOfOffset(offset);
	}

}
