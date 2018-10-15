/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 
 *******************************************************************************/
package org.eclipse.dltk.codeassist;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;

public interface IAssistParser {
	/**
	 * Possible this method may be not needed in future.
	 */
	void setSource(ModuleDeclaration unit);

	/**
	 * Used to parse inner content of methods, and other non type or module statements.
	 */
	void parseBlockStatements(ASTNode node, ASTNode unit, int position);
	
	public ModuleDeclaration parse(IModuleSource sourceModule);
	
	// XXX wtf? what should this method do? 
	public ASTNode getAssistNodeParent();

	/*  
	 * Called when element couldn't be found.
	 */
	void handleNotInElement(ASTNode unit, int position);

	ModuleDeclaration getModule();
}
