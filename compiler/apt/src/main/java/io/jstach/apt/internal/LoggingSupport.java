package io.jstach.apt.internal;

public interface LoggingSupport {

	public boolean isDebug();

	public void debug(CharSequence message);

}
