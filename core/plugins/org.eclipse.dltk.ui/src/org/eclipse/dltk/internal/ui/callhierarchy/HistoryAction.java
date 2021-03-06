/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.callhierarchy;

import java.text.MessageFormat;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.ui.ModelElementLabelProvider;
import org.eclipse.dltk.ui.ScriptElementImageProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Action used for the type hierarchy forward / backward buttons
 */
class HistoryAction extends Action {
	private static ModelElementLabelProvider fLabelProvider = new ModelElementLabelProvider(
			ModelElementLabelProvider.SHOW_POST_QUALIFIED | ModelElementLabelProvider.SHOW_PARAMETERS
					| ModelElementLabelProvider.SHOW_RETURN_TYPE);
	private CallHierarchyViewPart fView;
	private IMethod fMethod;

	public HistoryAction(CallHierarchyViewPart viewPart, IMethod element) {
		super("", AS_RADIO_BUTTON); //$NON-NLS-1$
		fView = viewPart;
		fMethod = element;

		String elementName = getElementLabel(element);
		setText(elementName);
		setImageDescriptor(getImageDescriptor(element));

		setDescription(MessageFormat.format(CallHierarchyMessages.HistoryAction_description, elementName));
		setToolTipText(MessageFormat.format(CallHierarchyMessages.HistoryAction_tooltip, elementName));

//        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.CALL_HIERARCHY_HISTORY_ACTION);
		if (DLTKCore.DEBUG) {
			System.err.println("Add help support here..."); //$NON-NLS-1$
		}
	}

	private ImageDescriptor getImageDescriptor(IModelElement elem) {
		ScriptElementImageProvider imageProvider = new ScriptElementImageProvider();
		ImageDescriptor desc = imageProvider.getBaseImageDescriptor(elem, 0);
		imageProvider.dispose();

		return desc;
	}

	@Override
	public void run() {
		fView.gotoHistoryEntry(fMethod);
	}

	/**
	 * @param element
	 * @return String
	 */
	private String getElementLabel(IModelElement element) {
		return fLabelProvider.getText(element);
	}
}
