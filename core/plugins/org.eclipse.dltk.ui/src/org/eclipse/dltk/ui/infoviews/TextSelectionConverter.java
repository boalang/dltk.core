/*******************************************************************************
 * Copyright (c) 2000, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.ui.infoviews;

import org.eclipse.dltk.core.ICodeAssist;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IWorkingCopyManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * Helper class to convert text selections to Script elements.
 *
 *
 */
class TextSelectionConverter {

	/** Prevent instance creation. */
	private TextSelectionConverter() {
	}

	/**
	 * Finds and returns the Script elements for the given editor selection.
	 *
	 * @param editor
	 *                      the Script editor
	 * @param selection
	 *                      the text selection
	 * @return the Script elements for the given editor selection
	 * @throws ModelException
	 */
	public static IModelElement[] codeResolve(IEditorPart editor,
			ITextSelection selection) throws ModelException {
		return codeResolve(getInput(editor), selection);
	}

	/**
	 * Finds and returns the Script element that contains the text selection in
	 * the given editor.
	 *
	 * @param editor
	 *                      the Script editor
	 * @param selection
	 *                      the text selection
	 * @return the Script elements for the given editor selection
	 * @throws ModelException
	 */
	public static IModelElement getElementAtOffset(IEditorPart editor,
			ITextSelection selection) throws ModelException {
		return getElementAtOffset(getInput(editor), selection);
	}

	// -------------------- Helper methods --------------------

	private static IModelElement getInput(IEditorPart editor) {
		if (editor == null)
			return null;
		IEditorInput input = editor.getEditorInput();
		IWorkingCopyManager manager = DLTKUIPlugin.getDefault()
				.getWorkingCopyManager();
		return manager.getWorkingCopy(input);
	}

	private static IModelElement[] codeResolve(IModelElement input,
			ITextSelection selection) throws ModelException {
		if (input instanceof ICodeAssist) {
			if (input instanceof ISourceModule) {
				ISourceModule cunit = (ISourceModule) input;
				if (cunit.isWorkingCopy())
					ScriptModelUtil.reconcile(cunit);
			}
			IModelElement[] elements = ((ICodeAssist) input)
					.codeSelect(selection.getOffset(), selection.getLength());
			if (elements != null && elements.length > 0)
				return elements;
		}
		return ScriptModelUtil.NO_ELEMENTS;
	}

	private static IModelElement getElementAtOffset(IModelElement input,
			ITextSelection selection) throws ModelException {
		if (input instanceof ISourceModule) {
			ISourceModule cunit = (ISourceModule) input;
			if (cunit.isWorkingCopy())
				ScriptModelUtil.reconcile(cunit);
			IModelElement ref = cunit.getElementAt(selection.getOffset());
			if (ref == null) {
				return input;
			}
			return ref;
		}
		return null;
	}
}
