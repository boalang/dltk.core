/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.navigator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.ui.ScriptElementImageDescriptor;
import org.eclipse.ui.IWorkingSet;

public class ScriptExplorerProblemsDecorator extends TreeHierarchyLayoutProblemsDecorator {

	public ScriptExplorerProblemsDecorator() {
		super();
	}

	public ScriptExplorerProblemsDecorator(boolean isFlatLayout) {
		super(isFlatLayout);
	}

	@Override
	protected int computeAdornmentFlags(Object obj) {
		if (!(obj instanceof IWorkingSet))
			return super.computeAdornmentFlags(obj);

		IWorkingSet workingSet= (IWorkingSet)obj;
		IAdaptable[] elements= workingSet.getElements();
		int result= 0;
		for (int i= 0; i < elements.length; i++) {
			IAdaptable element= elements[i];
			int flags= super.computeAdornmentFlags(element);
			if ((flags & ScriptElementImageDescriptor.ERROR) != 0)
				return ScriptElementImageDescriptor.ERROR;
			if ((flags & ScriptElementImageDescriptor.WARNING) != 0)
				result= ScriptElementImageDescriptor.WARNING;
		}
		return result;
	}
}
