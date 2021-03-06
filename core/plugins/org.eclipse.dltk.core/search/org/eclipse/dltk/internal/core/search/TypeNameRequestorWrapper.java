/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search;

import org.eclipse.dltk.core.search.BasicSearchEngine;
import org.eclipse.dltk.core.search.TypeNameRequestor;
import org.eclipse.dltk.internal.compiler.env.AccessRestriction;

/**
 * Wrapper used to link {@link IRestrictedAccessTypeRequestor} with
 * {@link TypeNameRequestor}. This wrapper specifically allows usage of internal
 * method
 * {@link BasicSearchEngine#searchAllTypeNames(char[] packageName, int packageMatchRule, char[] typeName, int typeMatchRule, int searchFor, org.eclipse.dltk.core.search.IDLTKSearchScope scope, IRestrictedAccessTypeRequestor nameRequestor, int waitingPolicy, org.eclipse.core.runtime.IProgressMonitor monitor) }
 * . from API method
 * {@link org.eclipse.dltk.core.search.SearchEngine#searchAllTypeNames(char[] packageName, char[] typeName, int matchRule, int searchFor, org.eclipse.dltk.core.search.IDLTKSearchScope scope, TypeNameRequestor nameRequestor, int waitingPolicy, org.eclipse.core.runtime.IProgressMonitor monitor) }
 * .
 */
public class TypeNameRequestorWrapper implements IRestrictedAccessTypeRequestor {
	TypeNameRequestor requestor;

	public TypeNameRequestorWrapper(TypeNameRequestor requestor) {
		this.requestor = requestor;
	}

	@Override
	public void acceptType(int modifiers, char[] packageName,
			char[] simpleTypeName, char[][] enclosingTypeNames,
			char[][] superTypes, String path, AccessRestriction access) {
		this.requestor.acceptType(modifiers, packageName, simpleTypeName,
				enclosingTypeNames, superTypes, path);
	}
}
