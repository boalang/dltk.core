/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.refactoring.reorg;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IModelElement;


public interface IReorgDestinationValidator {
	//TODO: Do we need this interface? Or should IReorgPolicy be public and available from ScriptMoveProcessor and CopyRefactoring?
	public boolean canChildrenBeDestinations(IResource resource);
	public boolean canChildrenBeDestinations(IModelElement modelElement);
	public boolean canElementBeDestination(IResource resource);
	public boolean canElementBeDestination(IModelElement modelElement);
}
