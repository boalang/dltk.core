/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
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
package org.eclipse.dltk.formatter;

public interface IFormatterContext {

	int getIndent();

	void incIndent();

	void decIndent();

	void resetIndent();

	IFormatterContext copy();

	boolean isIndenting();

	void setIndenting(boolean value);

	boolean isComment();

	void setComment(boolean value);

	int getBlankLines();

	void setBlankLines(int value);

	void resetBlankLines();

	/**
	 * @param node
	 */
	void enter(IFormatterNode node);

	/**
	 * @param node
	 */
	void leave(IFormatterNode node);

	IFormatterNode getParent();

	int getChildIndex();

	boolean isWrapping();

	void setWrapping(boolean value);

}
