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
package org.eclipse.dltk.validators.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface IConfigurableValidator {

	/**
	 * Store configuration information to the specified XML element.
	 * 
	 * @param doc
	 * @param element
	 */
	void storeTo(Document doc, Element element);

	/**
	 * Loads configuration information from the specified XML element.
	 * 
	 * @param element
	 */
	void loadFrom(Element element);

}
