package io.jstach.spec.generator;

import org.kohsuke.MetaInfServices;

import io.jstach.jstachio.spi.JStacheServices;

@MetaInfServices(JStacheServices.class)
public class SpecRenderService implements JStacheServices {

	// @Override
	// public Formatter formatter(Formatter previous) {
	// return MyFormatter.INSTANCE;
	// }
	//
	// private enum MyFormatter implements Formatter {
	//
	// INSTANCE;
	//
	// @Override
	// public <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER
	// downstream, A a, String path,
	// Class<?> c, @Nullable Object o) throws IOException {
	// if (o != null) {
	// downstream.append(a, String.valueOf(o));
	// }
	// }

	// }

}
