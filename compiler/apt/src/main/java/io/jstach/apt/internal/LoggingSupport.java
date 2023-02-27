package io.jstach.apt.internal;

public interface LoggingSupport {

	public boolean isDebug();

	public void debug(CharSequence message);

	public interface LoggingSupplier extends LoggingSupport {

		public LoggingSupport logging();

		default boolean isDebug() {
			return logging().isDebug();
		}

		default void debug(CharSequence message) {
			logging().debug(message);
		}

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

	}

}
