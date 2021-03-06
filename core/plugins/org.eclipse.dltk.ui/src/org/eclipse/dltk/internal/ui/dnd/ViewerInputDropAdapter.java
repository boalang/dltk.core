/*******************************************************************************
 * Copyright (c) 2007, 2017 IBM Corporation and others.
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


import org.eclipse.dltk.internal.ui.scriptview.SelectionTransferDropAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Item;

public abstract class ViewerInputDropAdapter extends SelectionTransferDropAdapter {

	private static final int ITEM_MARGIN_LEFT= 40;
	private static final int ITEM_MARGIN_RIGTH= 10;
	private static final int OPERATION= DND.DROP_LINK;

	public ViewerInputDropAdapter(StructuredViewer viewer) {
		super(viewer);
	}

	protected abstract Object getInputElement(ISelection selection);

	protected abstract void doInputView(Object inputElement);

	@Override
	protected int determineOperation(Object target, int operation, TransferData transferType, int operations) {

		setSelectionFeedbackEnabled(true);
		setExpandEnabled(true);

		initializeSelection();

		if (target != null) {
			return super.determineOperation(target, operation, transferType, operations);
		} else if (getInputElement(getSelection()) != null) {
			setSelectionFeedbackEnabled(false);
			setExpandEnabled(false);
			return OPERATION;
		} else {
			return DND.DROP_NONE;
		}
	}

	@Override
	public boolean performDrop(Object data) {
		setSelectionFeedbackEnabled(true);
		setExpandEnabled(true);

		if (getCurrentTarget() != null || getCurrentOperation() != OPERATION) {
			return super.performDrop(data);
		}

		Object input= getInputElement(getSelection());
		if (input != null)
			doInputView(input);

		return false;
	}

	@Override
	public boolean isEnabled(DropTargetEvent event) {
		return true;
	}

	@Override
	protected Object determineTarget(DropTargetEvent event) {
		if (event.item == null)
			return super.determineTarget(event);

		Point coordinates= getViewer().getControl().toControl(new Point(event.x, event.y));
		Rectangle bounds= getBounds((Item) event.item);
		if (coordinates.x < bounds.x - ITEM_MARGIN_LEFT || coordinates.x >= bounds.x + bounds.width + ITEM_MARGIN_RIGTH) {
			event.item= null; // too far away
			return null;
		}

		return super.determineTarget(event);
	}

}
