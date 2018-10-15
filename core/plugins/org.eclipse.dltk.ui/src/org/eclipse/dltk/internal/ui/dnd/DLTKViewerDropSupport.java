/*******************************************************************************
 * Copyright (c) 2007, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.dnd;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.internal.ui.scriptview.FileTransferDropAdapter;
import org.eclipse.dltk.internal.ui.scriptview.SelectionTransferDropAdapter;
import org.eclipse.jface.util.DelegatingDropAdapter;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.PluginTransfer;

public class DLTKViewerDropSupport {

	private final StructuredViewer fViewer;
	private final DelegatingDropAdapter fDelegatingDropAdapter;
	private final SelectionTransferDropAdapter fReorgDropListener;
	private boolean fStarted;

	public DLTKViewerDropSupport(StructuredViewer viewer) {
		fViewer= viewer;

		fDelegatingDropAdapter= new DelegatingDropAdapter();
		fReorgDropListener= new SelectionTransferDropAdapter(fViewer);
		fDelegatingDropAdapter.addDropTargetListener(fReorgDropListener);
		fDelegatingDropAdapter.addDropTargetListener(new FileTransferDropAdapter(fViewer));
//		fDelegatingDropAdapter.addDropTargetListener(new PluginTransferDropAdapter(fViewer));

		fStarted= false;
	}

	public void addDropTargetListener(TransferDropTargetListener listener) {
		Assert.isLegal(!fStarted);

		fDelegatingDropAdapter.addDropTargetListener(listener);
	}

	public void start() {
		Assert.isLegal(!fStarted);

		int ops= DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_DEFAULT;

		Transfer[] transfers = new Transfer[] {
				LocalSelectionTransfer.getTransfer(),
				FileTransfer.getInstance(), PluginTransfer.getInstance() };

		fViewer.addDropSupport(ops, transfers, fDelegatingDropAdapter);

		fStarted= true;
	}

	public void setFeedbackEnabled(boolean enabled) {
		fReorgDropListener.setFeedbackEnabled(enabled);
	}

}
