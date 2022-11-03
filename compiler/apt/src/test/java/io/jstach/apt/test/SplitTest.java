package io.jstach.apt.test;

import static org.junit.Assert.*;

import java.util.ArrayDeque;
import java.util.List;

import org.junit.Test;

import io.jstach.apt.internal.CodeAppendable;

public class SplitTest {

	@Test
	public void testSplit() {
		String s = "abaata";
		List<String> actual = CodeAppendable.split(s, "a");
		// assertEquals(4,actual.size());
		assertEquals("[a, ba, a, ta]", actual.toString());
	}

	@Test
	public void testEmpty() {
		String s = "";
		List<String> actual = CodeAppendable.split(s, "a");
		// assertEquals(4,actual.size());
		assertEquals("[]", actual.toString());
	}

	@Test
	public void testOnlyDelim1() {
		String s = "\\n";
		List<String> actual = CodeAppendable.split(s, "\\n");
		// assertEquals(4,actual.size());
		assertEquals("[\\n]", actual.toString());
	}

	@Test
	public void testOnlyDelim2() {
		String s = "abcabc";
		List<String> actual = CodeAppendable.split(s, "abc");
		// assertEquals(4,actual.size());
		assertEquals("[abc, abc]", actual.toString());
	}

	@Test
	public void testOnlyDelim3() {
		String s = "\\n\\n\\n";
		List<String> actual = CodeAppendable.split(s, "\\n");
		// assertEquals(4,actual.size());
		assertEquals(List.of("\\n", "\\n", "\\n"), actual);
	}

	@Test
	public void testReal() {
		String s = "<div>\\n\\n<div>\\n";
		List<String> actual = CodeAppendable.split(s, "\\n");
		// assertEquals(4,actual.size());
		assertEquals(List.of("<div>\\n", "\\n", "<div>\\n"), actual);
	}

	@Test
	public void testReal2() {
		String s = "<div>\\n\\n<div>";
		List<String> actual = CodeAppendable.split(s, "\\n");
		// assertEquals(4,actual.size());
		assertEquals(List.of("<div>\\n", "\\n", "<div>"), actual);
	}

	@Test
	public void testReal3() {
		String s = "<div>\\n\\n<div>\\n\\t  ";
		List<String> actual = CodeAppendable.split(s, "\\n");
		// assertEquals(4,actual.size());
		assertEquals(List.of("<div>\\n", "\\n", "<div>\\n", "\\t  "), actual);
	}

	@Test
	public void testName() throws Exception {
		ArrayDeque<String> s = new ArrayDeque<>();

		s.offer("a");
		s.offer("b");
		s.offer("c");

		// s.poll();
		// s.poll();

		// s.offer("a");
		// .offer("b");

		System.out.println(s.getLast());
		for (var i : s) {
			System.out.println(i);
		}
	}

}
