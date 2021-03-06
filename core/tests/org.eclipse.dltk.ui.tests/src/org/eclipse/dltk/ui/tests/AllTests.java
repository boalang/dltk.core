/*******************************************************************************
 * Copyright (c) 2017 Red Hat Inc. and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.ui.tests;

import org.eclipse.dltk.ui.tests.core.DLTKUILanguageManagerTests;
import org.eclipse.dltk.ui.tests.core.ScriptElementLabelsTest;
import org.eclipse.dltk.ui.tests.navigator.scriptexplorer.PackageExplorerTests;
import org.eclipse.dltk.ui.tests.refactoring.ModelElementUtilTests;
import org.eclipse.dltk.ui.tests.templates.ScriptTemplateContextTest;
import org.eclipse.dltk.ui.tests.text.FloatNumberRuleTest;
import org.eclipse.dltk.ui.tests.text.TodoHighlightingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({ ScriptElementLabelsTest.class,
		DLTKUILanguageManagerTests.class, ModelElementUtilTests.class,
		PackageExplorerTests.class, ScriptTemplateContextTest.class,
		TodoHighlightingTest.class, FloatNumberRuleTest.class })
public class AllTests {
}
