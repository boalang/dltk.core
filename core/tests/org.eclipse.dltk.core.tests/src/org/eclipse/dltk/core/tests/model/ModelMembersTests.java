/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *

 *******************************************************************************/
package org.eclipse.dltk.core.tests.model;

import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;

import junit.framework.TestCase;

public class ModelMembersTests extends AbstractModelTests {
	private static final String PRJ_NAME = "ModelMembersq";

	public ModelMembersTests(String name) {
		super(ModelTestsPlugin.PLUGIN_NAME, name);
	}

	@Override
	public void setUpSuite() throws Exception {
		super.setUpSuite();
		setUpScriptProjectTo(PRJ_NAME, "ModelMembers");
	}

	@Override
	public void tearDownSuite() throws Exception {
		deleteProject(PRJ_NAME);
		super.tearDownSuite();
	}

	public void test001() throws ModelException {
		ISourceModule module = getSourceModule(PRJ_NAME, "src1",
				new Path("X.txt"));
		assertNotNull("No source module", module);
		IModelElement[] children = module.getChildren();
		assertNotNull("No children", children);
		assertEquals("Wrong size", 2, children.length);
		IType type = (IType) children[0];
		assertEquals("Class1", type.getElementName());
		assertEquals("Wrong size", 1, type.getChildren().length);
		IMember proc = (IMember) children[1];
		assertEquals("Procedure1", proc.getElementName());
	}

	public void test002() throws ModelException {
		IProjectFragment fragment = getProjectFragment(PRJ_NAME, "src1");
		IModelElement[] fragmentChildren = fragment.getChildren();
		TestCase.assertEquals(2, fragmentChildren.length);
		ISourceModule module = fragment.getScriptFolder("Goo")
				.getSourceModule("X.txt");
		assertNotNull("No source module", module);
		IModelElement[] children = module.getChildren();
		assertNotNull("No children", children);
		assertEquals("Wrong size", 2, children.length);
		IType type = (IType) children[0];
		assertEquals("Class1", type.getElementName());
		assertEquals("Wrong size", 1, type.getChildren().length);
		IMember proc = (IMember) children[1];
		assertEquals("Procedure1", proc.getElementName());

		String identifier = module.getHandleIdentifier();
		IModelElement element = DLTKCore.create(identifier);
		TestCase.assertEquals(module, element);
	}
}
