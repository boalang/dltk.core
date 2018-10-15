/*******************************************************************************
 * Copyright (c) 2012 NumberFour AG
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.text.completion;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

/**
 * @since 4.0
 */
public abstract class LinkedModeScriptCompletionProposal extends
		LazyScriptCompletionProposal {

	public LinkedModeScriptCompletionProposal(CompletionProposal proposal,
			ScriptContentAssistInvocationContext context) {
		super(proposal, context);
	}

	private IRegion fSelectedRegion; // initialized by apply()

	@Override
	public void apply(IDocument document, char trigger, int offset) {
		if (trigger == ' ' || trigger == getOpenTrigger())
			trigger = '\0';
		super.apply(document, trigger, offset);

		final int replacementOffset = getReplacementOffset();
		final int exit = replacementOffset + getReplacementString().length();

		if (replacementBuffer != null && replacementBuffer.hasArguments()
				&& getTextViewer() != null) {
			final int cursor = getCursorPosition();
			try {
				LinkedModeModel model = new LinkedModeModel();
				for (ReplacementBuffer.Argument region : replacementBuffer.arguments) {
					LinkedPositionGroup group = new LinkedPositionGroup();
					int argOffset = replacementOffset + region.offset;
					if (region.relativeToCursor)
						argOffset += cursor;
					group.addPosition(new LinkedPosition(document, argOffset,
							region.length, LinkedPositionGroup.NO_STOP));
					model.addGroup(group);
				}

				model.forceInstall();

				LinkedModeUI ui = new EditorLinkedModeUI(model, getTextViewer());
				ui.setExitPosition(getTextViewer(), exit, 0, Integer.MAX_VALUE);
				char exitTrigger = getExitTrigger();
				if (exitTrigger != 0) {
					ui.setExitPolicy(new ExitPolicy(exitTrigger, document));
				}
				final char[] exitTriggers = getExitTriggers();
				if (exitTriggers != null) {
					for (int i = 0; i < exitTriggers.length; ++i) {
						ui.setExitPolicy(new ExitPolicy(exitTriggers[i],
								document));
					}
				}

				ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
				ui.enter();

				fSelectedRegion = ui.getSelectedRegion();

			} catch (BadLocationException e) {
			}
		} else {
			fSelectedRegion = new Region(replacementOffset
					+ getCursorPosition(), 0);
		}
	}

	protected abstract char getOpenTrigger();

	protected char getExitTrigger() {
		return 0;
	}

	protected char[] getExitTriggers() {
		return null;
	}

	/**
	 * @see org.eclipse.dltk.ui.text.completion.AbstractScriptCompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	@Override
	public Point getSelection(IDocument document) {
		if (fSelectedRegion == null)
			return new Point(getReplacementOffset(), 0);

		return new Point(fSelectedRegion.getOffset(),
				fSelectedRegion.getLength());
	}

	private ReplacementBuffer replacementBuffer;

	/**
	 * Override {@link #computeReplacement(IReplacementBuffer)}
	 */
	@Override
	protected final String computeReplacementString() {
		replacementBuffer = new ReplacementBuffer();
		computeReplacement(replacementBuffer);
		return replacementBuffer.toString();
	}

	protected abstract void computeReplacement(ReplacementBuffer buffer);

}
