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

import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.internal.ui.browsing.LogicalPackage;
import org.eclipse.dltk.ui.ProblemsLabelDecorator;
import org.eclipse.dltk.ui.ScriptElementImageDescriptor;


/**
 * Special problem decorator for hierarchical package layout.
 * <p>
 * It only decorates package fragments which are not covered by the
 * <code>ProblemsLabelDecorator</code>.
 * </p>
 *
 */
public class TreeHierarchyLayoutProblemsDecorator extends ProblemsLabelDecorator {

	private boolean fIsFlatLayout;

	public TreeHierarchyLayoutProblemsDecorator() {
		this(false);
	}

	public TreeHierarchyLayoutProblemsDecorator(boolean isFlatLayout) {
		super(null);
		fIsFlatLayout= isFlatLayout;
	}

	protected int computeScriptFolderAdornmentFlags(IScriptFolder fragment) {
		if (!fIsFlatLayout && !fragment.isRootFolder()) {
			return super.computeAdornmentFlags(fragment.getResource());
		}
		return super.computeAdornmentFlags(fragment);
	}

	@Override
	protected int computeAdornmentFlags(Object element) {
		if (element instanceof IScriptFolder) {
			return computeScriptFolderAdornmentFlags((IScriptFolder) element);
		} else if (element instanceof LogicalPackage) {
			IScriptFolder[] fragments= ((LogicalPackage) element).getFragments();
			int res= 0;
			for (int i= 0; i < fragments.length; i++) {
				int flags= computeScriptFolderAdornmentFlags(fragments[i]);
				if (flags == ScriptElementImageDescriptor.ERROR) {
					return flags;
				}
				else if (flags == ScriptElementImageDescriptor.WARNING) {
					return flags;
				}
				else if (flags != 0) {
					res= flags;
				}
			}
			return res;
		}
		return super.computeAdornmentFlags(element);
	}

	public void setIsFlatLayout(boolean state) {
		fIsFlatLayout= state;
	}

}
