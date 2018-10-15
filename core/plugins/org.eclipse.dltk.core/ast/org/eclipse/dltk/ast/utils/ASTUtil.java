/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.ast.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;

public class ASTUtil {
	public static List getStatements(ASTNode node) {
		if (node instanceof ModuleDeclaration) {
			return ((ModuleDeclaration) node).getStatements();
		} else if (node instanceof TypeDeclaration) {
			return ((TypeDeclaration) node).getStatements();
		} else if (node instanceof MethodDeclaration) {
			return ((MethodDeclaration) node).getStatements();
		}
		return null;
	}

	public static TypeDeclaration[] getTypes(List statements, List types) {
		List finalTypes = new ArrayList();
		finalTypes.addAll(types);

		if (statements != null) {
			Iterator it = statements.iterator();
			while (it.hasNext()) {
				ASTNode node = (ASTNode) it.next();
				if (node instanceof TypeDeclaration
						&& !finalTypes.contains(node)) {
					finalTypes.add(node);
				}
			}
		}
		return (TypeDeclaration[]) finalTypes
				.toArray(new TypeDeclaration[finalTypes.size()]);
	}

	public static MethodDeclaration[] getMethods(List statements,
			List functions) {
		List finalMethods = new ArrayList();
		finalMethods.addAll(functions);

		if (statements != null) {
			Iterator it = statements.iterator();
			while (it.hasNext()) {
				ASTNode node = (ASTNode) it.next();
				if (node instanceof MethodDeclaration
						&& !finalMethods.contains(node)) {
					finalMethods.add(node);
				}
			}
		}
		return (MethodDeclaration[]) finalMethods
				.toArray(new MethodDeclaration[finalMethods.size()]);
	}

	public static FieldDeclaration[] getVariables(List statements,
			List variables) {
		List finalVariables = new ArrayList();
		finalVariables.addAll(variables);

		if (statements != null) {
			Iterator it = statements.iterator();
			while (it.hasNext()) {
				ASTNode node = (ASTNode) it.next();
				if (node instanceof FieldDeclaration
						&& !finalVariables.contains(node)) {
					finalVariables.add(node);
				}
			}
		}
		return (FieldDeclaration[]) finalVariables
				.toArray(new FieldDeclaration[finalVariables.size()]);
	}

	public static <E> List<E> select(ASTNode root, final Class<E> type) {
		return select(root, type, false);
	}

	public static <E> List<E> select(ASTNode root, final Class<E> type,
			final boolean visitNested) {
		final List<E> result = new ArrayList<>();
		try {
			root.traverse(new ASTVisitor() {
				@SuppressWarnings("unchecked")
				@Override
				public boolean visitGeneral(ASTNode s) throws Exception {
					if (type.isInstance(s)) {
						result.add((E) s);
						return visitNested;
					} else {
						return true;
					}
				}
			});
		} catch (Exception e) {
			//
		}
		return result;
	}

}
