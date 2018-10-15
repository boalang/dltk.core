/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.editor;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.internal.ui.ExternalSourceModuleEditorInputFactory;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PlatformUI;

public class ExternalStorageEditorInput implements IEditorInput,
		IStorageEditorInput {
	private IStorage fStorage;

	public ExternalStorageEditorInput(IStorage storage) {
		this.fStorage = storage;
	}

	@Override
	public boolean exists() {
		return fStorage != null;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return PlatformUI.getWorkbench().getEditorRegistry()
				.getImageDescriptor(this.fStorage.getName());
	}

	@Override
	public String getName() {
		return fStorage.getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		if (fStorage instanceof IExternalSourceModule) {
			return ExternalSourceModuleEditorInputFactory
					.createPersistableElement((IExternalSourceModule) fStorage);
		}
		return null;
	}

	@Override
	public String getToolTipText() {
		IPath path = fStorage.getFullPath();
		if (path == null) {
			return ""; //$NON-NLS-1$
		}
		if (fStorage instanceof IModelElement) {
			IModelElement modelElement = (IModelElement) fStorage;
			IDLTKUILanguageToolkit uiToolkit = DLTKUILanguageManager
					.getLanguageToolkit(modelElement);
			ScriptElementLabels labels = uiToolkit.getScriptElementLabels();
			String label = labels.getTextLabel(fStorage,
					ScriptElementLabels.PREPEND_ROOT_PATH);
			return label;
		}

		return path.toOSString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == IModelElement.class
				&& fStorage instanceof IModelElement) {
			return (T) fStorage;
		}
		return null;
	}

	@Override
	public IStorage getStorage() {
		return this.fStorage;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ExternalStorageEditorInput)) {
			return false;
		}
		ExternalStorageEditorInput other = (ExternalStorageEditorInput) obj;
		return fStorage.equals(other.fStorage);
	}

	@Override
	public int hashCode() {
		return fStorage.hashCode();
	}

	public IPath getPath(Object element) {
		return fStorage.getFullPath();
	}
}
