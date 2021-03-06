/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.internal.ui;


import org.eclipse.dltk.core.IModelElement;
import org.eclipse.jface.viewers.IBasicPropertyConstants;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class ScriptElementProperties implements IPropertySource {

	private IModelElement fSource;

	// Property Descriptors
	private static final IPropertyDescriptor[] fgPropertyDescriptors= new IPropertyDescriptor[1];
	static {
		PropertyDescriptor descriptor;

		// resource name
		descriptor= new PropertyDescriptor(IBasicPropertyConstants.P_TEXT, DLTKUIMessages.ScriptElementProperties_name);
		descriptor.setAlwaysIncompatible(true);
		fgPropertyDescriptors[0]= descriptor;
	}

	public ScriptElementProperties(IModelElement source) {
		fSource= source;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return fgPropertyDescriptors;
	}

	@Override
	public Object getPropertyValue(Object name) {
		if (name.equals(IBasicPropertyConstants.P_TEXT)) {
			return fSource.getElementName();
		}
		return null;
	}

	@Override
	public void setPropertyValue(Object name, Object value) {
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	@Override
	public boolean isPropertySet(Object property) {
		return false;
	}

	@Override
	public void resetPropertyValue(Object property) {
	}
}
