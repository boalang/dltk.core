/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.ui;

import static org.eclipse.dltk.ui.DLTKUIPlugin.PLUGIN_ID;

import org.eclipse.dltk.ui.IOpenDelegate;
import org.eclipse.dltk.utils.LazyExtensionManager;

public class OpenDelegateManager extends LazyExtensionManager<IOpenDelegate> {

	public static final String EXT_POINT = PLUGIN_ID + ".openDelegate"; //$NON-NLS-1$

	private static final OpenDelegateManager MANAGER = new OpenDelegateManager();

	private OpenDelegateManager() {
		super(EXT_POINT);
	}

	public static IOpenDelegate findFor(Object object) {
		for (IOpenDelegate factory : MANAGER) {
			if (factory.supports(object)) {
				return factory;
			}
		}
		return null;
	}

}
