package io.jstach.examples.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

import org.junit.Test;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.spi.JStachioConfig;
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

	@Test
	public void testAddTemplate() throws Exception {
		JStachio jstachio = JStachioFactory.builder().add(FactoryModelRenderer.of()).build();
		var fm = new FactoryModel("blah");
		String actual = jstachio.execute(fm);
		assertEquals("blah", actual);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testMissingTemplate() throws Exception {
		JStachio jstachio = JStachioFactory.builder().build();
		jstachio.execute(new NotRegistered());

	}

	@Test
	public void testDisableReflection() throws Exception {

		Map<String, String> props = new LinkedHashMap<>();
		props.put(JStachioConfig.REFLECTION_TEMPLATE_DISABLE, "" + true);

		JStachioConfig config = props::get;

		JStachio jstachio = JStachioFactory.builder().add(FactoryModelRenderer.of()).add(config).build();
		var fm = new FactoryModel("blah");

		String actual = jstachio.execute(fm);

		assertEquals("blah", actual);

		try {
			jstachio.execute(new NotRegistered());
			fail("expected exception");
		}
		catch (NoSuchElementException e) {
			assertEquals(
					"template not found for type: class io.jstach.examples.factory.JStachioFactoryTest$NotRegistered",
					e.getMessage());
			System.out.println(e.getMessage());
		}

	}

	public record NotRegistered() {
	}

	@JStache(template = "{{message}}")
	public record FactoryModel(String message) {

	}

}
