/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.debug.ui.interpreters;

import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class EnvironmentVariablesLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		return DebugPluginImages
				.getImage(IDebugUIConstants.IMG_OBJS_ENVIRONMENT);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof EnvironmentVariable) {
			EnvironmentVariable var = (EnvironmentVariable) element;
			return var.getName() + "=" + var.getValue(); //$NON-NLS-1$
		}
		return null;
	}

}
