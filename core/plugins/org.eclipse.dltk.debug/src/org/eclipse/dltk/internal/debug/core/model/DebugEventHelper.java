/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.debug.core.model;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugElement;

public final class DebugEventHelper {
	private DebugEventHelper() {
	}

	private static void fireEvent(DebugEvent event) {
		if (DebugPlugin.getDefault() != null) {
			DebugPlugin.getDefault().fireDebugEventSet(
					new DebugEvent[] { event });
		}
	}

	public static void fireCreateEvent(IDebugElement element) {
		fireEvent(new DebugEvent(element, DebugEvent.CREATE));
	}

	public static void fireResumeEvent(IDebugElement element, int detail) {
		fireEvent(new DebugEvent(element, DebugEvent.RESUME, detail));

	}

	public static void fireSuspendEvent(IDebugElement element, int detail) {
		fireEvent(new DebugEvent(element, DebugEvent.SUSPEND, detail));
	}

	public static void fireTerminateEvent(IDebugElement element) {
		fireEvent(new DebugEvent(element, DebugEvent.TERMINATE));
	}

	public static void fireChangeEvent(IDebugElement element) {
		fireEvent(new DebugEvent(element, DebugEvent.CHANGE));
	}

	public static void fireExtendedEvent(Object eventSource, int details) {
		fireEvent(new DebugEvent(eventSource, DebugEvent.MODEL_SPECIFIC,
				details));
	}
}
