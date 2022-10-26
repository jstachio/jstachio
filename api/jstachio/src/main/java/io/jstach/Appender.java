package io.jstach;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * A singleton like decorator for appendables that has additional methods for dealing with
 * native types.
 *
 * @apiNote Unlike an Appendable this class is expected to be reused so avoid state.
 * @author agentgt
 * @param <A> the appendable
 * @see Escaper
 */
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

	default Appendable toAppendable(A appendable) {
		return new AppenderAppendable<>(this, appendable);
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

	/**
	 * An appender that will directly call StringBuilder methods for native types.
	 * <p>
	 * This is a low level utility class for where performance matters.
	 *
	 * @author agentgt
	 *
	 */
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

	class AppenderAppendable<A extends Appendable> implements Appendable {

		private final Appender<A> appender;

		private final A appendable;

		public AppenderAppendable(Appender<A> appender, A appendable) {
			super();
			this.appender = appender;
			this.appendable = appendable;
		}

		@Override
		public @NonNull Appendable append(@Nullable CharSequence csq) throws @Nullable IOException {
			appender.append(appendable, csq);
			return this;
		}

		@Override
		public @NonNull Appendable append(@Nullable CharSequence csq, int start, int end) throws IOException {
			appender.append(appendable, csq, start, end);
			return this;
		}

		@Override
		public @NonNull Appendable append(char c) throws IOException {
			appender.append(appendable, c);
			return this;
		}

	}

}
