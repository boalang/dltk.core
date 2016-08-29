/*******************************************************************************
 * Copyright (c) 2005, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.compiler.problem;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;

public class DefaultProblemFactory implements IProblemFactory {

	@Override
	public String getMarkerType(IProblem problem) {
		if (problem.getID() instanceof IProblemIdentifierExtension) {
			final String markerType = ((IProblemIdentifierExtension) problem
					.getID()).getMarkerType();
			if (markerType != null) {
				return markerType;
			}
		}
		return problem.isTask() ? getTaskMarkerType() : getProblemMarkerType();
	}

	protected String getProblemMarkerType() {
		return DefaultProblem.MARKER_TYPE_PROBLEM;
	}

	protected String getTaskMarkerType() {
		return DefaultProblem.MARKER_TYPE_TASK;
	}

	@Override
	public IMarker createMarker(IResource resource, IProblem problem)
			throws CoreException {
		final String markerType = getMarkerType(problem);
		return resource.createMarker(markerType);
	}

	@Override
	public void deleteMarkers(IResource resource) throws CoreException {
		resource.deleteMarkers(DefaultProblem.MARKER_TYPE_PROBLEM, true,
				IResource.DEPTH_INFINITE);
		resource.deleteMarkers(DefaultProblem.MARKER_TYPE_TASK, true,
				IResource.DEPTH_INFINITE);
	}

	@Override
	public boolean isValidMarker(IMarker marker) {
		try {
			return marker.exists() && (marker
					.isSubtypeOf(DefaultProblem.MARKER_TYPE_PROBLEM)
					|| marker.isSubtypeOf(DefaultProblem.MARKER_TYPE_TASK));
		} catch (CoreException e) {
			DLTKCore.error(e);
			return false;
		}
	}

	/**
	 * Validates that the specified marker type is correct for this problem
	 * factory
	 * 
	 * @param markerType
	 *            not null
	 * @return
	 */
	protected boolean isValidMarkerType(String markerType) {
		return markerType.startsWith(DefaultProblem.MARKER_TYPE_PREFIX);
	}

	@Override
	public IProblemSeverityTranslator createSeverityTranslator(
			IScriptProject project) {
		return IProblemSeverityTranslator.IDENTITY;
	}
}
