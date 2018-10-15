/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
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
package org.eclipse.dltk.ui.formatter;

import java.util.Map;

/**
 * Represents a profile with a unique ID, a name and a map containing the code
 * formatter settings.
 */
public interface IProfile extends Comparable<IProfile> {

	public interface ICustomProfile extends IProfile {
		void setVersion(int version);
	}

	public String getName();

	public Map<String, String> getSettings();

	public void setSettings(Map<String, String> settings);

	public String getFormatterId();

	public int getVersion();

	public boolean equalsTo(Map<String, String> otherMap);

	public String getID();

	public boolean isBuiltInProfile();
}
