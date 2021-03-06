/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.dbgp.internal.packets;

import org.w3c.dom.Element;

public class DbgpNotifyPacket extends DbgpPacket {
	private final String name;

	public DbgpNotifyPacket(Element content, String name) {
		super(content);

		if (name == null) {
			throw new IllegalArgumentException();
		}

		this.name = name;
	}

	public String getName() {
		return name;
	}
}
