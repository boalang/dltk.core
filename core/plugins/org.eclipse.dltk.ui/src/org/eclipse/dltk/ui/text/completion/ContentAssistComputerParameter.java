/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.ui.text.completion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;

/**
 * Map of parameters for the specific content assist command.
 */
public final class ContentAssistComputerParameter implements IParameterValues {
	@Override
	public Map getParameterValues() {
		Collection descriptors = CompletionProposalComputerRegistry
				.getDefault().getProposalCategories();

		Map map = new HashMap(descriptors.size());
		for (Iterator it = descriptors.iterator(); it.hasNext();) {
			CompletionProposalCategory category = (CompletionProposalCategory) it
					.next();
			map.put(category.getDisplayName(), category.getId());
		}
		return map;
	}
}
