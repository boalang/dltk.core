/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     		IBM Corporation - initial API and implementation
 * 			Alex Panchenko <alex@xored.com>
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Font;

/**
 * Handles dltk editor font changes for script source preview viewers.
 */
public class ScriptSourcePreviewerUpdater {

	/**
	 * Creates a script source preview updater for the given viewer,
	 * configuration and preference store.
	 *
	 * @param viewer
	 *            the viewer
	 * @param configuration
	 *            the configuration
	 * @param preferenceStore
	 *            the preference store
	 */
	public ScriptSourcePreviewerUpdater(final SourceViewer viewer,
			final ScriptSourceViewerConfiguration configuration,
			final IPreferenceStore preferenceStore) {
		Assert.isNotNull(viewer);
		Assert.isNotNull(configuration);
		Assert.isNotNull(preferenceStore);
		final IPropertyChangeListener fontChangeListener = event -> {
			final String fontKey = configuration.getFontPropertyPreferenceKey();
			if (fontKey.equals(event.getProperty())) {
				final Font font = JFaceResources.getFont(fontKey);
				viewer.getTextWidget().setFont(font);
			}
		};
		final IPropertyChangeListener propertyChangeListener = event -> {
			if (configuration.affectsTextPresentation(event)) {
				configuration.handlePropertyChangeEvent(event);
				viewer.invalidateTextPresentation();
			}
		};
		viewer.getTextWidget().addDisposeListener(e -> {
			preferenceStore
					.removePropertyChangeListener(propertyChangeListener);
			JFaceResources.getFontRegistry().removeListener(
					fontChangeListener);
		});

		JFaceResources.getFontRegistry().addListener(fontChangeListener);
		preferenceStore.addPropertyChangeListener(propertyChangeListener);
	}
}
