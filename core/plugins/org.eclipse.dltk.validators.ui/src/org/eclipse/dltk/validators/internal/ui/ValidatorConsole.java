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
package org.eclipse.dltk.validators.internal.ui;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.console.IOConsole;

public class ValidatorConsole extends IOConsole {

	public static final String TYPE = "org.eclipse.dltk.validators.ConsoleValidatorOutput"; //$NON-NLS-1$

	private final String baseName;

	private final String initialName;
	private boolean closed = false;

	/**
	 * @param name
	 */
	public ValidatorConsole(String name) {
		super(formatConsoleName(name), TYPE, null);
		this.baseName = name;
		this.initialName = getName();
	}

	private static String formatConsoleName(String name) {
		final String timestamp = DateFormat
				.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)
				.format(new Date(System.currentTimeMillis()));
		final String message = Messages.AbstractValidateSelectionWithConsole_dltkValidatorOutput;
		return NLS.bind(message, name, timestamp);
	}

	public void close() {
		if (closed) {
			return;
		}
		closed = true;
		Runnable r = () -> setName(
				Messages.ValidatorConsole_terminated + initialName);
		DLTKUIPlugin.getStandardDisplay().asyncExec(r);
	}

	public String getInitialName() {
		return initialName;
	}

	/**
	 * @return the closed
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * @return the baseName
	 */
	public String getBaseName() {
		return baseName;
	}

}
