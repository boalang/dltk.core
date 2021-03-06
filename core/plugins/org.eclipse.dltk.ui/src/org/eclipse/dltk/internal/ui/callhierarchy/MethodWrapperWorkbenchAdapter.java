/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.callhierarchy;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.internal.corext.callhierarchy.MethodWrapper;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;


public class MethodWrapperWorkbenchAdapter implements IWorkbenchAdapter {

	private final MethodWrapper fMethodWrapper;

	public MethodWrapperWorkbenchAdapter(MethodWrapper methodWrapper) {
		Assert.isNotNull(methodWrapper);
		fMethodWrapper= methodWrapper;
	}

	public MethodWrapper getMethodWrapper() {
		return fMethodWrapper;
	}

	@Override
	public Object[] getChildren(Object o) { //should not be called
		return new Object[0];
	}

	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	@Override
	public String getLabel(Object o) {
        return fMethodWrapper.getMember().getElementName();
	}

	@Override
	public Object getParent(Object o) {
		return fMethodWrapper.getParent();
	}

    @Override
	public boolean equals(Object obj) {
        //Note: A MethodWrapperWorkbenchAdapter is equal to its MethodWrapper and vice versa (bug 101677).
        return fMethodWrapper.equals(obj);
    }

    @Override
	public int hashCode() {
        //Note: A MethodWrapperWorkbenchAdapter is equal to its MethodWrapper and vice versa (bug 101677).
        return fMethodWrapper.hashCode();
    }

}
