/*******************************************************************************
 * Copyright (c) 2011 xored software, Inc.
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
package org.eclipse.dltk.core.keyword;

import org.eclipse.dltk.core.ISourceModule;

/**
 * Keyword provider.
 * 
 * <p>
 * Implementations of this interface should be contributed via
 * <code>org.eclipse.dltk.core.keywords</code> extension point.
 * </p>
 * 
 * @since 3.0
 */
public interface IKeywordProvider {

	/**
	 * Returns the keywords for the specified <code>category</code>. The meaning
	 * of the category is completely language specific.
	 * 
	 * @param category
	 * @param module
	 * @return
	 */
	String[] getKeywords(IKeywordCategory category, ISourceModule module);

}
