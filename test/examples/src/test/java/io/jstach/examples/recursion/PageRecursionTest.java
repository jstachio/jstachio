package io.jstach.examples.recursion;

import static org.junit.Assert.*;

import org.junit.Test;

public class PageRecursionTest {

	@Test
	public void testNavigationExample() throws Exception {
		String actual = PageRenderer.of().execute(Page.ofExample());
		String expected = """
					<ol>
						<li>1 - about</li>
						<li>1 - products</li>
						<ol>
							<li>2 - widget</li>
						</ol>
					</ol>
				""";
		assertEquals(expected, actual);
	}

}
