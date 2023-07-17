package io.jstach.examples.fragment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.jstach.examples.fragment.FragmentExample.FragmentC;
import io.jstach.examples.fragment.FragmentExample.FragmentD;

public class FragmentExampleTest {

	@Test
	public void test() {
		FragmentExample e = new FragmentExample("Hello");
		String actual = FragmentExampleRenderer.of().execute(e);
		String expected = """
				<div id="a">
				A
				Hello
				</div>
				""";
		assertEquals(expected, actual);
	}

	@Test
	public void testInnerSection() {
		FragmentC c = new FragmentC("Hello");
		String actual = FragmentCRenderer.of().execute(c);
		String expected = """
				C
				Hello
				""";
		assertEquals(expected, actual);
	}

	@Test
	public void testBlockDollarSigilStyle() throws Exception {
		FragmentD c = new FragmentD("Hello");
		String actual = FragmentDRenderer.of().execute(c);
		String expected = """
				D
				Hello
				""";
		assertEquals(expected, actual);
	}

}
