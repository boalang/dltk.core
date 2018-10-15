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
package org.eclipse.dltk.testing;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceRange;

public class TestElementResolution {

	private final IModelElement element;

	private final ISourceRange range;

	/**
	 * @param element
	 * @param range
	 */
	public TestElementResolution(IModelElement element, ISourceRange range) {
		Assert.isNotNull(element);
		this.element = element;
		this.range = range;
	}

	/**
	 * Returns the model element. Should not be <code>null</code>.
	 * 
	 * @return the element
	 */
	public IModelElement getElement() {
		return element;
	}

	/**
	 * Returns the source range. Could be <code>null</code>.
	 * 
	 * @return the range
	 */
	public ISourceRange getRange() {
		return range;
	}

}
