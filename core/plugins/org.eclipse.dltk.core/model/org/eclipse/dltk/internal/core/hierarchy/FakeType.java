/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.core.hierarchy;

import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceRange;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.SourceType;

/**
 * This is a fake model element that can live independently from the DLTK model
 * manager.
 */
public class FakeType extends SourceType {

	private int flags = Modifiers.AccPublic;
	private int offset;
	private int length;
	private boolean hasSpecialOffsets = false;
	private int nameOffset;
	private int nameLength;

	public FakeType(ModelElement sourceModule, String name) {
		super(sourceModule, name);
	}

	public FakeType(ModelElement sourceModule, String name, int flags) {
		super(sourceModule, name);
		this.flags = flags;
	}

	public FakeType(ModelElement parent, String name, int flags, int offset,
			int length, int nameOffset, int nameLength) {
		super(parent, name);
		this.flags = flags;
		this.offset = offset;
		this.length = length;
		this.nameOffset = nameOffset;
		this.nameLength = nameLength;
		hasSpecialOffsets = true;
	}

	@Override
	public ISourceRange getNameRange() throws ModelException {
		if (hasSpecialOffsets)
			return new SourceRange(nameOffset, nameLength);
		return super.getNameRange();
	}

	@Override
	public ISourceRange getSourceRange() throws ModelException {
		if (hasSpecialOffsets)
			return new SourceRange(offset, length);
		return super.getSourceRange();
	}

	@Override
	public IScriptProject getScriptProject() {
		return parent.getScriptProject();
	}

	@Override
	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}
}
