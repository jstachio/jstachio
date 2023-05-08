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