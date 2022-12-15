package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;
import io.jstach.jstachio.JStachio;

public class BeanExampleTest {

	@JStache(template = """
			Bean {{name}} is {{#active}}active{{/active}}{{^active}}not active{{/active}}!
			""")
	@JStacheFlags(flags = Flag.DEBUG)
	public static class BeanExample {

		private String name;

		private boolean active;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

	}

	@Test
	public void testBean() throws Exception {
		BeanExample model = new BeanExample();
		model.setName("Rick");
		String actual = JStachio.render(model);
		String expected = """
				Bean Rick is not active!
								""";
		assertEquals(expected, actual);

	}

}
