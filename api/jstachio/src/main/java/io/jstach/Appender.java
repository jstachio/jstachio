package io.jstach;

import java.io.IOException;

public interface Appender<A extends Appendable> {

	public void append(A a, CharSequence s) throws IOException;

	public void append(A a, CharSequence csq, int start, int end) throws IOException;

	public void append(A a, char c) throws IOException;

	default void append(A a, short s) throws IOException {
		append(a, String.valueOf(s));
	}

	default void append(A a, int i) throws IOException {
		append(a, String.valueOf(i));
	}

	default void append(A a, long l) throws IOException {
		append(a, String.valueOf(l));
	}

	default void append(A a, double d) throws IOException {
		append(a, String.valueOf(d));
	}

	default void append(A a, boolean b) throws IOException {
		append(a, String.valueOf(b));
	}

	enum DefaultAppender implements Appender<Appendable> {

		INSTANCE;

		@Override
		public void append(Appendable a, CharSequence s) throws IOException {
			a.append(s);
		}

		@Override
		public void append(Appendable a, CharSequence csq, int start, int end) throws IOException {
			a.append(csq, start, end);
		}

		@Override
		public void append(Appendable a, char c) throws IOException {
			a.append(c);
		}

	}

	enum StringAppender implements Appender<StringBuilder> {

		INSTANCE;

		@Override
		public void append(StringBuilder a, CharSequence s) throws IOException {
			a.append(s);
		}

		@Override
		public void append(StringBuilder a, CharSequence csq, int start, int end) throws IOException {
			a.append(csq, start, end);
		}

		@Override
		public void append(StringBuilder a, char c) throws IOException {
			a.append(c);
		}

		public void append(StringBuilder a, short s) throws IOException {
			a.append(s);
		}

		public void append(StringBuilder a, int i) throws IOException {
			a.append(i);
		}

		public void append(StringBuilder a, long l) throws IOException {
			a.append(l);
		}

		public void append(StringBuilder a, double d) throws IOException {
			a.append(d);
		}

		public void append(StringBuilder a, boolean b) throws IOException {
			a.append(b);
		}

	}

}
