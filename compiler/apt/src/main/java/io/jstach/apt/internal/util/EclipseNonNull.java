package io.jstach.apt.internal.util;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/*
 * Anytime we use this it means it is a bug in Eclipse.
 * If you do not understand nullable annotations do not use this stuff.
 * Almost always it is something to do with records at the moment as
 * eclipse has issues with propagating null annotations to the accessors.
 */
public class EclipseNonNull {

	@SuppressWarnings("null")
	public static <T extends @Nullable Object> @NonNull T castNonNull(@Nullable T ref) {
		return (T) ref;
	}

	@SuppressWarnings("null")
	public static <T> @NonNull List<@NonNull T> castNonNullList(@Nullable List<T> ref) {
		return ref;
	}

	@SuppressWarnings("null")
	public static <T> @NonNull T[] castNonNullArray(T @Nullable [] ref) {
		return ref;
	}

	public static PrintStream systemOut() {
		return Objects.requireNonNull(System.out);
	}

}
