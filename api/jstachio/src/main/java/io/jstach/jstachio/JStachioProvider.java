package io.jstach.jstachio;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.jdt.annotation.NonNull;

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

	private static final String DEFAULT_JSTACHIO_PROVIDER = "io.jstach.jstachio.spi.JStachioServices";

	private static JStachio load() {
		List<ClassLoader> classLoaders = new ArrayList<>();
		classLoaders.add(JStachioProvider.class.getClassLoader());
		var tcl = Thread.currentThread().getContextClassLoader();
		if (tcl != null) {
			classLoaders.add(tcl);
		}
		JStachioProvider provider = null;
		for (var cl : classLoaders) {
			provider = ServiceLoader.load(JStachioProvider.class, cl).findFirst().orElse(null);
			if (provider != null) {
				break;
			}
		}
		if (provider == null) {
			for (var cl : classLoaders) {
				try {
					@NonNull
					Method method = cl.loadClass(DEFAULT_JSTACHIO_PROVIDER).getMethod("provides");
					provider = (JStachioProvider) method.invoke(null);
					break;
				}
				catch (@NonNull Exception e) {
				}

			}
		}
		if (provider == null) {
			throw new RuntimeException("JStachio not found");
		}
		return provider.provideJStachio();
	}

}