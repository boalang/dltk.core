/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.typehierarchy;

import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.hierarchy.TypeHierarchy;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.ScriptElementImageDescriptor;
import org.eclipse.dltk.ui.ScriptElementImageProvider;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.dltk.ui.viewsupport.ScriptUILabelProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * Label provider for the hierarchy viewers. Types in the hierarchy that are not
 * belonging to the input scope are rendered differntly.
 */
public class HierarchyLabelProvider extends AppearanceAwareLabelProvider {

	private static class FocusDescriptor extends CompositeImageDescriptor {
		private ImageDescriptor fBase;

		public FocusDescriptor(ImageDescriptor base) {
			fBase = base;
		}

		@Override
		protected void drawCompositeImage(int width, int height) {
			drawImage(getImageData(fBase), 0, 0);
			// drawImage(getImageData(DLTKPluginImages.DESC_OVR_FOCUS), 0, 0);
		}

		private ImageData getImageData(ImageDescriptor descriptor) {
			ImageData data = descriptor.getImageData();
			// see bug 51965:
			// getImageData can
			// return null
			if (data == null) {
				data = DEFAULT_IMAGE_DATA;
				DLTKUIPlugin
						.logErrorMessage("Image data not available: " + descriptor.toString()); //$NON-NLS-1$
			}
			return data;
		}

		@Override
		protected Point getSize() {
			return ScriptElementImageProvider.BIG_SIZE;
		}

		@Override
		public int hashCode() {
			return fBase.hashCode();
		}

		@Override
		public boolean equals(Object object) {
			return object != null
					&& FocusDescriptor.class.equals(object.getClass())
					&& ((FocusDescriptor) object).fBase.equals(fBase);
		}
	}

	private Color fGrayedColor;
	private Color fSpecialColor;

	private ViewerFilter fFilter;

	private TypeHierarchyLifeCycle fHierarchy;

	public HierarchyLabelProvider(TypeHierarchyLifeCycle lifeCycle,
			IPreferenceStore store) {
		super(DEFAULT_TEXTFLAGS | ScriptElementLabels.USE_RESOLVED,
				DEFAULT_IMAGEFLAGS, store);
		fHierarchy = lifeCycle;
		fFilter = null;
	}

	/**
	 * @return Returns the filter.
	 */
	public ViewerFilter getFilter() {
		return fFilter;
	}

	/**
	 * @param filter
	 *            The filter to set.
	 */
	public void setFilter(ViewerFilter filter) {
		fFilter = filter;
	}

	protected boolean isDifferentScope(IType type) {
		if (fFilter != null && !fFilter.select(null, null, type)) {
			return true;
		}

		IModelElement input = fHierarchy.getInputElement();
		if (input == null || input.getElementType() == IModelElement.TYPE) {
			return false;
		}

		IModelElement parent = type.getAncestor(input.getElementType());
		if (input.getElementType() == IModelElement.PROJECT_FRAGMENT) {
			if (parent == null
					|| parent.getElementName().equals(input.getElementName())) {
				return false;
			}
		} else if (input.equals(parent)) {
			return false;
		}
		return true;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof CumulativeType) {
			CumulativeType cType = (CumulativeType) element;
			return super.getText(cType.getFirst());
		} else if (element instanceof CumulativeType.Part) {
			CumulativeType.Part part = (CumulativeType.Part) element;
			return ScriptElementLabels.getDefault().getTextLabel(
					part.type.getSourceModule(),
					ScriptElementLabels.ALL_FULLY_QUALIFIED);
		} else {
			return super.getText(element);
		}
	}

	private boolean isCustomTypeHierarchy() {
		final ITypeHierarchy hierarchy = fHierarchy.getHierarchy();
		return hierarchy != null && !(hierarchy instanceof TypeHierarchy);
	}

	private ILabelProvider fScriptLabelProvider = null;

	private ILabelProvider getScriptLabelProvider() {
		if (fScriptLabelProvider == null) {
			fScriptLabelProvider = new ScriptUILabelProvider();
		}
		return fScriptLabelProvider;
	}

	@Override
	public Image getImage(Object element) {
		Image result = null;
		if (element instanceof IType) {
			if (isCustomTypeHierarchy()) {
				// TODO (alex) think of better solution
				return getScriptLabelProvider().getImage(element);
			}
			ImageDescriptor desc = getTypeImageDescriptor((IType) element);
			if (desc != null) {
				if (element.equals(fHierarchy.getInputElement())) {
					desc = new FocusDescriptor(desc);
				}
				result = DLTKUIPlugin.getImageDescriptorRegistry().get(desc);
			}
		} else if (element instanceof CumulativeType) {
			final CumulativeType cType = (CumulativeType) element;
			ImageDescriptor desc = getTypeImageDescriptor(cType.getFirst());
			if (desc != null) {
				if (cType.contains(fHierarchy.getInputElement())) {
					desc = new FocusDescriptor(desc);
				}
				result = DLTKUIPlugin.getImageDescriptorRegistry().get(desc);
			}
		} else if (element instanceof CumulativeType.Part) {
			final CumulativeType.Part part = (CumulativeType.Part) element;
			result = super.getImage(part.type.getSourceModule());
			ImageDescriptor desc = new ScriptElementImageDescriptor(
					ImageDescriptor.createFromImage(result), 0,
					ScriptElementImageProvider.BIG_SIZE);
			result = DLTKUIPlugin.getImageDescriptorRegistry().get(desc);
		} else {
			result = fImageLabelProvider.getImageLabel(element,
					evaluateImageFlags(element));
		}
		return decorateImage(result, element);
	}

	private ImageDescriptor getTypeImageDescriptor(IType type) {
		ITypeHierarchy hierarchy = fHierarchy.getHierarchy();
		if (hierarchy == null) {
			return new ScriptElementImageDescriptor(
					DLTKPluginImages.DESC_OBJS_CLASS, 0,
					ScriptElementImageProvider.BIG_SIZE);
		}

		int flags = hierarchy.getCachedFlags(type);
		if (flags == -1) {
			try {
				flags = type.getFlags();
				if (hierarchy instanceof TypeHierarchy) {
					((TypeHierarchy) hierarchy).cacheFlags(type, flags);
				}
			} catch (ModelException e) {
			}
		}
		// boolean isInner= (type.getDeclaringType() != null);

		ImageDescriptor desc = ScriptElementImageProvider
				.getTypeImageDescriptor(flags, isDifferentScope(type));
		// (isInner, false, flags, isDifferentScope (type) );
		boolean isInterface = Flags.isInterface(flags);
		int adornmentFlags = 0;
		if (Flags.isFinal(flags)) {
			adornmentFlags |= ScriptElementImageDescriptor.FINAL;
		}
		if (Flags.isAbstract(flags) && !isInterface) {
			adornmentFlags |= ScriptElementImageDescriptor.ABSTRACT;
		}
		if (Flags.isStatic(flags)) {
			adornmentFlags |= ScriptElementImageDescriptor.STATIC;
		}

		return new ScriptElementImageDescriptor(desc, adornmentFlags,
				ScriptElementImageProvider.BIG_SIZE);
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof IMethod) {
			if (fSpecialColor == null) {
				fSpecialColor = Display.getCurrent().getSystemColor(
						SWT.COLOR_DARK_BLUE);
			}
			return fSpecialColor;
		} else if (element instanceof IType
				&& isDifferentScope((IType) element)) {
			if (fGrayedColor == null) {
				fGrayedColor = Display.getCurrent().getSystemColor(
						SWT.COLOR_DARK_GRAY);
			}
			return fGrayedColor;
		}
		return null;
	}

}
