/*******************************************************************************
 * Copyright (c) 2008, 2017 xored software, Inc. and others.
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
package org.eclipse.dltk.dbgp.internal.commands;

import java.net.URI;

import org.eclipse.dltk.dbgp.DbgpBaseCommands;
import org.eclipse.dltk.dbgp.DbgpRequest;
import org.eclipse.dltk.dbgp.IDbgpCommunicator;
import org.eclipse.dltk.dbgp.IDbgpFeature;
import org.eclipse.dltk.dbgp.IDbgpSpawnpoint;
import org.eclipse.dltk.dbgp.commands.IDbgpCommands;
import org.eclipse.dltk.dbgp.commands.IDbgpFeatureCommands;
import org.eclipse.dltk.dbgp.commands.IDbgpSpawnpointCommands;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.debug.core.DLTKDebugPlugin;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DbgpSpawnpointCommands extends DbgpBaseCommands
		implements IDbgpSpawnpointCommands {

	private static final String ELEMENT_SPAWNPOINT = "spawnpoint"; //$NON-NLS-1$
	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_STATE = "state"; //$NON-NLS-1$
	private static final String ATTR_FILENAME = "filename"; //$NON-NLS-1$
	private static final String ATTR_LINENO = "lineno"; //$NON-NLS-1$

	private static final String CMD_GET = "spawnpoint_get"; //$NON-NLS-1$
	private static final String CMD_SET = "spawnpoint_set"; //$NON-NLS-1$
	private static final String CMD_LIST = "spawnpoint_list"; //$NON-NLS-1$
	private static final String CMD_REMOVE = "spawnpoint_remove"; //$NON-NLS-1$
	private static final String CMD_UPDATE = "spawnpoint_update"; //$NON-NLS-1$

	private static final String OPTION_ID = "-d"; //$NON-NLS-1$
	private static final String OPTION_FILENAME = "-f"; //$NON-NLS-1$
	private static final String OPTION_LINE = "-n"; //$NON-NLS-1$
	private static final String OPTION_STATE = "-s"; //$NON-NLS-1$

	private static final String STATE_ENABLED = "enabled"; //$NON-NLS-1$
	private static final String STATE_DISABLED = "disabled"; //$NON-NLS-1$

	private final IDbgpCommands commands;

	/**
	 * @param communicator
	 * @param commands
	 */
	public DbgpSpawnpointCommands(IDbgpCommunicator communicator,
			IDbgpCommands commands) {
		super(communicator);
		this.commands = commands;
	}

	private boolean initialized = false;

	private void initSpawnpoints() {
		if (initialized) {
			return;
		}
		try {
			commands.getCoreCommands().setFeature(
					IDbgpFeatureCommands.MULTIPLE_SESSIONS,
					IDbgpFeature.ONE_VALUE);
		} catch (DbgpException e) {
			DLTKDebugPlugin.logWarning("Error setting '" //$NON-NLS-1$
					+ IDbgpFeatureCommands.MULTIPLE_SESSIONS + "' feature to '" //$NON-NLS-1$
					+ IDbgpFeature.ONE_VALUE + "'", e); //$NON-NLS-1$
		}
		initialized = true;
	}

	private static abstract class AbstractSpawnpoint
			implements IDbgpSpawnpoint {
		private final String id;
		private final boolean enabled;
		private final int lineNumber;

		/**
		 * @param id
		 * @param enabled
		 * @param lineNumber
		 */
		public AbstractSpawnpoint(String id, boolean enabled, int lineNumber) {
			this.id = id;
			this.enabled = enabled;
			this.lineNumber = lineNumber;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public int getLineNumber() {
			return lineNumber;
		}

		@Override
		public boolean isEnabled() {
			return enabled;
		}

		@Override
		public String toString() {
			return id + '/' + (enabled ? STATE_ENABLED : STATE_DISABLED) + '/'
					+ getFilename() + ':' + lineNumber;
		}

	}

	private static class DbgpSpawnpoint extends AbstractSpawnpoint {

		private final URI uri;

		/**
		 * @param id
		 * @param equals
		 * @param uri
		 * @param lineNumber
		 */
		public DbgpSpawnpoint(String id, boolean enabled, URI uri,
				int lineNumber) {
			super(id, enabled, lineNumber);
			this.uri = uri;
		}

		@Override
		public String getFilename() {
			return uri.toString();
		}
	}

	private static class DbgpSpawnpointString extends AbstractSpawnpoint {

		private final String filename;

		/**
		 * @param id
		 * @param equals
		 * @param uri
		 * @param lineNumber
		 */
		public DbgpSpawnpointString(String id, boolean enabled, String filename,
				int lineNumber) {
			super(id, enabled, lineNumber);
			this.filename = filename;
		}

		@Override
		public String getFilename() {
			return filename;
		}
	}

	@Override
	public IDbgpSpawnpoint getSpawnpoint(String spawnpointId)
			throws DbgpException {
		initSpawnpoints();
		final DbgpRequest request = createRequest(CMD_GET);
		request.addOption(OPTION_ID, spawnpointId);
		final Element response = communicate(request);
		final String id = response.getAttribute(ATTR_ID);
		if (id != null) {
			return new DbgpSpawnpointString(id, parseState(response),
					response.getAttribute(ATTR_FILENAME),
					parseLineNumber(response));
		} else {
			return null;
		}
	}

	@Override
	public IDbgpSpawnpoint[] listSpawnpoints() throws DbgpException {
		initSpawnpoints();
		final DbgpRequest request = createRequest(CMD_LIST);
		final Element response = communicate(request);
		final NodeList points = response
				.getElementsByTagName(ELEMENT_SPAWNPOINT);
		final IDbgpSpawnpoint[] result = new IDbgpSpawnpoint[points
				.getLength()];
		int count = 0;
		for (int i = 0; i < points.getLength(); ++i) {
			final Element point = (Element) points.item(i);
			final String id = point.getAttribute(ATTR_ID);
			if (id != null) {
				result[count++] = new DbgpSpawnpointString(id,
						parseState(point), point.getAttribute(ATTR_FILENAME),
						parseLineNumber(point));
			}
		}
		if (count < result.length) {
			final IDbgpSpawnpoint[] newResult = new IDbgpSpawnpoint[count];
			System.arraycopy(result, 0, newResult, 0, count);
			return newResult;
		} else {
			return result;
		}
	}

	@Override
	public void removeSpawnpoint(String spawnpointId) throws DbgpException {
		if (spawnpointId == null) {
			return;
		}
		initSpawnpoints();
		final DbgpRequest request = createRequest(CMD_REMOVE);
		request.addOption(OPTION_ID, spawnpointId);
		communicate(request);
	}

	@Override
	public IDbgpSpawnpoint setSpawnpoint(URI uri, int lineNumber,
			boolean enabled) throws DbgpException {
		initSpawnpoints();
		final DbgpRequest request = createRequest(CMD_SET);
		request.addOption(OPTION_FILENAME, uri.toString());
		request.addOption(OPTION_LINE, lineNumber);
		request.addOption(OPTION_STATE,
				enabled ? STATE_ENABLED : STATE_DISABLED);
		final Element response = communicate(request);
		final String id = response.getAttribute(ATTR_ID);
		if (id != null) {
			return new DbgpSpawnpoint(id, parseState(response), uri,
					lineNumber);
		} else {
			return null;
		}
	}

	@Override
	public void updateSpawnpoint(String spawnpointId, boolean enabled)
			throws DbgpException {
		initSpawnpoints();
		final DbgpRequest request = createRequest(CMD_UPDATE);
		request.addOption(OPTION_ID, spawnpointId);
		request.addOption(OPTION_STATE,
				enabled ? STATE_ENABLED : STATE_DISABLED);
		communicate(request);
	}

	private static boolean parseState(final Element response) {
		return STATE_ENABLED.equals(response.getAttribute(ATTR_STATE));
	}

	private static int parseLineNumber(final Element point) {
		try {
			return Integer.parseInt(point.getAttribute(ATTR_LINENO));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

}
