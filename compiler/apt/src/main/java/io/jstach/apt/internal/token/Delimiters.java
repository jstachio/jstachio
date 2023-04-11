package io.jstach.apt.internal.token;

public record Delimiters(char start1, char start2, char end1, char end2) {

	public static Delimiters defaults() {
		return new Delimiters('{', '{', '}', '}');
	}

	public boolean requiresStart2() {
		if (start2 == Character.MIN_VALUE) {
			return false;
		}
		return true;
	}

	public boolean requiresEnd2() {
		if (end2 == Character.MIN_VALUE) {
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

}
