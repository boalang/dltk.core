/*******************************************************************************
 * Copyright (c) 2010, 2016 xored software, Inc. and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.compiler.env;

import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.IModelElement;

public class ModuleSource implements IModuleSource {

	private final IModelElement modelElement;
	private final String filename;
	private String string;
	private char[] charArray;

	public ModuleSource(char[] content) {
		this(Util.EMPTY_STRING, content);
	}

	public ModuleSource(String content) {
		this(Util.EMPTY_STRING, content);
	}

	public ModuleSource(String filename, char[] content) {
		assert content != null;
		this.modelElement = null;
		this.filename = filename;
		this.charArray = content;
	}

	public ModuleSource(String filename, String content) {
		assert content != null;
		this.modelElement = null;
		this.filename = filename;
		this.string = content;
	}

	public ModuleSource(String filename, IModelElement modelElement,
			char[] content) {
		assert content != null;
		this.modelElement = modelElement;
		this.filename = filename;
		this.charArray = content;
	}

	public ModuleSource(String filename, IModelElement modelElement,
			String content) {
		assert content != null;
		this.modelElement = modelElement;
		this.filename = filename;
		this.string = content;
	}

	@Override
	public char[] getContentsAsCharArray() {
		if (charArray == null) {
			charArray = string.toCharArray();
		}
		return charArray;
	}

	@Override
	public String getSourceContents() {
		if (string == null) {
			string = new String(charArray);
		}
		return string;
	}

	@Override
	public String getFileName() {
		return filename;
	}

	@Override
	public IModelElement getModelElement() {
		return modelElement;
	}

}
