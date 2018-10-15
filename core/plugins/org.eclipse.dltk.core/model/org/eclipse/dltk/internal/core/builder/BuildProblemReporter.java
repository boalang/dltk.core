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
package org.eclipse.dltk.internal.core.builder;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemCategory;
import org.eclipse.dltk.compiler.problem.IProblemFactory;
import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.IProblemIdentifierExtension3;
import org.eclipse.dltk.compiler.problem.IProblemSeverityTranslator;
import org.eclipse.dltk.compiler.problem.ProblemCollector;
import org.eclipse.dltk.core.DLTKCore;

public class BuildProblemReporter extends ProblemCollector {

	private final IProblemFactory problemFactory;
	final IResource resource;
	private boolean oldMarkersDeleted = false;

	/**
	 * @param resource
	 */
	public BuildProblemReporter(IProblemFactory problemFactory,
			IResource resource) {
		this.problemFactory = problemFactory;
		this.resource = resource;
	}

	public void flush(IProblemSeverityTranslator severityProvider) {
		try {
			if (!oldMarkersDeleted) {
				oldMarkersDeleted = true;
				problemFactory.deleteMarkers(resource);
			}
			createMarkers(resource, problemFactory, severityProvider);
			problems.clear();
		} catch (CoreException e) {
			DLTKCore.error(Messages.BuildProblemReporter_errorUpdatingMarkers,
					e);
		}
	}

	public boolean hasCategory(IProblemCategory category) {
		for (IProblem problem : getProblems()) {
			final IProblemIdentifier id = problem.getID();
			if (id != null && id instanceof IProblemIdentifierExtension3
					&& ((IProblemIdentifierExtension3) id).belongsTo(category)) {
				return true;
			}
		}
		return false;
	}

}
