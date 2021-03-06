/*******************************************************************************
 * Copyright (c) 2008, 2016 xored software, Inc.
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
package org.eclipse.dltk.utils;

/**
 * {@link CharSequence} implementation backing by the char[]
 */
public class CharArraySequence implements CharSequence {

	private final char[] buff;
	private final int offset;
	private final int count;

	/**
	 * @param buff
	 */
	public CharArraySequence(char[] buff) {
		this(buff, 0, buff.length);
	}

	/**
	 * @param buff
	 * @param count
	 */
	public CharArraySequence(char[] buff, int count) {
		this(buff, 0, count);
	}

	/**
	 * @param buff
	 * @param offset
	 * @param count
	 */
	public CharArraySequence(char[] buff, int offset, int count) {
		this.buff = buff;
		this.offset = offset;
		this.count = count;
	}

	@Override
	public char charAt(int index) {
		if (index < 0 || index >= count) {
			throw new StringIndexOutOfBoundsException(index);
		}
		return buff[offset + index];
	}

	@Override
	public int length() {
		return count;
	}

	@Override
	public CharSequence subSequence(int beginIndex, int endIndex) {
		if (beginIndex < 0) {
			throw new StringIndexOutOfBoundsException(beginIndex);
		}
		if (endIndex > count) {
			throw new StringIndexOutOfBoundsException(endIndex);
		}
		if (beginIndex > endIndex) {
			throw new StringIndexOutOfBoundsException(endIndex - beginIndex);
		}
		return ((beginIndex == 0) && (endIndex == count)) ? this
				: new CharArraySequence(buff, offset + beginIndex, endIndex
						- beginIndex);
	}

	@Override
	public String toString() {
		return new String(this.buff, this.offset, this.count);
	}
}
