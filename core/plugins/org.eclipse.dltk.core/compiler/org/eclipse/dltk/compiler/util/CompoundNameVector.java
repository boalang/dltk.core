/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.compiler.util;

import org.eclipse.dltk.compiler.CharOperation;

public final class CompoundNameVector {
	static int INITIAL_SIZE = 10;

	public int size;
	int maxSize;
	char[][][] elements;

	public CompoundNameVector() {
		maxSize = INITIAL_SIZE;
		size = 0;
		elements = new char[maxSize][][];
	}

	public void add(char[][] newElement) {
		if (size == maxSize) // knows that size starts <= maxSize
			System.arraycopy(elements, 0, (elements = new char[maxSize *= 2][][]), 0, size);
		elements[size++] = newElement;
	}

	public void addAll(char[][][] newElements) {
		if (size + newElements.length >= maxSize) {
			maxSize = size + newElements.length; // assume no more elements
													// will be added
			System.arraycopy(elements, 0, (elements = new char[maxSize][][]), 0, size);
		}
		System.arraycopy(newElements, 0, elements, size, newElements.length);
		size += newElements.length;
	}

	public boolean contains(char[][] element) {
		for (int i = size; --i >= 0;)
			if (CharOperation.equals(element, elements[i]))
				return true;
		return false;
	}

	public char[][] elementAt(int index) {
		return elements[index];
	}

	public char[][] remove(char[][] element) {
		// assumes only one occurrence of the element exists
		for (int i = size; --i >= 0;)
			if (element == elements[i]) {
				// shift the remaining elements down one spot
				System.arraycopy(elements, i + 1, elements, i, --size - i);
				elements[size] = null;
				return element;
			}
		return null;
	}

	public void removeAll() {
		for (int i = size; --i >= 0;)
			elements[i] = null;
		size = 0;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < size; i++) {
			buffer.append(CharOperation.toString(elements[i])).append("\n"); //$NON-NLS-1$
		}
		return buffer.toString();
	}
}
