/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.callhierarchy;

import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.dltk.internal.corext.callhierarchy.MethodWrapper;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.ScriptElementImageProvider;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;

class CallHierarchyLabelProvider extends AppearanceAwareLabelProvider {
//	private final static long FULLY_QUALIFIED = Long.valueOf(ScriptElementLabels.F_FULLY_QUALIFIED | ScriptElementLabels.M_FULLY_QUALIFIED | ScriptElementLabels.I_FULLY_QUALIFIED | ScriptElementLabels.T_FULLY_QUALIFIED ).longValue();
//
//    private static final long TEXTFLAGS= DEFAULT_TEXTFLAGS | FULLY_QUALIFIED | ScriptElementLabels.P_POST_QUALIFIED | ScriptElementLabels.P_COMPRESSED;// | ScriptElementLabels.M_FULLY_QUALIFIED | ScriptElementLabels.PREPEND_ROOT_PATH;
//    private static final int IMAGEFLAGS= DEFAULT_IMAGEFLAGS | ScriptElementImageProvider.SMALL_ICONS;
	private static final long TEXTFLAGS = DEFAULT_TEXTFLAGS | ScriptElementLabels.ALL_POST_QUALIFIED
			| ScriptElementLabels.P_COMPRESSED | ScriptElementLabels.APPEND_FILE | ScriptElementLabels.APPEND_ROOT_PATH;
	private static final int IMAGEFLAGS = DEFAULT_IMAGEFLAGS | ScriptElementImageProvider.SMALL_ICONS;

	private ILabelDecorator fDecorator;

	CallHierarchyLabelProvider() {
		super(TEXTFLAGS, IMAGEFLAGS, DLTKUIPlugin.getDefault().getPreferenceStore());
		fDecorator = new CallHierarchyLabelDecorator();
	}

	@Override
	public Image getImage(Object element) {
		Image result = null;
		if (element instanceof MethodWrapper) {
			MethodWrapper methodWrapper = (MethodWrapper) element;

			if (methodWrapper.getMember() != null) {
				result = fDecorator.decorateImage(super.getImage(methodWrapper.getMember()), methodWrapper);
			}
		} else if (isPendingUpdate(element)) {
			return null;
		} else {
			result = super.getImage(element);
		}

		return result;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof MethodWrapper) {
			MethodWrapper methodWrapper = (MethodWrapper) element;

			if (methodWrapper.getMember() != null) {
				return getElementLabel(methodWrapper);
			}
			return CallHierarchyMessages.CallHierarchyLabelProvider_root;
		} else if (element == TreeTermination.SEARCH_CANCELED) {
			return CallHierarchyMessages.CallHierarchyLabelProvider_searchCanceled;
		} else if (isPendingUpdate(element)) {
			return CallHierarchyMessages.CallHierarchyLabelProvider_updatePending;
		}

		return CallHierarchyMessages.CallHierarchyLabelProvider_noMethodSelected;
	}

	private boolean isPendingUpdate(Object element) {
		return element instanceof IWorkbenchAdapter;
	}

	private String getElementLabel(MethodWrapper methodWrapper) {
		String label = super.getText(methodWrapper.getMember());

		Collection callLocations = methodWrapper.getMethodCall().getCallLocations();

		if ((callLocations != null) && (callLocations.size() > 1)) {
			return MessageFormat.format(CallHierarchyMessages.CallHierarchyLabelProvider_matches, label,
					String.valueOf(callLocations.size()));
		}

		return label;
	}
}
