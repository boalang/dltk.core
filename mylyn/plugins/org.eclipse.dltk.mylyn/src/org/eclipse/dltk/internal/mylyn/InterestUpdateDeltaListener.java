/*******************************************************************************
 * Copyright (c) 2004, 2017 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.internal.mylyn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class InterestUpdateDeltaListener implements IElementChangedListener {

	private static boolean asyncExecMode = true;

	@Override
	public void elementChanged(ElementChangedEvent event) {
		IModelElementDelta delta = event.getDelta();
		handleDelta(delta.getAffectedChildren());
	}

	/**
	 * Only handles first addition/removal
	 */
	private void handleDelta(IModelElementDelta[] delta) {
		try {
			IModelElement added = null;
			IModelElement removed = null;
			for (IModelElementDelta child : delta) {
				if (child.getElement() instanceof ISourceModule
						&& ((ISourceModule) child.getElement()).getOwner() != null) {
					// see bug 195361, do not reduce interest of temporary working copy
					return;
				}

				if (child.getKind() == IModelElementDelta.ADDED) {
					if (added == null) {
						added = child.getElement();
					}
				} else if (child.getKind() == IModelElementDelta.REMOVED) {
					if (removed == null) {
						removed = child.getElement();
					}
				}
				handleDelta(child.getAffectedChildren());
			}

			if (added != null && removed != null) {
				IInteractionElement element = ContextCore.getContextManager().getElement(removed.getHandleIdentifier());
				if (element != null) {
					resetHandle(element, added.getHandleIdentifier());
				}
			} else if (removed != null) {

				IInteractionElement element = ContextCore.getContextManager().getElement(removed.getHandleIdentifier());
				if (element != null) {
					delete(element);
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, DLTKUiBridgePlugin.ID_PLUGIN, "Delta update failed", t)); //$NON-NLS-1$
		}
	}

	private void resetHandle(final IInteractionElement element, final String newHandle) {
		if (!asyncExecMode) {
			ContextCore.getContextManager().updateHandle(element, newHandle);
		} else {
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				workbench.getDisplay()
						.asyncExec(() -> ContextCore.getContextManager().updateHandle(element, newHandle));
			}
		}
	}

	private void delete(final IInteractionElement element) {
		List<IInteractionElement> elements = new ArrayList<>();
		elements.add(element);
		if (!asyncExecMode) {
			ContextCore.getContextManager().deleteElements(elements);
		} else {
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				workbench.getDisplay().asyncExec(() -> ContextCore.getContextManager().deleteElements(elements));
			}
		}
	}

	/**
	 * For testing
	 */
	public static void setAsyncExecMode(boolean asyncExecMode) {
		InterestUpdateDeltaListener.asyncExecMode = asyncExecMode;
	}
}
