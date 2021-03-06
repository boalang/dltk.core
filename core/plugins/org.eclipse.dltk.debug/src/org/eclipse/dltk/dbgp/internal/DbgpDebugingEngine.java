/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.dbgp.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.dbgp.DbgpRequest;
import org.eclipse.dltk.dbgp.IDbgpRawListener;
import org.eclipse.dltk.dbgp.IDbgpRawPacket;
import org.eclipse.dltk.dbgp.internal.packets.DbgpNotifyPacket;
import org.eclipse.dltk.dbgp.internal.packets.DbgpPacketReceiver;
import org.eclipse.dltk.dbgp.internal.packets.DbgpPacketSender;
import org.eclipse.dltk.dbgp.internal.packets.DbgpResponsePacket;
import org.eclipse.dltk.dbgp.internal.packets.DbgpStreamPacket;
import org.eclipse.dltk.debug.core.ExtendedDebugEventDetails;
import org.eclipse.dltk.internal.debug.core.model.DebugEventHelper;

public class DbgpDebugingEngine extends DbgpTermination
		implements IDbgpDebugingEngine, IDbgpTerminationListener {
	private final Socket socket;

	private final DbgpPacketReceiver receiver;

	private final DbgpPacketSender sender;

	private final Object terminatedLock = new Object();
	private boolean terminated = false;

	private final int id;

	private static int lastId = 0;
	private static final Object idLock = new Object();

	public DbgpDebugingEngine(Socket socket) throws IOException {
		this.socket = socket;
		synchronized (idLock) {
			id = ++lastId;
		}

		receiver = new DbgpPacketReceiver(
				new BufferedInputStream(socket.getInputStream()));

		receiver.setLogger(output -> firePacketReceived(output));

		receiver.addTerminationListener(this);

		receiver.start();

		sender = new DbgpPacketSender(
				new BufferedOutputStream(socket.getOutputStream()));

		sender.setLogger(output -> firePacketSent(output));
		/*
		 * FIXME this event is delivered on the separate thread, so sometimes
		 * logging misses a few initial packets.
		 */
		DebugEventHelper.fireExtendedEvent(this,
				ExtendedDebugEventDetails.DGBP_NEW_CONNECTION);
	}

	@Override
	public DbgpStreamPacket getStreamPacket()
			throws IOException, InterruptedException {
		return receiver.getStreamPacket();
	}

	@Override
	public DbgpNotifyPacket getNotifyPacket()
			throws IOException, InterruptedException {
		return receiver.getNotifyPacket();
	}

	@Override
	public DbgpResponsePacket getResponsePacket(int transactionId, int timeout)
			throws IOException, InterruptedException {
		return receiver.getResponsePacket(transactionId, timeout);
	}

	@Override
	public void sendCommand(DbgpRequest command) throws IOException {
		sender.sendCommand(command);
	}

	// IDbgpTerminataion
	@Override
	public void requestTermination() {
		// always just close the socket
		try {
			socket.close();
		} catch (IOException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void waitTerminated() throws InterruptedException {
		synchronized (terminatedLock) {
			if (terminated) {
				return;
			}

			receiver.waitTerminated();
		}
	}

	@Override
	public void objectTerminated(Object object, Exception e) {
		synchronized (terminatedLock) {
			if (terminated)
				return;

			receiver.removeTerminationListener(this);
			try {
				receiver.waitTerminated();
			} catch (InterruptedException e1) {
				// OK, interrupted
			}

			terminated = true;
		}

		fireObjectTerminated(e);
	}

	private final ListenerList<IDbgpRawListener> listeners = new ListenerList<>();

	protected void firePacketReceived(IDbgpRawPacket content) {
		for (IDbgpRawListener listener : listeners) {
			listener.dbgpPacketReceived(id, content);
		}
	}

	protected void firePacketSent(IDbgpRawPacket content) {
		for (IDbgpRawListener listener : listeners) {
			listener.dbgpPacketSent(id, content);
		}
	}

	@Override
	public void addRawListener(IDbgpRawListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeRawListenr(IDbgpRawListener listener) {
		listeners.remove(listener);
	}
}
