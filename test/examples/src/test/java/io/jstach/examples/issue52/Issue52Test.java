package io.jstach.examples.issue52;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.formatters.SpecFormatter;

public class Issue52Test {

	@Test
	public void testDefaultFormatterShouldFailForNullStrings() throws Exception {
		try {
			NullStringModelRenderer.of().execute(new NullStringModel(null));
			fail("expected null pointer exception");
		}
		catch (NullPointerException e) {
			String message = e.getMessage();
			assertEquals("null at: 'someString'", message);
		}
	}

	@Test
	public void testSpecFormatterShouldRenderEmptyStringForNull() throws Exception {
		NullStringModelRenderer r = new NullStringModelRenderer(SpecFormatter.provider(), null);
		String actual = r.execute(new NullStringModel(null));
		assertEquals("", actual);

	}

	@JStache(template = "{{someString}}")
	public record NullStringModel(String someString) {
	}

}
