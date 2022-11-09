package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ParentTest {

	@Test
	public void testParent() throws Exception {
		Parent parent = new Parent("hello 1");
		String actual = ParentRenderer.of(parent).render();
		String expected = """
				before child
				CHILD start
				GRAND CHILD start
				PARAM message from parent: Surprise hello 1!
				GRAND CHILD end
				PARAM message from parent: Surprise hello 1!
				no replace
				CHILD end
				after child""";
		assertEquals(expected, actual);
	}

	@Test
	public void testParent2() throws Exception {
		Parent2 parent2 = new Parent2("hello 2");
		String actual = Parent2Renderer.of(parent2).render();
		String expected = """
				before child
				CHILD start
				GRAND CHILD start
				PARAM message from child.mustache
				GRAND CHILD end
				CHILD local message
				no replace
				CHILD end
				after child""";
		assertEquals(expected, actual);
	}

}
