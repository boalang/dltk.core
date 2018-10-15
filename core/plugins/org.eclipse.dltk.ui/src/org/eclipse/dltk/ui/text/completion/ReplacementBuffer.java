/*******************************************************************************
 * Copyright (c) 2011, 2017 NumberFour AG and others.
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
package org.eclipse.dltk.ui.text.completion;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 4.0
 */
public class ReplacementBuffer {
	static class Argument {
		final int offset;
		final int length;
		final boolean relativeToCursor;

		public Argument(int offset, int length, boolean relativeToCursor) {
			this.offset = offset;
			this.length = length;
			this.relativeToCursor = relativeToCursor;
		}
	}

	final List<Argument> arguments = new ArrayList<>();

	@Deprecated
	public void addArgument(int offset, int length) {
		arguments.add(new Argument(offset, length, true));
	}

	public void addArgument(String value) {
		final int offset = length();
		append(value);
		arguments.add(new Argument(offset, value.length(), false));
	}

	private final StringBuilder buffer = new StringBuilder();

	public void append(String text) {
		buffer.append(text);
	}

	public int length() {
		return buffer.length();
	}

	@Override
	public String toString() {
		return buffer.toString();
	}

	public boolean hasArguments() {
		return !arguments.isEmpty();
	}

}
