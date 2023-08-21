package io.jstach.apt.internal;

import java.io.PrintStream;
import java.util.Objects;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.context.JavaLanguageModel;

public sealed interface LoggingSupport {

	public boolean isDebug();

	public void debug(CharSequence message);

	default void debug(Throwable t) {
		if (isDebug()) {
			t.printStackTrace(errorWriter());
		}
	}

	default PrintStream errorWriter() {
		return Objects.requireNonNull(System.err);
	}

	default PrintStream outWriter() {
		return Objects.requireNonNull(System.out);
	}

	public void info(CharSequence message);

	default void debug(CharSequence message, Object a) {
		if (isDebug()) {
			debug(message + String.valueOf(a));
		}
	}

	default void debug(CharSequence message, Object a, Object b) {
		if (isDebug()) {
			debug(message + String.valueOf(a) + String.valueOf(b));
		}
	}

	public void error(CharSequence message, Throwable t);

	public non-sealed interface MessagerLogging extends LoggingSupport {

		@Nullable
		AnnotationMirror annotationToLog();

		Element elementToLog();

		Messager messager();

		default void printError(CharSequence message) {
			var m = messager();
			var a = annotationToLog();
			var e = elementToLog();
			if (a == null) {
				m.printMessage(Kind.ERROR, message, e);
			}
			else {
				m.printMessage(Kind.ERROR, message, e, a);
			}
		}

		@Override
		default void info(CharSequence message) {
			messager().printMessage(Kind.NOTE, message);
		}

	}

	public record AdHocMessager(String prefix, boolean isDebug, Element elementToLog,
			AnnotationMirror annotationToLog) implements MessagerLogging {

		@Override
		public void debug(CharSequence message) {
			if (isDebug()) {
				outWriter().println(prefix + message);
			}
		}

		@Override
		public void error(CharSequence message, Throwable t) {
			String m = message + t.getMessage();
			printError(m);
			var out = errorWriter();
			out.println(m);
			t.printStackTrace(out);
		}

		@Override
		public Messager messager() {
			return JavaLanguageModel.getInstance().getMessager();
		}

	}

	static LoggingSupport testLogger() {
		return new TestLogging();
	}

	final class TestLogging implements LoggingSupport {

		@Override
		public boolean isDebug() {
			return true;
		}

		@Override
		public void debug(CharSequence message) {
			outWriter().println("[TEST] " + message);
		}

		@Override
		public void error(CharSequence message, Throwable t) {
			errorWriter().println("[ERROR] " + message);
		}

		@Override
		public void info(CharSequence message) {
			outWriter().println("[INFO] " + message);

		}

	}

	record RootLogging(Messager messager, boolean isDebug) implements LoggingSupport {

		@Override
		public void debug(CharSequence message) {
			if (isDebug()) {
				messager.printMessage(Kind.NOTE, message);
				outWriter().println("[JSTACHIO] " + message);
			}
		}

		@Override
		public void error(CharSequence message, Throwable t) {
			String m = message + " " + t.getMessage();
			messager.printMessage(Kind.ERROR, m);
			errorWriter().println("[JSTACHIO] " + m);
		}

		@Override
		public void info(CharSequence message) {
			messager.printMessage(Kind.NOTE, message);
		}
	}

	public interface LoggingSupplier {

		public LoggingSupport logging();

		default boolean isDebug() {
			return logging().isDebug();
		}

		default void debug(Throwable t) {
			logging().debug(t);
		}

		default void debug(CharSequence message) {
			logging().debug(message);
		}

		default void debug(CharSequence message, Object a) {
			logging().debug(message, a);
		}

		default void debug(CharSequence message, Object a, Object b) {
			logging().debug(message, a, b);
		}

		default void error(CharSequence message, Throwable t) {
			logging().error(message, t);
		}

	}

}
