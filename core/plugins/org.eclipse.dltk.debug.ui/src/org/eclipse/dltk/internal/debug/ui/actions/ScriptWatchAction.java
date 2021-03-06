/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.debug.ui.actions;

import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IWatchExpression;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.dltk.debug.core.model.IScriptVariable;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;

public class ScriptWatchAction extends ScriptEvaluationAction {
	private void createWatchExpression(String snippet) {
		IWatchExpression expression = DebugPlugin.getDefault()
				.getExpressionManager().newWatchExpression(snippet);
		DebugPlugin.getDefault().getExpressionManager()
				.addExpression(expression);

		IAdaptable object = DebugUITools.getDebugContext();
		IDebugElement context = null;
		if (object instanceof IDebugElement) {
			context = (IDebugElement) object;
		} else if (object instanceof ILaunch) {
			context = ((ILaunch) object).getDebugTarget();
		}

		expression.setExpressionContext(context);
	}

	@Override
	public void run() {
		Object selectedObject = getSelectedObject();

		if (selectedObject instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) selectedObject;
			Iterator<?> elements = selection.iterator();
			while (elements.hasNext()) {
				try {
					createWatchExpression(
							((IScriptVariable) elements.next()).getName());
				} catch (DebugException e) {
					DLTKDebugUIPlugin.log(e);
					return;
				}
			}
		} else if (selectedObject instanceof String) {
			createWatchExpression((String) selectedObject);
		}

		showExpressionView();
	}
}
