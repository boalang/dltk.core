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
package org.eclipse.dltk.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.task.ITaskReporter;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.IFileHandle;

/**
 * The context of the building a module.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IBuildContext extends IModuleSource {

	/**
	 * The name of the attribute to store the {@link ModuleDeclaration}
	 * instance.
	 */
	public static final String ATTR_MODULE_DECLARATION = ModuleDeclaration.class
			.getName();

	int FULL_BUILD = IScriptBuilder.FULL_BUILD;
	int INCREMENTAL_BUILD = IScriptBuilder.INCREMENTAL_BUILD;
	int RECONCILE_BUILD = 10;

	int getBuildType();

	/**
	 * Returns the contents of the source module
	 * 
	 * @return
	 */
	char[] getContents();

	/**
	 * Returns the source module being compiled
	 * 
	 * @return
	 */
	ISourceModule getSourceModule();

	ISourceLineTracker getLineTracker();

	IProblemReporter getProblemReporter();

	ITaskReporter getTaskReporter();

	/**
	 * Returns the workspace {@link IFile} being compiled or <code>null</code>
	 * if building external module.
	 * 
	 * @return
	 */
	IFile getFile();

	/**
	 * Returns the external {@link IFileHandle} being compiled
	 * 
	 * @return
	 */
	IFileHandle getFileHandle();

	/**
	 * Returns the value of the specified attribute.
	 * 
	 * {@link org.eclipse.dltk.ast.declarations.ModuleDeclaration} have the key
	 * org.eclipse.dltk.ast.declarations.ModuleDeclaration.class.getName(). If
	 * there are unrecoverable compilation errors then it would be
	 * <code>null</code>
	 * 
	 * @param attribute
	 * @return
	 */
	Object get(String attribute);

	/**
	 * Sets the value of the specified attribute
	 * 
	 * @param attribute
	 * @param value
	 */
	void set(String attribute, Object value);

	/**
	 * Records the dependency on the current module on the specified file.
	 * 
	 * @param dependency
	 * @param flags
	 * @since 5.0
	 * @see IBuildState#recordDependency(IPath, IPath, int)
	 */
	void recordDependency(IPath dependency, int flags);

}
