/*******************************************************************************
 * Copyright (c) 2012, 2017 NumberFour AG and others.
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
package org.eclipse.dltk.internal.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.annotations.Nullable;
import org.eclipse.dltk.core.ICodeSelection;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.utils.ArrayIterator;
import org.eclipse.dltk.utils.MultiArrayIterator;

/**
 * Default implementation of {@link ICodeSelection}.
 */
class CodeSelection implements ICodeSelection {

	@Nullable
	private final IModelElement[] modelElements;

	@Nullable
	private final Object[] foreignElements;

	@Nullable
	private final Map<Object, ISourceRange> ranges;

	public CodeSelection(IModelElement[] modelElements,
			Object[] foreignElements, Map<Object, ISourceRange> ranges) {
		Assert.isLegal(modelElements != null || foreignElements != null);
		if (modelElements != null) {
			Assert.isLegal(modelElements.length != 0);
		}
		if (foreignElements != null) {
			Assert.isLegal(foreignElements.length != 0);
		}
		this.modelElements = modelElements;
		this.foreignElements = foreignElements;
		this.ranges = ranges;
	}

	@Override
	public int size() {
		return (modelElements != null ? modelElements.length : 0)
				+ (foreignElements != null ? foreignElements.length : 0);
	}

	@Override
	public Object[] toArray() {
		if (modelElements != null && foreignElements == null) {
			return modelElements;
		} else if (modelElements == null && foreignElements != null) {
			return foreignElements;
		} else {
			final Object[] result = new Object[modelElements.length
					+ foreignElements.length];
			System.arraycopy(modelElements, 0, result, 0, modelElements.length);
			System.arraycopy(foreignElements, 0, result, modelElements.length,
					foreignElements.length);
			return result;
		}
	}

	@Override
	public List<Object> toList() {
		if (modelElements != null && foreignElements == null) {
			return Arrays.asList((Object[]) modelElements);
		} else if (modelElements == null && foreignElements != null) {
			return Arrays.asList(foreignElements);
		} else {
			final List<Object> result = new ArrayList<>(
					modelElements.length + foreignElements.length);
			Collections.addAll(result, modelElements);
			Collections.addAll(result, foreignElements);
			return result;
		}
	}

	@Override
	public Iterator<Object> iterator() {
		if (modelElements != null && foreignElements == null) {
			return new ArrayIterator<>(modelElements);
		} else if (modelElements == null && foreignElements != null) {
			return new ArrayIterator<>(foreignElements);
		} else {
			return new MultiArrayIterator<>(modelElements,
					foreignElements);
		}
	}

	@Override
	public List<IModelElement> getModelElements() {
		return modelElements != null ? Arrays.asList(modelElements)
				: Collections.<IModelElement> emptyList();
	}

	@Override
	public ISourceRange rangeOf(Object element) {
		return ranges != null ? ranges.get(element) : null;
	}

}
