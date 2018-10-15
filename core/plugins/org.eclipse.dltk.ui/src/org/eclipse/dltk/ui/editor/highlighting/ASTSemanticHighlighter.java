/*******************************************************************************
 * Copyright (c) 2008, 2018 xored software, Inc. and others.
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
package org.eclipse.dltk.ui.editor.highlighting;

import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceParserUtil;

/**
 * Abstract base class for the semantic highlighters operating on the AST tree.
 */
public abstract class ASTSemanticHighlighter
		extends AbstractSemanticHighlighter {

	/**
	 * @param code
	 * @return
	 * @throws ModelException
	 */
	protected IModuleDeclaration parseCode(IModuleSource code)
			throws ModelException {
		if (code instanceof ISourceModule) {
			return parseSourceModule((ISourceModule) code);
		}
		return parseSourceCode(code);
	}

	private IModuleDeclaration parseSourceCode(IModuleSource code) {
		return SourceParserUtil.parse(code, getNature(), null);
	}

	private IModuleDeclaration parseSourceModule(
			final ISourceModule sourceModule) {
		return SourceParserUtil.parse(sourceModule, null);
	}

	protected abstract String getNature();

	@Deprecated
	protected final void createVisitor(
			org.eclipse.dltk.compiler.env.ISourceModule sourceCode) {
	}

}
