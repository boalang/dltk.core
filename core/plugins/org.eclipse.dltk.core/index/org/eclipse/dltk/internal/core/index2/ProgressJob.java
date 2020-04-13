/*******************************************************************************
 * Copyright (c) 2009, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.internal.core.index2;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.internal.core.search.processing.JobManager;
import org.eclipse.dltk.internal.core.util.Messages;

public class ProgressJob extends Job {

	private JobManager jobManager;
	private IProgressMonitor monitor;
	private transient boolean running;

	public ProgressJob(JobManager jobManager) {
		super(Messages.manager_indexingInProgress);
		this.jobManager = jobManager;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		running = true;
		try {
			this.monitor = monitor;
			monitor.beginTask(Messages.manager_indexingTask, IProgressMonitor.UNKNOWN);

			while (!monitor.isCanceled() && (jobManager.awaitingJobsCount()) > 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignore
				}
			}
			if (monitor.isCanceled()) {
				jobManager.discardJobs(null);
			}
			monitor.done();
			return Status.OK_STATUS;
		} finally {
			running = false;
			this.monitor = null;
		}
	}

	public void subTask(String message) {
		if (!running) {
			schedule();
		}
		if (monitor != null) {
			monitor.subTask(message);
		}
	}
}