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

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.dltk.dbgp.DbgpRequest;

public class DbgpPacketSender {
	private final Object lock = new Object();

	private final OutputStream output;

	private IDbgpRawLogger logger;

	public DbgpPacketSender(OutputStream output) {
		if (output == null) {
			throw new IllegalArgumentException();
		}

		this.output = output;
	}

	public void setLogger(IDbgpRawLogger logger) {
		this.logger = logger;
	}

	public void sendCommand(DbgpRequest command) throws IOException {
		if (logger != null) {
			logger.log(command);
		}

		synchronized (lock) {
			command.writeTo(output);
			output.write(0);
			output.flush();
		}
	}
}
