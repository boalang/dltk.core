package org.eclipse.dltk.debug.core.model;

public class StringScriptType extends AtomicScriptType {
	public StringScriptType(String name) {
		super(name);
	}

	@Override
	public boolean isString() {
		return true;
	}

	@Override
	public String formatValue(IScriptValue value) {
		String string = value.getRawValue();

		if (string == null) {
			return null;
		}
		return escapeString(string);
	}

	private static String escapeString(String string) {
		final boolean alreadyQuoted = isQuoted(string);
		final boolean escapeNeed = isEscapeNeeded(string, alreadyQuoted);
		if (!escapeNeed) {
			if (alreadyQuoted) {
				return string;
			} else {
				return '"' + string + '"';
			}
		}
		final StringBuffer escaped = new StringBuffer(string.length() + 8);
		if (!alreadyQuoted) {
			escaped.append('"');
		}
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
			case '"':
				escaped.append("\\\""); //$NON-NLS-1$
				break;
			default:
				escaped.append(c);
				break;
			}
		}
		if (!alreadyQuoted) {
			escaped.append('"');
		}
		return escaped.toString();
	}

	private static boolean isQuoted(String string) {
		if (string.length() >= 2) {
			final char firstChar = string.charAt(0);
			final char lastChar = string.charAt(string.length() - 1);
			if (firstChar == '\'' && lastChar == '\''
					|| firstChar == '"' && lastChar == '"') {
				return true;
			}
		}
		return false;
	}

	private static boolean isEscapeNeeded(String string, boolean isQuoted) {
		int i = 0, len = string.length();
		if (isQuoted) {
			++i;
			--len;
		}
		for (; i < len; ++i) {
			if (string.charAt(i) == '"') {
				return true;
			}
		}
		return false;
	}
}
