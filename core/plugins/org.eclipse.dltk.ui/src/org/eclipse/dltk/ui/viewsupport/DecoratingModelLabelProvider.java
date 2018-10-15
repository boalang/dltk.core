/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.ui.viewsupport;

import org.eclipse.dltk.ui.ProblemsLabelDecorator;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DecorationContext;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;

/**
 * @deprecated use StyledDecoratingModelLabelProvider instead
 */
@Deprecated
public class DecoratingModelLabelProvider extends DecoratingLabelProvider
		implements IColorProvider {

	/**
	 * Decorating label provider for DLTK. Combines a ScriptUILabelProvider with
	 * problem and override indicuator with the workbench decorator (label
	 * decorator extension point).
	 */
	public DecoratingModelLabelProvider(ScriptUILabelProvider labelProvider) {
		this(labelProvider, true);
	}

	/**
	 * Decorating label provider for dltk. Combines a ScriptUILabelProvider (if
	 * enabled with problem indicator) with the workbench decorator (label
	 * decorator extension point).
	 */
	public DecoratingModelLabelProvider(ScriptUILabelProvider labelProvider,
			boolean errorTick) {
		this(labelProvider, errorTick, true);
	}

	/**
	 * Decorating label provider for dltk. Combines a ScriptUILabelProvider (if
	 * enabled with problem indicator) with the workbench decorator (label
	 * decorator extension point).
	 */
	public DecoratingModelLabelProvider(ScriptUILabelProvider labelProvider,
			boolean errorTick, boolean flatPackageMode) {
		super(labelProvider, PlatformUI.getWorkbench().getDecoratorManager()
				.getLabelDecorator());

		if (errorTick) {
			labelProvider.addLabelDecorator(new ProblemsLabelDecorator(null));
		}
		setFlatPackageMode(flatPackageMode);
	}

	/**
	 * Tells the label decorator if the view presents packages flat or
	 * hierarchical.
	 * 
	 * @param enable
	 *            If set, packages are presented in flat mode.
	 */
	public void setFlatPackageMode(boolean enable) {
		if (enable) {
			setDecorationContext(DecorationContext.DEFAULT_CONTEXT);
		} else {
			setDecorationContext(DecorationContext.DEFAULT_CONTEXT);
			// TODO setDecorationContext(HierarchicalDecorationContext.CONTEXT);
		}
	}

	@Override
	public Color getForeground(Object element) {
		// label provider is a ScriptUILabelProvider
		return ((IColorProvider) getLabelProvider()).getForeground(element);
	}

	@Override
	public Color getBackground(Object element) {
		// label provider is a ScriptUILabelProvider
		return ((IColorProvider) getLabelProvider()).getBackground(element);
	}

}
