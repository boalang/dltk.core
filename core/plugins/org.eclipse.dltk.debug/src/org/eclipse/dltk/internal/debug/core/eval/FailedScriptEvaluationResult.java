package org.eclipse.dltk.internal.debug.core.eval;

import org.eclipse.debug.core.DebugException;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationResult;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.debug.core.model.IScriptValue;

public class FailedScriptEvaluationResult implements IScriptEvaluationResult {
	private final IScriptThread thread;
	private final String snippet;
	private DebugException exception;
	private String[] messages;

	public FailedScriptEvaluationResult(IScriptThread thread, String snippet,
			DebugException exception) {
		this.thread = thread;
		this.snippet = snippet;
		this.exception = exception;
	}

	public FailedScriptEvaluationResult(IScriptThread thread, String snippet,
			String[] messages) {
		this.thread = thread;
		this.snippet = snippet;
		this.messages = messages;
	}

	public FailedScriptEvaluationResult(IScriptThread thread, String snippet,
			DebugException exception, String[] messages) {
		this.thread = thread;
		this.snippet = snippet;
		this.exception = exception;
		this.messages = messages;
	}

	@Override
	public boolean hasErrors() {
		return true;
	}

	@Override
	public String[] getErrorMessages() {
		if (messages != null) {
			return messages;
		} else if (exception != null) {
			return new String[] { exception.getMessage() };
		}

		return new String[0];
	}

	@Override
	public DebugException getException() {
		return exception;
	}

	@Override
	public String getSnippet() {
		return snippet;
	}

	@Override
	public IScriptThread getThread() {
		return thread;
	}

	@Override
	public IScriptValue getValue() {
		return null;
	}
}
