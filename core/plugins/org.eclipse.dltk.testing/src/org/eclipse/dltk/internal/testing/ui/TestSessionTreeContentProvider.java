/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.internal.testing.ui;

import org.eclipse.dltk.internal.testing.model.TestContainerElement;
import org.eclipse.dltk.internal.testing.model.TestElement;
import org.eclipse.dltk.internal.testing.model.TestRoot;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;



public class TestSessionTreeContentProvider implements ITreeContentProvider {

	private final Object[] NO_CHILDREN= new Object[0];
	
	@Override
	public void dispose() {
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TestContainerElement)
			return ((TestContainerElement) parentElement).getChildren();
		else
			return NO_CHILDREN;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return ((TestRoot) inputElement).getChildren();
	}

	@Override
	public Object getParent(Object element) {
		return ((TestElement) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof TestContainerElement)
			return ((TestContainerElement) element).getChildren().length != 0;
		else
			return false;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
