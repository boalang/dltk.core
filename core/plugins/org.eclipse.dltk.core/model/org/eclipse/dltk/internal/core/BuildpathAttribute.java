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

import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.internal.core.util.Util;

public class BuildpathAttribute implements IBuildpathAttribute {
	
	private String name;
	private String value;
	
	public BuildpathAttribute(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BuildpathAttribute)) return false;
		BuildpathAttribute other = (BuildpathAttribute) obj;
		return this.name.equals(other.name) && this.value.equals(other.value);
	}

    @Override
	public String getName() {
		return this.name;
    }

    @Override
	public String getValue() {
		return this.value;
    }
    
    @Override
	public int hashCode() {
     	return Util.combineHashCodes(this.name.hashCode(), this.value.hashCode());
    }
    
    @Override
	public String toString() {
    	return this.name + "=" + this.value; //$NON-NLS-1$
    }

}
