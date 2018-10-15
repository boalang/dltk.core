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
 *     xored software, Inc. - initial API and Implementation (Yuri Strot) 
 *******************************************************************************/
package org.eclipse.dltk.formatter.profile;

import java.util.Map;

import org.eclipse.dltk.ui.formatter.IProfile;

/**
 * Represents a profile with a unique ID, a name and a map containing the code
 * formatter settings.
 */
public abstract class Profile implements IProfile {

	@Override
	public boolean equalsTo(Map<String, String> otherMap) {
		return getSettings().equals(otherMap);
	}

	@Override
	public boolean isBuiltInProfile() {
		return false;
	}
}
