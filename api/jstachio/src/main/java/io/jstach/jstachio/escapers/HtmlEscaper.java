package io.jstach.jstachio.escapers;

import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.Escaper;
import io.jstach.jstachio.Output;

enum HtmlEscaper implements Escaper {

	HTML5(defaultMappings());

	private final String[] lookupTable;

	HtmlEscaper(Map<Character, String> mapping) {
		String[] table = createTable(mapping);
		this.lookupTable = table;
	}

	static final String QUOT = "&quot;";

	static final String AMP = "&amp;";

	static final String APOS = "&#x27;";

	static final String LT = "&lt;";

	static final String EQUAL = "&#x3D;";

	static final String GT = "&gt;";

	static final String BACK_TICK = "&#x60;";

	static Map<Character, String> defaultMappings() {
		return Map.<Character, String>of('"', QUOT, //
				'&', AMP, //
				'\'', APOS, '<', LT, '=', EQUAL, '>', GT, '`', BACK_TICK);
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence s) throws E {
		append(a, s, 0, s.length());
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence csq, int start, int end) throws E {
		escape(a, csq, lookupTable);
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, char c) throws E {
		String escaped = escapeChar(lookupTable, c);
		if (escaped != null) {
			a.append(escaped);
		}
		else {
			a.append(c);
		}
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, short s) throws E {
		a.append(s);
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, int i) throws E {
		a.append(i);
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, long l) throws E {
		a.append(l);
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, double d) throws E {
		a.append(d);
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, boolean b) throws E {
		a.append(b);
	}

	private static String[] createTable(Map<Character, String> mapping) {
		String[] table = new String[128];
		for (var entry : mapping.entrySet()) {
			char k = entry.getKey();
			String value = entry.getValue();
			if (k > 127) {
				throw new IllegalArgumentException("char '" + k + "' cannot be mapped as it is greater than 127");
			}
			table[k] = value;
		}
		return table;
	}

	private static @Nullable String escapeChar(String[] lookupTable, char c) {
		if (c > 127) {
			return null;
		}
		return lookupTable[c];
	}

	private static <A extends Output<E>, E extends Exception> void escape(A a, CharSequence raw, String[] lookupTable)
			throws E {
		int end = raw.length();
		for (int i = 0, start = 0; i < end; i++) {
			char c = raw.charAt(i);
			String found = escapeChar(lookupTable, c);
			/*
			 * While this could be done with one loop it appears through benchmarking that
			 * by having the first loop assume the string to be not changed creates a fast
			 * path for strings with no escaping needed.
			 */
			if (found != null) {
				a.append(raw, 0, i);
				a.append(found);
				start = i = i + 1;
				for (; i < end; i++) {
					c = raw.charAt(i);
					found = escapeChar(lookupTable, c);
					if (found != null) {
						a.append(raw, start, i);
						a.append(found);
						start = i + 1;
					}
				}
				a.append(raw, start, end);
				return;
			}
		}
		a.append(raw);

	}

}
