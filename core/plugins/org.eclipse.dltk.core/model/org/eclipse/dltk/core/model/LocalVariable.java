/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core.model;

import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IBuffer;
import org.eclipse.dltk.core.ILocalVariable;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceRange;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.SourceRefElement;
import org.eclipse.dltk.internal.core.util.MementoTokenizer;
import org.eclipse.dltk.internal.core.util.Util;

public class LocalVariable extends SourceRefElement implements ILocalVariable {

	private String name;
	private int declarationSourceStart, declarationSourceEnd;
	private int nameStart, nameEnd;
	private String type;

	/**
	 * @param parent                 the parent of the variable
	 * @param name                   the name of the variable
	 * @param declarationSourceStart the position of the statement start
	 * @param declarationSourceEnd   the position of the statement end (including -
	 *                               the position of the last character)
	 * @param nameStart              the position of the variable name start
	 * @param nameEnd                the position of the variable name end
	 *                               (including - the position of the last
	 *                               character)
	 * @param type
	 */
	public LocalVariable(IModelElement parent, String name, int declarationSourceStart, int declarationSourceEnd,
			int nameStart, int nameEnd, String type) {
		super((ModelElement) parent);
		this.name = name;
		this.declarationSourceStart = declarationSourceStart;
		this.declarationSourceEnd = declarationSourceEnd;
		this.nameStart = nameStart;
		this.nameEnd = nameEnd;
		this.type = type;
	}

	@Override
	protected void closing(Object info) {
		// a local variable has no info
	}

	@Override
	protected Object createElementInfo() {
		// a local variable has no info
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LocalVariable))
			return false;
		LocalVariable other = (LocalVariable) o;
		return this.declarationSourceStart == other.declarationSourceStart
				&& this.declarationSourceEnd == other.declarationSourceEnd && this.nameStart == other.nameStart
				&& this.nameEnd == other.nameEnd && super.equals(o);
	}

	@Override
	public boolean exists() {
		return this.parent.exists();
	}

	@Override
	protected void generateInfos(Object info, HashMap newElements, IProgressMonitor pm) {
		// a local variable has no info
	}

	@Override
	public IModelElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner owner) {
		switch (token.charAt(0)) {
		case JEM_COUNT:
			return getHandleUpdatingCountFromMemento(memento, owner);
		}
		return this;
	}

	@Deprecated
	public void getHandleMemento(StringBuffer buff) {

	}

	@Override
	public void getHandleMemento(StringBuilder buff) {
		((ModelElement) getParent()).getHandleMemento(buff);
		buff.append(getHandleMementoDelimiter());
		buff.append(this.name);
		buff.append(JEM_COUNT);
		buff.append(this.declarationSourceStart);
		buff.append(JEM_COUNT);
		buff.append(this.declarationSourceEnd);
		buff.append(JEM_COUNT);
		buff.append(this.nameStart);
		buff.append(JEM_COUNT);
		buff.append(this.nameEnd);
		buff.append(JEM_COUNT);
		escapeMementoName(buff, this.type);
		if (this.occurrenceCount > 1) {
			buff.append(JEM_COUNT);
			buff.append(this.occurrenceCount);
		}
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return ModelElement.JEM_LOCALVARIABLE;
	}

	@Override
	public IResource getCorrespondingResource() {
		return null;
	}

	@Override
	public String getElementName() {
		return this.name;
	}

	@Override
	public int getElementType() {
		return LOCAL_VARIABLE;
	}

	@Override
	public ISourceRange getNameRange() {
		return new SourceRange(this.nameStart, this.nameEnd - this.nameStart + 1);
	}

	@Override
	public IPath getPath() {
		return this.parent.getPath();
	}

	/**
	 * @see ISourceReference
	 */
	@Override
	public String getSource() throws ModelException {
		IOpenable openable = this.parent.getOpenableParent();
		IBuffer buffer = openable.getBuffer();
		if (buffer == null) {
			return null;
		}
		ISourceRange range = getSourceRange();
		int offset = range.getOffset();
		int length = range.getLength();
		if (offset == -1 || length == 0) {
			return null;
		}
		try {
			return buffer.getText(offset, length);
		} catch (RuntimeException e) {
			return null;
		}
	}

	@Override
	public ISourceRange getSourceRange() {
		return new SourceRange(this.declarationSourceStart,
				this.declarationSourceEnd - this.declarationSourceStart + 1);
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public IResource getUnderlyingResource() throws ModelException {
		return this.parent.getUnderlyingResource();
	}

	@Override
	public int hashCode() {
		return Util.combineHashCodes(this.parent.hashCode(), this.nameStart);
	}

	@Override
	public boolean isStructureKnown() throws ModelException {
		return true;
	}

	@Override
	protected void toStringInfo(int tab, StringBuilder buffer, Object info, boolean showResolvedInfo) {
		buffer.append(tabString(tab));
		if (info != NO_INFO && getType() != null) {
			buffer.append(getType());
			buffer.append(" "); //$NON-NLS-1$
		}
		toStringName(buffer);
	}

}
