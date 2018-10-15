/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParameter;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.utils.CorePrinter;

public class SourceMethod extends NamedMember implements IMethod {

	public SourceMethod(ModelElement parent, String name) {
		super(parent, name);
	}

	@Override
	public int getElementType() {
		return METHOD;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SourceMethod)) {
			return false;
		}
		return super.equals(o);
	}

	@Override
	public String[] getParameterNames() throws ModelException {
		return ((SourceMethodElementInfo) getElementInfo()).getArgumentNames();
	}

	@Override
	public IParameter[] getParameters() throws ModelException {
		return ((SourceMethodElementInfo) getElementInfo()).getArguments();
	}

	@Override
	public void printNode(CorePrinter output) {
		output.formatPrint("DLTK Source Method:" + getElementName()); //$NON-NLS-1$
		output.indent();
		try {
			IModelElement modelElements[] = this.getChildren();
			for (int i = 0; i < modelElements.length; ++i) {
				IModelElement element = modelElements[i];
				if (element instanceof ModelElement) {
					((ModelElement) element).printNode(output);
				} else {
					output.print("Unknown element:" + element); //$NON-NLS-1$
				}
			}
		} catch (ModelException ex) {
			output.formatPrint(ex.getLocalizedMessage());
		}
		output.dedent();
	}

	/**
	 * @see IMethod
	 */
	@Override
	public boolean isConstructor() throws ModelException {
		return ((SourceMethodElementInfo) getElementInfo()).isConstructor();
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return JEM_METHOD;
	}

	@Override
	public String getFullyQualifiedName(String enclosingTypeSeparator) {
		try {
			return getFullyQualifiedName(enclosingTypeSeparator, false);
		} catch (ModelException e) {
			// exception thrown only when showing parameters
			return null;
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return getFullyQualifiedName("$"); //$NON-NLS-1$
	}

	@Override
	public IScriptFolder getScriptFolder() {
		IModelElement parentElement = this.parent;
		while (parentElement != null) {
			if (parentElement.getElementType() == IModelElement.SCRIPT_FOLDER) {
				return (IScriptFolder) parentElement;
			} else {
				parentElement = parentElement.getParent();
			}
		}
		Assert.isTrue(false); // should not happen
		return null;
	}

	@Override
	public String getType() throws ModelException {
		return ((SourceMethodElementInfo) getElementInfo()).getReturnTypeName();
	}
}
