package io.jstach.apt.internal.token;

import java.util.ArrayList;
import java.util.List;

public record Delimiters(char start1, char start2, char end1, char end2) {

	public static Delimiters of() {
		return new Delimiters('{', '{', '}', '}');
	}

	public static final char NO_CHAR = Character.MIN_VALUE;

	/*
	 * This delimiter parsing code was inspired and taken from
	 * JMustache.
	 * https://github.com/samskivert/jmustache
	 * 
	 * Normally we would implement our own parsing w/o taking
	 * from other code bases but we want compatibility with JMustache 
	 */
	public static Delimiters of(String content) throws DelimiterParsingException {
		String[] delims = content.split(" ");
		if (delims.length != 2)
			throw new DelimiterParsingException(content);

		char start1, start2, end1, end2;

		switch (delims[0].length()) {
			case 1:
				start1 = delims[0].charAt(0);
				start2 = NO_CHAR;
				break;
			case 2:
				start1 = delims[0].charAt(0);
				start2 = delims[0].charAt(1);
				break;
			default:
				throw new DelimiterParsingException(content);
		}

		switch (delims[1].length()) {
			case 1:
				end1 = delims[1].charAt(0);
				end2 = NO_CHAR;
				break;
			case 2:
				end1 = delims[1].charAt(0);
				end2 = delims[1].charAt(1);
				break;
			default:
				throw new DelimiterParsingException(content);
		}
		return new Delimiters(start1, start2, end1, end2);
	}

	static class DelimiterParsingException extends Exception {

		private static final long serialVersionUID = -6891726165744713522L;

		public DelimiterParsingException(String content) {
			super("Cannot parse delimiters from '" + content + "'");
		}

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
