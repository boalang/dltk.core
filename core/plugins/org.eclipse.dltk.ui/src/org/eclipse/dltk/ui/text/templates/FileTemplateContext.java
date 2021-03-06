/*******************************************************************************
 * Copyright (c) 2007, 2017 Wind River Systems, Inc. and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Anton Leherbauer (Wind River Systems) - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.ui.text.templates;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.ILineTracker;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateTranslator;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

/**
 * A template context for plain file resources.
 */
public class FileTemplateContext extends TemplateContext {

	private final String fLineDelimiter;

	public FileTemplateContext(TemplateContextType contextType, String lineDelimiter) {
		super(contextType);
		fLineDelimiter = lineDelimiter;
	}

	@Override
	public TemplateBuffer evaluate(Template template) throws BadLocationException, TemplateException {
		// test that all variables are defined
		Iterator iterator = getContextType().resolvers();
		while (iterator.hasNext()) {
			TemplateVariableResolver var = (TemplateVariableResolver) iterator.next();
			if (var.getClass() == FileTemplateContextType.FileTemplateVariableResolver.class) {
				Assert.isNotNull(getVariable(var.getType()), "Variable " + var.getType() + " not defined"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		if (!canEvaluate(template))
			return null;

		String pattern = changeLineDelimiter(template.getPattern(), fLineDelimiter);

		TemplateTranslator translator = new TemplateTranslator();
		TemplateBuffer buffer = translator.translate(pattern);
		getContextType().resolve(buffer, this);
		return buffer;
	}

	private static String changeLineDelimiter(String code, String lineDelim) {
		try {
			ILineTracker tracker = new DefaultLineTracker();
			tracker.set(code);
			int nLines = tracker.getNumberOfLines();
			if (nLines == 1) {
				return code;
			}

			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < nLines; i++) {
				if (i != 0) {
					buf.append(lineDelim);
				}
				IRegion region = tracker.getLineInformation(i);
				String line = code.substring(region.getOffset(), region.getOffset() + region.getLength());
				buf.append(line);
			}
			return buf.toString();
		} catch (BadLocationException e) {
			// can not happen
			return code;
		}
	}

	@Override
	public boolean canEvaluate(Template template) {
		return true;
	}

	public void setResourceVariables(IFile file) {
		setVariable(FileTemplateContextType.FILENAME, file.getName());
		setVariable(FileTemplateContextType.FILEBASE, new Path(file.getName()).removeFileExtension().lastSegment());
		IPath location = file.getLocation();
		setVariable(FileTemplateContextType.FILELOCATION, location != null ? location.toOSString() : Util.EMPTY_STRING);
		setVariable(FileTemplateContextType.FILEPATH, file.getFullPath().toString());
		setVariable(FileTemplateContextType.PROJECTNAME, file.getProject().getName());
	}

}
