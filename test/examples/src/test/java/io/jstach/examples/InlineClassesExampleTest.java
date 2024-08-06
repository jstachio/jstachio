package io.jstach.examples;

import org.junit.Test;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;

public class InlineClassesExampleTest {

	@Test
	public void testInlineClasses() {

		@JStacheFlags(flags = Flag.DEBUG)
		@JStache(template = "Hello {{name}}. Nice number: {{number}}")
		record MyInlineModel(String name, int number) {
		}

	}

}
