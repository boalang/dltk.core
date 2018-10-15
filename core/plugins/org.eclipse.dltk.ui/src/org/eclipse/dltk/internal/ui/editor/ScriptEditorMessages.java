/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.editor;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Helper class to get NLSed messages.
 */
final class ScriptEditorMessages extends NLS {

	/**
	 * Returns the message bundle which contains constructed keys.
	 * 
	 * @return the message bundle
	 */
	public static ResourceBundle getBundleForConstructedKeys() {
		return DLTKEditorMessages.getBundleForConstructedKeys();
	}

	private static final String BUNDLE_NAME = ScriptEditorMessages.class
			.getName();

	private ScriptEditorMessages() {
		// Do not instantiate
	}

	public static String ContentAssistProposal_label;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ScriptEditorMessages.class);
	}

	public static String BasicEditorActionContributor_specific_content_assist_menu;
}
