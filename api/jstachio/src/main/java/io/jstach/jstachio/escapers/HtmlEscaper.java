package io.jstach.jstachio.escapers;

import io.jstach.jstachio.Escaper;
import io.jstach.jstachio.Output;

enum HtmlEscaper implements Escaper {

	Html;

	static final String QUOT = "&quot;";

	static final String AMP = "&amp;";

	static final String APOS = "&#x27;";

	static final String LT = "&lt;";

	static final String EQUAL = "&#x3D;";

	static final String GT = "&gt;";

	static final String BACK_TICK = "&#x60;";

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence s) throws E {
		s = s == null ? "null" : s;
		append(a, s, 0, s.length());
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence csq, int start, int end) throws E {
		csq = csq == null ? "null" : csq;
		for (int i = start; i < end; i++) {
			char c = csq.charAt(i);
			switch (c) {
				case '"' -> { // 34
					a.append(csq, start, i);
					start = i + 1;
					a.append(QUOT);
				}
				case '&' -> { // 38
					a.append(csq, start, i);
					start = i + 1;
					a.append(AMP);

				}
				case '\'' -> { // 39
					a.append(csq, start, i);
					start = i + 1;
					a.append(APOS);
				}
				case '<' -> { // 60
					a.append(csq, start, i);
					start = i + 1;
					a.append(LT);
				}
				case '=' -> { // 61
					a.append(csq, start, i);
					start = i + 1;
					a.append(EQUAL);
				}
				case '>' -> { // 62
					a.append(csq, start, i);
					start = i + 1;
					a.append(GT);
				}
				case '`' -> { // 96
					a.append(csq, start, i);
					start = i + 1;
					a.append(BACK_TICK);
				}
			}
		}
		a.append(csq, start, end);

	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, char c) throws E {
		switch (c) {
			case '"' -> {
				a.append(QUOT);
			}
			case '&' -> {
				a.append(AMP);
			}
			case '\'' -> {
				a.append(APOS);
			}
			case '<' -> {
				a.append(LT);
			}
			case '=' -> {
				a.append(EQUAL);
			}
			case '>' -> {
				a.append(GT);
			}
			case '`' -> {
				a.append(BACK_TICK);
			}
			default -> {
				a.append(c);
			}
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

}
