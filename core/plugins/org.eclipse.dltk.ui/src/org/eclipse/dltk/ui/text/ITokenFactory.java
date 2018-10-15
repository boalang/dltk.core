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
package org.eclipse.dltk.ui.text;

import org.eclipse.jface.text.rules.IToken;

/**
 * The interface to provide color {@link IToken}s by the internal key.
 * Implementation is provided by {@link AbstractScriptScanner}.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITokenFactory {

	IToken getToken(String key);

}
