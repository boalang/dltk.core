/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.text.util;

/**
 * Provides information about the preferred indentation options.
 * 
 * @author Andrey Tarantsov
 */
public interface ITabPreferencesProvider {
	
	public int getIndentSize();
	
	public int getTabSize();
	
	public TabStyle getTabStyle();
	
	public String getIndent();
	
	public String getIndentByVirtualSize(int size);

	public String getIndent(int count);

}
