/*******************************************************************************
 * Copyright (c) 2008, 2016 xored software, Inc.
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
package org.eclipse.dltk.core.search.indexing;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.internal.core.search.processing.IJob;
import org.eclipse.dltk.internal.core.util.Util;

public abstract class AbstractJob implements IJob {

	public static final boolean DEBUG = false;

	protected boolean isCancelled = false;

	@Override
	public boolean belongsTo(String jobFamily) {
		return false;
	}

	@Override
	public void cancel() {
		isCancelled = true;
	}

	// private Exception constructedAt;

	@Override
	public void ensureReadyToRun() {
		if (DEBUG) {
			// constructedAt = new Exception();
		}
	}

	protected abstract String getName();

	protected void log(Object message) {
		System.out.println('[' + toString() + "] " + message); //$NON-NLS-1$
	}

	private final String getShortClassName() {
		return getClass().getSimpleName();
	}

	private String savedName;

	@Override
	public String toString() {
		final String shortClassName = getShortClassName();
		if (savedName == null) {
			try {
				savedName = getName();
			} catch (Exception e) {
				savedName = "<Unknown>"; //$NON-NLS-1$
				Util.log(e, shortClassName + " getName() error"); //$NON-NLS-1$
			}
		}
		return shortClassName + '|' + savedName;
	}

	@Override
	public final boolean execute(IProgressMonitor monitor) {
		try {
			final long startTime = DEBUG ? System.currentTimeMillis() : 0;
			if (DEBUG) {
				log("BEGIN"); //$NON-NLS-1$
			}
			run();
			if (DEBUG) {
				log("END " + (System.currentTimeMillis() - startTime) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (Exception e) {
			if (DEBUG) {
				DLTKCore.error(toString() + " error", e); //$NON-NLS-1$
				e.printStackTrace();
				// DLTKCore.error("request was created at", constructedAt);
			}
			return FAILED;
		}
		return COMPLETE;
	}

	protected abstract void run() throws CoreException, IOException;

}
