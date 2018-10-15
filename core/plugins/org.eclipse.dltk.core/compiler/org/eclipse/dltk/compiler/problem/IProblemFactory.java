/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.compiler.problem;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;

/*
 * Factory used from inside the compiler to build the actual problems
 * which are handed back in the compilation result.
 *
 * This allows sharing the internal problem representation with the environment.
 *
 * Note: The factory is responsible for computing and storing a localized error message.
 */
public interface IProblemFactory {

	/**
	 * @since 3.0
	 */
	String getMarkerType(IProblem problem);

	/**
	 * @param resource
	 * @param problem
	 * @return
	 * @throws CoreException
	 * @since 3.0
	 */
	IMarker createMarker(IResource resource, IProblem problem)
			throws CoreException;

	/**
	 * @param resource
	 * @throws CoreException
	 * @since 3.0
	 */
	void deleteMarkers(IResource resource) throws CoreException;

	/**
	 * Validates that the specified marker has correct type for this problem
	 * factory
	 * 
	 * @param marker
	 * @return
	 */
	boolean isValidMarker(IMarker marker);

	/**
	 * Returns new instance of the {@link IProblemSeverityTranslator} to be used
	 * for the specified project.
	 * 
	 * @param project
	 * @return
	 * @since 4.0
	 */
	IProblemSeverityTranslator createSeverityTranslator(IScriptProject project);

}
