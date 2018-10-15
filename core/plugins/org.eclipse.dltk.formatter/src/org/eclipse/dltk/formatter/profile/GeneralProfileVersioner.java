/******************************************************************************* 
 * Copyright (c) 2008, 2016 xored software, Inc. and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0  
 * 
 * Contributors: 
 *     xored software, Inc. - initial API and Implementation (Yuri Strot) 
 *******************************************************************************/
package org.eclipse.dltk.formatter.profile;

import org.eclipse.dltk.ui.formatter.IProfile;
import org.eclipse.dltk.ui.formatter.IProfileVersioner;

/**
 * Default implementation of the <code>IProfileVersioner</code>
 */
public class GeneralProfileVersioner implements IProfileVersioner {

	public GeneralProfileVersioner(String formatter) {
		this.formatter = formatter;
	}

	@Override
	public int getCurrentVersion() {
		return DEFAULT_VERSION;
	}

	@Override
	public int getFirstVersion() {
		return DEFAULT_VERSION;
	}

	@Override
	public String getFormatterId() {
		return formatter;
	}

	@Override
	public void update(IProfile profile) {
		if (profile instanceof CustomProfile)
			((CustomProfile) profile).setVersion(DEFAULT_VERSION);
	}

	private static final int DEFAULT_VERSION = 1;
	private String formatter;

}
