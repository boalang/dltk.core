/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
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
package org.eclipse.dltk.core.tests;

import java.util.List;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;

public class TestCompletionRequestor extends CompletionRequestor {
	private final List<CompletionProposal> results;

	public TestCompletionRequestor(List<CompletionProposal> results) {
		this.results = results;
	}

	@Override
	public void accept(CompletionProposal proposal) {
		if (!isIgnored(proposal.getKind())) {
			final int value = evaluateFilters(proposal);
			if (value != CompletionProposalFilter.IGNORE
					&& value != CompletionProposalFilter.DISCOURAGED) {
				results.add(proposal);
			}
		}
	}
}
