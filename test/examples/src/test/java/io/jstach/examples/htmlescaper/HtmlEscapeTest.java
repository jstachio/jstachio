package io.jstach.examples.htmlescaper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.jstach.jstache.JStache;

public class HtmlEscapeTest {

	@JStache(template = """
			<div alt="{{attr}}">
			{{body}}
			</div>
			""")
	public record EscapeModel(String attr, String body) {

	}

	@Test
	public void testName() throws Exception {
		var m = new EscapeModel("\"&\'<=>`", "\"&\'<=>`");
		String actual = EscapeModelRenderer.of().execute(m);
		String expected = """
				<div alt="&quot;&amp;&#x27;&lt;&#x3D;&gt;&#x60;">
				&quot;&amp;&#x27;&lt;&#x3D;&gt;&#x60;
				</div>
				""";

		assertEquals(expected, actual);
	}

}
