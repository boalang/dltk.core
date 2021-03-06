/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.resources.mapping.ResourceMappingContext;
import org.eclipse.core.resources.mapping.ResourceTraversal;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Resource mapping for the script model provider.
 *
 */
public final class DLTKResourceMapping extends ResourceMapping {

	/** The resource to map */
	private final IResource fResource;

	/**
	 * Creates a new script resource mapping.
	 *
	 * @param resource
	 *            the resource to map
	 */
	public DLTKResourceMapping(final IResource resource) {
		Assert.isNotNull(resource);
		fResource= resource;
	}

	@Override
	public Object getModelObject() {
		return fResource;
	}

	@Override
	public String getModelProviderId() {
		return ScriptModelProvider.DLTK_MODEL_PROVIDER_ID;
	}

	@Override
	public IProject[] getProjects() {
		return new IProject[] { fResource.getProject() };
	}

	@Override
	public ResourceTraversal[] getTraversals(final ResourceMappingContext context, final IProgressMonitor monitor) {
		return new ResourceTraversal[] { new ResourceTraversal(new IResource[] { fResource }, IResource.DEPTH_INFINITE, IResource.NONE) };
	}
}
