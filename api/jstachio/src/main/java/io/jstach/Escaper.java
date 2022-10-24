package io.jstach;

import java.io.IOException;

public interface Escaper extends Appender<Appendable> {

	enum NoEscaper implements Escaper {

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

}
