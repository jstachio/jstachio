package io.jstach.jstachio.escapers;

import io.jstach.jstachio.Escaper;
import io.jstach.jstachio.Output;

enum HtmlEscaper implements Escaper {

	Html;

	private static final String QUOT = "&quot;";

	private static final String GT = "&gt;";

	private static final String LT = "&lt;";

	private static final String AMP = "&amp;";

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
				case '&' -> {
					a.append(csq, start, i);
					start = i + 1;
					a.append(AMP);

				}
				case '<' -> {
					a.append(csq, start, i);
					start = i + 1;
					a.append(LT);

				}
				case '>' -> {
					a.append(csq, start, i);
					start = i + 1;
					a.append(GT);
				}
				case '"' -> {
					a.append(csq, start, i);
					start = i + 1;
					a.append(QUOT);
				}

			}
		}
		a.append(csq, start, end);

	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, char c) throws E {
		switch (c) {
			case '&' -> {
				a.append(AMP);
			}
			case '<' -> {
				a.append(LT);
			}
			case '>' -> {
				a.append(GT);
			}
			case '"' -> {
				a.append(QUOT);
			}
			default -> {
				a.append(c);
			}
		}
	}

}
