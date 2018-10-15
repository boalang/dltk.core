/*******************************************************************************
 * Copyright (c) 2011 NumberFour AG
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.core.hierarchy;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ITypeHierarchyBuilder;
import org.eclipse.dltk.utils.NatureExtensionManager;

public class TypeHierarchyBuilders extends
		NatureExtensionManager<ITypeHierarchyBuilder> {

	private static TypeHierarchyBuilders INSTANCE = null;

	private static synchronized TypeHierarchyBuilders get() {
		if (INSTANCE == null) {
			INSTANCE = new TypeHierarchyBuilders();
		}
		return INSTANCE;
	}

	public TypeHierarchyBuilders() {
		super(DLTKCore.PLUGIN_ID + ".typeHierarchy",
				ITypeHierarchyBuilder.class);
	}

	public static ITypeHierarchy getTypeHierarchy(IType type,
			ITypeHierarchy.Mode mode, IProgressMonitor monitor) {
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(type);
		if (toolkit != null) {
			final ITypeHierarchyBuilder[] builders = get().getInstances(
					toolkit.getNatureId());
			if (builders != null) {
				for (ITypeHierarchyBuilder builder : builders) {
					final ITypeHierarchy hierarchy = builder.build(type, mode,
							monitor);
					if (hierarchy != null) {
						return hierarchy;
					}
				}
			}
		}
		return null;
	}
}
