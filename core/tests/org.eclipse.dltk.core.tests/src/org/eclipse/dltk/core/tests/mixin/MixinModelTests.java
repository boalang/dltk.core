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
package org.eclipse.dltk.core.tests.mixin;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.mixin.IMixinElement;
import org.eclipse.dltk.core.mixin.MixinModel;
import org.eclipse.dltk.core.search.index.MixinIndex;
import org.eclipse.dltk.core.tests.model.AbstractModelTests;
import org.eclipse.dltk.core.tests.model.TestLanguageToolkit;

/**
 * Tests for the {@link MixinIndex} class.
 */
@SuppressWarnings("deprecation")
public class MixinModelTests extends AbstractModelTests {

	private IProject project;

	public MixinModelTests(String name) {
		super("org.eclipse.dltk.core.tests", name);
	}

	@Override
	public void setUpSuite() throws Exception {
		super.setUpSuite();
		project = setUpProject("Mixin0");
		waitUntilIndexesReady();
	}

	@Override
	public void tearDownSuite() throws Exception {
		deleteProject(project.getName());
		super.tearDownSuite();
	}

	public void testMixin001() throws Throwable {
		MixinModel model = new MixinModel(TestLanguageToolkit.getDefault());
		try {
			IMixinElement[] results = model.find("{foo");
			TestCase.assertEquals(1, results.length);
		} finally {
			model.stop();
		}
	}

	public void testMixin002() throws Throwable {
		MixinModel model = new MixinModel(TestLanguageToolkit.getDefault());
		try {
			IMixinElement[] results = model.find("{foo*");
			TestCase.assertEquals(3, results.length);
		} finally {
			model.stop();
		}
	}

	public void testMixin003() throws Throwable {
		MixinModel model = new MixinModel(TestLanguageToolkit.getDefault());
		try {
			IMixinElement[] results = model.find("{foo*a");
			TestCase.assertEquals(1, results.length);
		} finally {
			model.stop();
		}
	}
}
