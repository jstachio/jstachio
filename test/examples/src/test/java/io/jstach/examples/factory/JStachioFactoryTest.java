package io.jstach.examples.factory;

import static org.junit.Assert.*;

import java.util.ServiceLoader;

import org.junit.Test;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.spi.JStachioExtension;
import io.jstach.jstachio.spi.JStachioFactory;

public class JStachioFactoryTest {

	public static JStachio create() {
		JStachio jstachio = JStachioFactory.builder().add(ServiceLoader.load(JStachioExtension.class)).build();
		return jstachio;
	}

	@Test
	public void testBuilder() throws Exception {
		JStachio custom = create();
		assertFalse(custom == JStachio.of());
	}

}
