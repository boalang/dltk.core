/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.testing.launcher;

import java.util.Comparator;

public class ContainerComparator implements Comparator<String> {

    @Override
	public int compare(String container1, String container2) {
		if (container1 == null)
			container1= ""; //$NON-NLS-1$
		if (container2 == null)
			container2= ""; //$NON-NLS-1$
		return container1.compareTo(container2);
    }
}
