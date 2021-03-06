/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.testing.util;

public interface IDLTKTestingStatusConstants {

	// JUnit UI status constants start at 10000 to make sure that we don't
	// collide with resource and java model constants.
	
	public static final int INTERNAL_ERROR= 10001;
	
	/**
	 * Status constant indicating that an validateEdit call has changed the
	 * content of a file on disk.
	 */
	public static final int VALIDATE_EDIT_CHANGED_CONTENT= 10003;

	/**
	 * Status constant indicating that junit.framework.TestCase
	 * is not on the project's path.
	 */
	public static final int ERR_JUNIT_NOT_ON_PATH = 10004;
	
}
