package io.jstach.examples.finder;

import java.util.Map;

import org.junit.Test;

import io.jstach.jstachio.spi.JStachioConfig;
import io.jstach.jstachio.spi.JStachioFactory;

public class NoReflectModelTest {

	@Test
	public void testName() throws Exception {
		var m = Map.of(JStachioConfig.REFLECTION_TEMPLATE_DISABLE, "true");
		JStachioConfig config = m::get;
		var jstachio = JStachioFactory.builder().add(config).build();

		jstachio.execute(new NoReflectModel("asdfasdf"));
	}

}
