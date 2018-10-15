/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
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
package org.eclipse.dltk.ui.wizards;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.builder.State;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.jface.wizard.IWizardPage;

public class ProjectWizardUtils {

	public static void reuseInterpreterLibraries(IProject fCurrProject,
			IInterpreterInstall projectInterpreter, IProgressMonitor monitor)
			throws CoreException {
		State lastState = (State) ModelManager.getModelManager()
				.getLastBuiltState(fCurrProject, monitor);
		if (lastState == null) {
			lastState = new State(fCurrProject);
		}
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		for (int i = 0; i < projects.length; i++) {
			if (projects[i].isAccessible()
					&& DLTKLanguageManager.hasScriptNature(projects[i])) {
				IScriptProject scriptProject = DLTKCore.create(projects[i]);

				IInterpreterInstall install = ScriptRuntime
						.getInterpreterInstall(scriptProject);
				if (projectInterpreter.equals(install)) {
					// We found project with same interpreter.
					State state = (State) ModelManager.getModelManager()
							.getLastBuiltState(projects[i], monitor);
					if (state != null) {
						lastState.getExternalFolders().addAll(
								state.getExternalFolders());
					}
				}
			}
		}
		lastState.setNoCleanExternalFolders();
		ModelManager.getModelManager().setLastBuiltState(fCurrProject,
				lastState);
	}

	/**
	 * Returns List of IBuildpathEntry or empty list.
	 * 
	 * @param firstPage
	 * @return
	 */
	static List<IBuildpathEntry> getDefaultBuildpathEntry(
			ILocationGroup firstPage) {

		IPath InterpreterEnvironmentContainerPath = new Path(
				ScriptRuntime.INTERPRETER_CONTAINER);

		IInterpreterInstall inst = firstPage.getInterpreter();
		if (inst != null) {
			IPath newPath = InterpreterEnvironmentContainerPath.append(
					inst.getInterpreterInstallType().getId()).append(
					inst.getName());
			return Collections.singletonList(DLTKCore
					.newContainerEntry(newPath));
		}
		final IBuildpathEntry defaultPath = ScriptRuntime
				.getDefaultInterpreterContainerEntry();
		if (defaultPath != null)
			return Collections.singletonList(defaultPath);
		return Collections.emptyList();
	}

	/**
	 * @param page
	 * @return
	 * @since 2.0
	 */
	public static boolean isProjectRequredFor(IWizardPage page) {
		return !(page instanceof ProjectWizardFirstPage);
	}

}
