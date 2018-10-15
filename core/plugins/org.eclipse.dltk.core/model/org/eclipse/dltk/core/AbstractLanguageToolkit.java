/*******************************************************************************
 * Copyright (c) 2016 xored software, Inc. and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     xored software, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.dltk.core.DLTKFeatures.BooleanFeature;
import org.eclipse.dltk.core.DLTKFeatures.IntegerFeature;
import org.eclipse.dltk.core.DLTKFeatures.StringFeature;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.core.ZipArchiveFile;

public abstract class AbstractLanguageToolkit implements IDLTKLanguageToolkit {

	public AbstractLanguageToolkit() {
	}

	@Override
	public boolean languageSupportZIPBuildpath() {
		return false;
	}

	@Override
	public boolean validateSourcePackage(IPath path, IEnvironment environment) {
		return true;
	}

	@Override
	public IStatus validateSourceModule(IResource resource) {
		return Status.OK_STATUS;
	}

	protected static boolean isEmptyExtension(String name) {
		return name.indexOf('.') == -1;
	}

	@Override
	public boolean canValidateContent(IResource resource) {
		final IProject project = resource.getProject();
		if (project == null) { // This is workspace root.
			return false;
		}
		final IEnvironment environment = EnvironmentManager
				.getEnvironment(project);
		if (environment == null || !environment.isLocal()) {
			return false;
		}
		return isEmptyExtension(resource.getName());
	}

	@Override
	public boolean canValidateContent(File file) {
		return isEmptyExtension(file.getName());
	}

	@Override
	public boolean canValidateContent(IFileHandle file) {
		return false;
	}

	@Override
	public String getPreferenceQualifier() {
		return null;
	}

	@Override
	public boolean get(BooleanFeature feature) {
		return feature.getDefaultValue();
	}

	@Override
	public int get(IntegerFeature feature) {
		return feature.getDefaultValue();
	}

	@Override
	public String get(StringFeature feature) {
		return feature.getDefaultValue();
	}

	/**
	 * @throws IOException
	 * @since 2.0
	 */
	@Override
	public IArchive openArchive(File localFile) throws IOException {
		return new ZipArchiveFile(localFile);
	}

	@Override
	public String getFileType() {
		final IContentType contentType = Platform.getContentTypeManager()
				.getContentType(getLanguageContentType());
		if (contentType != null) {
			final String[] specs = contentType
					.getFileSpecs(IContentType.FILE_EXTENSION_SPEC
							| IContentType.IGNORE_USER_DEFINED);
			if (specs.length != 0) {
				return specs[0];
			}
		}
		return "txt";
	}
}
