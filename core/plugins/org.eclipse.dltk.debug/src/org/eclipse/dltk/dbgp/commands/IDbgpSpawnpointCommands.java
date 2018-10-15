/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
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
package org.eclipse.dltk.dbgp.commands;

import java.net.URI;

import org.eclipse.dltk.dbgp.IDbgpSpawnpoint;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;

public interface IDbgpSpawnpointCommands {

	/**
	 * Sets the spawn point at the specified location. Returns the id of created
	 * spawn point.
	 * 
	 * @param uri
	 * @param lineNumber
	 * @param enabled
	 * @return
	 * @throws DbgpException
	 */
	IDbgpSpawnpoint setSpawnpoint(URI uri, int lineNumber, boolean enabled)
			throws DbgpException;

	/**
	 * Retrieves the information about the specified spawn point
	 * 
	 * @param spawnpointId
	 * @return
	 * @throws DbgpException
	 */
	IDbgpSpawnpoint getSpawnpoint(String spawnpointId) throws DbgpException;

	/**
	 * Updates the specified spawn point
	 * 
	 * @param spawnpointId
	 * @param enabled
	 * @return
	 * @throws DbgpException
	 */
	void updateSpawnpoint(String spawnpointId, boolean enabled)
			throws DbgpException;

	/**
	 * Removes the specified spawn point
	 * 
	 * @param spawnpointId
	 * @throws DbgpException
	 */
	void removeSpawnpoint(String spawnpointId) throws DbgpException;

	/**
	 * Retrieves all spawn points. If there are no spawn points empty array is
	 * returned.
	 * 
	 * @return
	 * @throws DbgpException
	 */
	IDbgpSpawnpoint[] listSpawnpoints() throws DbgpException;

}
