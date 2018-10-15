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
package org.eclipse.dltk.ui.formatter;

import org.eclipse.osgi.util.NLS;

public class FormatterMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.ui.formatter.FormatterMessages"; //$NON-NLS-1$
	public static String AbstractFormatterSelectionBlock_activeProfileName;
	public static String AbstractScriptFormatterFactory_defaultProfileName;
	public static String AbstractFormatterSelectionBlock_activeProfile;
	public static String AbstractFormatterSelectionBlock_confirmRemoveLabel;
	public static String AbstractFormatterSelectionBlock_confirmRemoveMessage;
	public static String AbstractFormatterSelectionBlock_editProfile;
	public static String AbstractFormatterSelectionBlock_importingProfile;
	public static String AbstractFormatterSelectionBlock_importProfile;
	public static String AbstractFormatterSelectionBlock_importProfileLabel;
	public static String AbstractFormatterSelectionBlock_moreRecentVersion;
	public static String AbstractFormatterSelectionBlock_newProfile;
	public static String AbstractFormatterSelectionBlock_noBuiltInProfiles;
	public static String AbstractFormatterSelectionBlock_notValidFormatter;
	public static String AbstractFormatterSelectionBlock_notValidProfile;
	public static String AbstractFormatterSelectionBlock_preview;
	public static String AbstractFormatterSelectionBlock_removeProfile;
	public static String AbstractFormatterSelectionBlock_formatterLabel;
	public static String AbstractFormatterSelectionBlock_profilesGroup;
	public static String AlreadyExistsDialog_loadProfile;
	public static String AlreadyExistsDialog_nameEmpty;
	public static String AlreadyExistsDialog_nameExists;
	public static String AlreadyExistsDialog_nameExistsQuestion;
	public static String AlreadyExistsDialog_overwriteProfile;
	public static String AlreadyExistsDialog_renameProfile;
	public static String CreateProfileDialog_initSettings;
	public static String CreateProfileDialog_nameEmpty;
	public static String CreateProfileDialog_nameExists;
	public static String CreateProfileDialog_newProfile;
	public static String CreateProfileDialog_openEditDialog;
	public static String CreateProfileDialog_profileName;
	public static String FormatterModifyDialog_changeBuiltInProfileName;
	public static String FormatterModifyDialog_createNewProfile;
	public static String FormatterModifyDialog_dialogTitle;
	public static String FormatterModifyDialog_export;
	public static String FormatterModifyDialog_exportProblem;
	public static String FormatterModifyDialog_exportProfile;
	public static String FormatterModifyDialog_nameEmpty;
	public static String FormatterModifyDialog_nameExists;
	public static String FormatterModifyDialog_profileName;
	public static String FormatterModifyDialog_replaceFileQuestion;
	public static String FormatterModifyTabPage_showInvisible;
	public static String FormatterModifyTabPage_preview_label_text;
	public static String FormatterPreferencePage_edit;
	public static String FormatterPreferencePage_preview;
	public static String FormatterPreferencePage_groupName;
	public static String FormatterPreferencePage_selectionLabel;
	public static String FormatterPreferencePage_settingsLink;
	public static String ScriptFormattingStrategy_formattingError;
	public static String ScriptFormattingStrategy_unableToFormatSourceContainingSyntaxError;
	/**
	 * @since 3.0
	 */
	public static String ScriptFormattingStrategy_unableToFormat;
	public static String ScriptFormattingStrategy_unexpectedFormatterError;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, FormatterMessages.class);
	}

	private FormatterMessages() {
	}
}
