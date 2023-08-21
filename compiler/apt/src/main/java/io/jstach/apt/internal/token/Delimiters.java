package io.jstach.apt.internal.token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record Delimiters(char start1, char start2, char end1, char end2) {

	private static final Delimiters defaultDelimiters = new Delimiters('{', '{', '}', '}');

	public static Delimiters of() {
		return defaultDelimiters;
	}

	public static final char NO_CHAR = Character.MIN_VALUE;

	/*
	 * This delimiter parsing code was inspired and taken from JMustache.
	 * https://github.com/samskivert/jmustache
	 *
	 * Normally we would implement our own parsing w/o taking from other code bases but we
	 * want compatibility with JMustache.
	 *
	 * EDIT because of spec considerations the JMustache like code below has been altered.
	 *
	 * The real solution is to put ina PR to JMustache to fix it.
	 */
	public static Delimiters of(String content) throws DelimiterParsingException {
		String[] delims = content.trim().split("\\s+");
		if (delims.length != 2)
			throw new DelimiterParsingException(content);

		char start1, start2, end1, end2;

		switch (delims[0].length()) {
			case 1 -> {
				start1 = delims[0].charAt(0);
				start2 = NO_CHAR;
			}
			case 2 -> {
				start1 = delims[0].charAt(0);
				start2 = delims[0].charAt(1);
			}
			default -> throw new DelimiterParsingException(content);
		}

		switch (delims[1].length()) {
			case 1 -> {
				end1 = delims[1].charAt(0);
				end2 = NO_CHAR;
			}
			case 2 -> {
				end1 = delims[1].charAt(0);
				end2 = delims[1].charAt(1);
			}
			default -> throw new DelimiterParsingException(content);
		}
		return new Delimiters(start1, start2, end1, end2);
	}

	public boolean isDefault() {
		return defaultDelimiters.equals(this);
	}

	static class DelimiterParsingException extends Exception {

		private static final long serialVersionUID = -6891726165744713522L;

		private final String message;

		public DelimiterParsingException(String content) {
			super("Cannot parse delimiters from '" + content + "'");
			this.message = "Cannot parse delimiters from '" + content + "'";
		}

		public String getMessage() {
			return this.message;
		}

	}

	public Appendable appendStart(Appendable a) throws IOException {
		a.append(start1);
		if (requiresStart2()) {
			a.append(start2);
		}
		return a;
	}

	public Appendable appendStartEscape(Appendable a) throws IOException {
		appendStart(a);
		a.append(start3());
		return a;
	}

	public Appendable appendEndEscape(Appendable a) throws IOException {
		appendEnd(a);
		a.append(end3());
		return a;
	}

	public Appendable appendEnd(Appendable a) throws IOException {
		a.append(end1);
		if (requiresEnd2()) {
			a.append(end2);
		}
		return a;
	}

	public boolean requiresStart2() {
		if (start2 == NO_CHAR) {
			return false;
		}
		return true;
	}

	public boolean requiresEnd2() {
		if (end2 == NO_CHAR) {
			return false;
		}
		return true;
	}

	public char start3() {
		return '{';
	}

	public char end3() {
		return '}';
	}

	public interface Subscriber {

		void setDelimters(Delimiters delimiters);

	}

	public static class Publisher {

		private List<Subscriber> subscribers = new ArrayList<>();

		private Delimiters delimiters = Delimiters.of();

		public void subscribe(Subscriber subscriber) {
			subscribers.add(subscriber);
			subscriber.setDelimters(delimiters);
		}

		public void setDelimiters(Delimiters delimiters) {
			this.delimiters = delimiters;
			for (var s : subscribers) {
				s.setDelimters(delimiters);
			}
		}

		public Delimiters getDelimiters() {
			return delimiters;
		}

	}

}
