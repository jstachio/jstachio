package io.jstach.jstachio;

import java.util.ServiceLoader;

/**
 * An SPI factory interface to load the default singleton JStachio generally using the
 * {@link ServiceLoader}.
 *
 * @author agentgt
 *
 */
public interface JStachioProvider {

	/**
	 * Provides a jstachio.
	 * @return non null jstachio.
	 */
	public JStachio provideJStachio();

	/**
	 * Set the static singleton of JStachio.
	 * <p>
	 * Useful if you would like to avoid using the default ServiceLoader mechanism.
	 * @param jstachio if null a NPE will be thrown.
	 */
	public static void setStaticJStachio(JStachio jstachio) {
		jstachio.getClass(); // to trigger NPE
		JStachioHolder.jstachio = jstachio;
	}

}

final class JStachioHolder {

	static JStachio jstachio;

	static JStachio get() {
		JStachio j = jstachio;
		if (j == null) {
			jstachio = j = load();
		}
		return j;
	}

	private static JStachio load() {
		return io.jstach.jstachio.spi.JStachioServices.find().provideJStachio();
	}

}