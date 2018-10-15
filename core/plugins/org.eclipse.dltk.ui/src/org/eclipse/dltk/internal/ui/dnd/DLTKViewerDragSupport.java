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
import org.eclipse.dltk.internal.ui.scriptview.FileTransferDragAdapter;
import org.eclipse.dltk.internal.ui.scriptview.SelectionTransferDragAdapter;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.ResourceTransfer;

public class DLTKViewerDragSupport {

	private final StructuredViewer fViewer;
	private final DLTKViewerDragAdapter fDragAdapter;
	private boolean fStarted;

	public DLTKViewerDragSupport(StructuredViewer viewer) {
		fViewer= viewer;

		fDragAdapter= new DLTKViewerDragAdapter(fViewer);
		fDragAdapter.addDragSourceListener(new SelectionTransferDragAdapter(fViewer));
		fDragAdapter.addDragSourceListener(new EditorInputTransferDragAdapter(viewer));
		fDragAdapter.addDragSourceListener(new ResourceTransferDragAdapter(fViewer));
		fDragAdapter.addDragSourceListener(new FileTransferDragAdapter(fViewer));

		fStarted= false;
	}

	public void start() {
		Assert.isLegal(!fStarted);

		int ops= DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;

		Transfer[] transfers = new Transfer[] {
				LocalSelectionTransfer.getTransfer(),
				ResourceTransfer.getInstance(), FileTransfer.getInstance() };

		fViewer.addDragSupport(ops, transfers, fDragAdapter);

		fStarted= true;
	}

}
