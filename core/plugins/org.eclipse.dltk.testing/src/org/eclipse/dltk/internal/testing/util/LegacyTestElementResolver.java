/*******************************************************************************
 * Copyright (c) 2008, 2016 xored software, Inc. and others.
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
package org.eclipse.dltk.internal.testing.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.internal.testing.MemberResolverManager;
import org.eclipse.dltk.internal.testing.model.TestElement;
import org.eclipse.dltk.internal.testing.model.TestRoot;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.testing.DLTKTestingConstants;
import org.eclipse.dltk.testing.ITestElementResolver;
import org.eclipse.dltk.testing.ITestingElementResolver;
import org.eclipse.dltk.testing.TestElementResolution;
import org.eclipse.dltk.testing.model.ITestCaseElement;
import org.eclipse.dltk.testing.model.ITestElement;

public class LegacyTestElementResolver implements ITestElementResolver {

	private final IScriptProject project;
	private final ILaunchConfiguration launchConfiguration;

	/**
	 * @param launchedProject
	 * @param launchConfiguration
	 */
	public LegacyTestElementResolver(IScriptProject project,
			ILaunchConfiguration configuration) {
		this.project = project;
		this.launchConfiguration = configuration;
	}

	@Override
	public TestElementResolution resolveElement(ITestElement testElement) {
		final String engineId;
		try {
			engineId = launchConfiguration.getAttribute(
					DLTKTestingConstants.ATTR_ENGINE_ID, (String) null);
		} catch (CoreException e) {
			return null;
		}
		if (engineId == null) {
			return null;
		}
		final ITestingElementResolver resolver = MemberResolverManager
				.getResolver(engineId);
		if (resolver == null) {
			return null;
		}
		final ISourceModule module = resolveSourceModule();
		if (module == null) {
			return null;
		}
		final String relativeName = getRootRelativeName((TestElement) testElement);
		final IModelElement element = resolver.resolveElement(project,
				launchConfiguration, module, relativeName);
		if (element == null) {
			return null;
		}
		final String method;
		if (testElement instanceof ITestCaseElement) {
			final String testName = ((ITestCaseElement) testElement)
					.getTestName();
			int index = testName.indexOf('(');
			method = index > 0 ? testName.substring(0, index) : testName;
		} else {
			method = null;
		}
		ISourceRange range = resolver.resolveRange(project,
				launchConfiguration, relativeName, module, element, method);
		return new TestElementResolution(element, range);
	}

	protected ISourceModule resolveSourceModule() {
		String scriptName;
		try {
			scriptName = launchConfiguration.getAttribute(
					ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME,
					(String) null);
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			return null;
		}
		IProject prj = project.getProject();
		IResource file = prj.findMember(new Path(scriptName));
		if (file instanceof IFile) {
			return (ISourceModule) DLTKCore.create(file);
		}
		return null;
	}

	private String getRootRelativeName(TestElement testCase) {
		String name = Util.EMPTY_STRING;
		TestElement el = testCase;
		while (el != null) {
			if (name.length() != 0) {
				name = el.getTestName() + "." + name; //$NON-NLS-1$
			} else {
				name = el.getTestName();
			}
			el = el.getParent();
			if (el instanceof TestRoot) {
				break;
			}
		}
		if (name.startsWith(".")) { //$NON-NLS-1$
			return name.substring(1);
		}
		return name;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

}
