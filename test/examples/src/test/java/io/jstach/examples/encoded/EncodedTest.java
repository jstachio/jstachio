package io.jstach.examples.encoded;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Test;

import io.jstach.examples.encoded.Encoded.EncodedUtf8;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Template;

public class EncodedTest {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void test() throws Exception {
		EncodedUtf8 e = new EncodedUtf8("Hello", List.of("Earl", "Randy"));
		Template t = JStachio.of().findTemplate(e.getClass());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		t.write(e, os);
		String binResult = os.toString(StandardCharsets.UTF_8);
		String strResult = t.execute(e);
		String expected = """
				<body>
				Hello
				<ul>
				<li>Earl</li>
				<li>Randy</li>
				</ul>
				</body>
				""";
		assertEquals(expected, strResult);
		assertEquals(expected, binResult);

	}

}
