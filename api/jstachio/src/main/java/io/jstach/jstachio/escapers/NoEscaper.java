package io.jstach.jstachio.escapers;

import io.jstach.jstachio.Escaper;
import io.jstach.jstachio.Output;

enum NoEscaper implements Escaper {

	PlainText;

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence s) throws E {
		a.append(s);
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence csq, int start, int end) throws E {
		a.append(csq, start, end);
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, char c) throws E {
		a.append(c);
	}

}