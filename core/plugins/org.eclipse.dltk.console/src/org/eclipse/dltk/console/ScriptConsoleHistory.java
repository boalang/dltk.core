/*******************************************************************************
 * Copyright (c) 2005, 2017 IBM Corporation and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *******************************************************************************/
package org.eclipse.dltk.console;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.dltk.compiler.util.Util;

public class ScriptConsoleHistory {

	/**
	 * History items. Items are added to the end. Always has at least one item,
	 * since empty line is added to keep the value of the currently selected
	 * line.
	 */
	private final List<String> lines = new ArrayList<>();

	public ScriptConsoleHistory() {
		lines.add(Util.EMPTY_STRING);
	}

	/**
	 * The index of the current item.
	 *
	 * Invariant:
	 * <code>selection &gt;= 0 &gt;&gt; selection < lines.size()</code>
	 */
	private int selection = 0;

	private void addToHistory(String line) {
		final int index = lines.indexOf(line);
		if (index >= 0) {
			if (index != lines.size() - 1) {
				lines.remove(index);
			}
		}
		lines.set(lines.size() - 1, line);
	}

	/**
	 * Adds the specified line to the top of the history
	 *
	 * @param line
	 */
	public void add(String line) {
		if (line != null && line.length() != 0) {
			addToHistory(line);
			lines.add(Util.EMPTY_STRING);
			selection = lines.size() - 1;
		}
	}

	/**
	 * Moves the selection to the previous item. Returns <code>true</code> on
	 * success or <code>false</code> otherwise.
	 *
	 * @return
	 */
	public boolean prev() {
		if (selection > 0) {
			--selection;
			return true;
		}
		return false;
	}

	/**
	 * Moves the selection to the next item. Returns <code>true</code> on
	 * success or <code>false</code> otherwise.
	 *
	 * @return
	 */
	public boolean next() {
		if (selection < lines.size() - 1) {
			++selection;
			return true;
		}
		return false;
	}

	/**
	 * Returns the text of the currently selected line.
	 *
	 * @return
	 */
	public String get() {
		return lines.get(selection);
	}

	/**
	 * Updates the text of the currently selected line
	 *
	 * @param line
	 */
	public void updateSelectedLine(String line) {
		if (selection >= 0 && selection < lines.size()) {
			/*
			 * TODO probably it should a temporary change until a command is
			 * executed, so the history will contain only commands which were
			 * actually executed.
			 */
			lines.set(selection, line);
		}
	}

	public void restoreState(String history) {
		if (history != null && history.length() != 0) {
			StringTokenizer st = new StringTokenizer(history, "\n"); //$NON-NLS-1$
			while (st.hasMoreTokens()) {
				add(st.nextToken());
			}
		}
	}

	public String saveState() {
		int size = Math.min(lines.size(), 50);
		StringBuilder sb = new StringBuilder(size * 10);
		for (int i = 0; i < size; i++) {
			sb.append(lines.get(i));
			sb.append("\n"); //$NON-NLS-1$
		}
		return sb.toString();
	}
}
