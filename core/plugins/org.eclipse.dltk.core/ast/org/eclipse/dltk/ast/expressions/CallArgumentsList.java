/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.ast.expressions;

import java.util.List;

import org.eclipse.dltk.ast.ASTListNode;
import org.eclipse.dltk.ast.ASTNode;

public class CallArgumentsList extends ASTListNode {
	public static final CallArgumentsList EMPTY = new CallArgumentsList() {

		@Override
		public void addNode(ASTNode s) {
			throw new IllegalStateException("This object is unmodifiable"); //$NON-NLS-1$
		}

		@Override
		public void setChilds(List l) {
			throw new IllegalStateException("This object is unmodifiable"); //$NON-NLS-1$
		}

		@Override
		public void setEnd(int end) {
			throw new IllegalStateException("This object is unmodifiable"); //$NON-NLS-1$
		}

		@Override
		public void setStart(int start) {
			throw new IllegalStateException("This object is unmodifiable"); //$NON-NLS-1$
		}

	};

	public CallArgumentsList() {
		super();
	}

	public CallArgumentsList(int start, int end) {
		super(start, end);
	}
}
