package io.jstach.apt;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import io.jstach.apt.internal.LoggingSupport;
import io.jstach.apt.internal.ProcessingException;

public class FragmentTokenProcessorTest {

	@Test
	public void testRun()
			throws ProcessingException,
			IOException {
		String template = """
				<html>
				    <body>
				        <div hx-target="this">
				          {{#archive-ui}}
				---
				            {{#contact.archived}}
				            <button hx-patch="/contacts/${contact.id}/unarchive">Unarchive</button>
				            {{/contact.archived}}
				            {{^contact.archived}}
				            <button hx-delete="/contacts/${contact.id}">Archive</button>
				            {{/contact.archived}}
				---
				          {{/archive-ui}}
				        </div>
				        <h3>Contact</h3>
				        <p>${contact.email}</p>
				    </body>
				</html>""";

		FragmentTokenProcessor p = new FragmentTokenProcessor("archive-ui", LoggingSupport.testLogger());
		String actual = p.run(NamedReader.ofString(template));
		String expected = """
				---
				            {{#contact.archived}}
				            <button hx-patch="/contacts/${contact.id}/unarchive">Unarchive</button>
				            {{/contact.archived}}
				            {{^contact.archived}}
				            <button hx-delete="/contacts/${contact.id}">Archive</button>
				            {{/contact.archived}}
				---
				""";
		assertEquals(expected, actual);
		actual = p.getIndent();
		expected = "          ";
		assertEquals(expected, actual);
	}

	@Test
	public void testReindent()
			throws ProcessingException,
			IOException {
		String template = """
				<html>
				    <body>
				        <div hx-target="this">
				          {{#archive-ui}}
				          ---
				            {{#contact.archived}}
				            <button hx-patch="/contacts/${contact.id}/unarchive">Unarchive</button>
				            {{/contact.archived}}
				            {{^contact.archived}}
				            <button hx-delete="/contacts/${contact.id}">Archive</button>
				            {{/contact.archived}}
				          ---
				          {{/archive-ui}}
				        </div>
				        <h3>Contact</h3>
				        <p>${contact.email}</p>
				    </body>
				</html>""";

		FragmentTokenProcessor p = new FragmentTokenProcessor("archive-ui", LoggingSupport.testLogger());
		String actual = p.run(NamedReader.ofString(template));
		String expected = """
				---
				  {{#contact.archived}}
				  <button hx-patch="/contacts/${contact.id}/unarchive">Unarchive</button>
				  {{/contact.archived}}
				  {{^contact.archived}}
				  <button hx-delete="/contacts/${contact.id}">Archive</button>
				  {{/contact.archived}}
				---
				""";
		assertEquals(expected, actual);
		actual = p.getIndent();
		expected = "          ";
		assertEquals(expected, actual);
	}

}
