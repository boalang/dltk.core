/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *

 *******************************************************************************/
package org.eclipse.dltk.internal.core.util;

import org.eclipse.dltk.internal.core.ModelElement;

public class MementoTokenizer {
	private static final String COUNT = Character.toString(ModelElement.JEM_COUNT);
	private static final String SCRIPTPROJECT = Character.toString(ModelElement.JEM_SCRIPTPROJECT);
	private static final String PROJECTFRAGMENT = Character.toString(ModelElement.JEM_PROJECTFRAGMENT);
	private static final String SCRIPTFOLDER = Character.toString(ModelElement.JEM_SCRIPTFOLDER);
	private static final String FIELD = Character.toString(ModelElement.JEM_FIELD);
	private static final String METHOD = Character.toString(ModelElement.JEM_METHOD);
	private static final String SOURCEMODULE = Character.toString(ModelElement.JEM_SOURCEMODULE);
	private static final String TYPE = Character.toString(ModelElement.JEM_TYPE);
	private static final String IMPORTDECLARATION = Character.toString(ModelElement.JEM_IMPORTDECLARATION);
	private static final String LOCALVARIABLE = Character.toString(ModelElement.JEM_LOCALVARIABLE);
	private static final String TYPE_PARAMETER = Character.toString(ModelElement.JEM_TYPE_PARAMETER);
	private static final String USER_ELEMENT = Character.toString(ModelElement.JEM_USER_ELEMENT);
	private final char[] memento;
	private final int length;
	private int index = 0;

	public MementoTokenizer(String memento) {
		this.memento = memento.toCharArray();
		this.length = this.memento.length;
	}

	public boolean hasMoreTokens() {
		return this.index < this.length;
	}

	public String nextToken() {
		int start = this.index;
		StringBuilder buffer = null;
		switch (this.memento[this.index++]) {
		case ModelElement.JEM_ESCAPE:
			buffer = new StringBuilder();
			buffer.append(this.memento[this.index]);
			start = ++this.index;
			break;
		case ModelElement.JEM_COUNT:
			return COUNT;
		case ModelElement.JEM_SCRIPTPROJECT:
			return SCRIPTPROJECT;
		case ModelElement.JEM_PROJECTFRAGMENT:
			return PROJECTFRAGMENT;
		case ModelElement.JEM_SCRIPTFOLDER:
			return SCRIPTFOLDER;
		case ModelElement.JEM_FIELD:
			return FIELD;
		case ModelElement.JEM_METHOD:
			return METHOD;
		case ModelElement.JEM_SOURCEMODULE:
			return SOURCEMODULE;
		case ModelElement.JEM_TYPE:
			return TYPE;
		case ModelElement.JEM_IMPORTDECLARATION:
			return IMPORTDECLARATION;
		case ModelElement.JEM_LOCALVARIABLE:
			return LOCALVARIABLE;
		case ModelElement.JEM_TYPE_PARAMETER:
			return TYPE_PARAMETER;
		case ModelElement.JEM_USER_ELEMENT:
			return USER_ELEMENT;
		}
		loop: while (this.index < this.length) {
			switch (this.memento[this.index]) {
			case ModelElement.JEM_ESCAPE:
				if (buffer == null)
					buffer = new StringBuilder();
				buffer.append(this.memento, start, this.index - start);
				start = ++this.index;
				break;
			case ModelElement.JEM_COUNT:
			case ModelElement.JEM_SCRIPTPROJECT:
			case ModelElement.JEM_PROJECTFRAGMENT:
			case ModelElement.JEM_SCRIPTFOLDER:
			case ModelElement.JEM_FIELD:
			case ModelElement.JEM_METHOD:
			case ModelElement.JEM_SOURCEMODULE:
			case ModelElement.JEM_TYPE:
			case ModelElement.JEM_IMPORTDECLARATION:
			case ModelElement.JEM_LOCALVARIABLE:
			case ModelElement.JEM_TYPE_PARAMETER:
			case ModelElement.JEM_USER_ELEMENT:
				break loop;
			}
			this.index++;
		}
		if (buffer != null) {
			buffer.append(this.memento, start, this.index - start);
			return buffer.toString();
		} else {
			return new String(this.memento, start, this.index - start);
		}
	}

}
