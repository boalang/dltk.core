/*******************************************************************************
 * Copyright (c) 2010, 2017 xored software, Inc.
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
package org.eclipse.dltk.logconsole.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.logconsole.ILogConsole;
import org.eclipse.dltk.logconsole.ILogConsoleStream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class LogConsoleImpl extends IOConsole {

	private final DefaultLogConsole logConsole;

	public LogConsoleImpl(DefaultLogConsole logConsole) {
		super(logConsole.getConsoleType().computeTitle(
				logConsole.getIdentifier()), logConsole.getConsoleType()
				.getType(), null, true);
		this.logConsole = logConsole;
	}

	@Override
	protected void init() {
		super.init();
		logConsole.consoleInitialized();
	}

	@Override
	public void clearConsole() {
		super.clearConsole();
		logConsole.clear();
	}

	@Override
	protected void dispose() {
		logConsole.consoleDisposed();
		super.dispose();
	}

	private final Map<ILogConsoleStream, IOConsoleOutputStream> streams = new HashMap<>();

	protected void println(ILogConsoleStream stream, String message)
			throws IOException {
		IOConsoleOutputStream outputStream;
		synchronized (streams) {
			outputStream = streams.get(stream);
			if (outputStream == null) {
				outputStream = newOutputStream();
				setupColor(outputStream, stream);
			}
			streams.put(stream, outputStream);
		}
		outputStream.write(message + "\n");
	}

	private void setupColor(final IOConsoleOutputStream outputStream,
			ILogConsoleStream stream) {
		if (stream == ILogConsole.STDERR) {
			final Display current = Display.getCurrent();
			if (current != null) {
				if (!current.isDisposed())
					outputStream
							.setColor(current.getSystemColor(SWT.COLOR_RED));
			} else {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				if (!display.isDisposed())
					display.asyncExec(() -> {
						if (!display.isDisposed())
							outputStream.setColor(display
									.getSystemColor(SWT.COLOR_RED));
					});
			}
		}
	}
}
