package io.jstach.apt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import io.jstach.apt.internal.LoggingSupport;
import io.jstach.apt.internal.ProcessingException;

public class CallSiteProcessorTest {

	LoggingSupport logging = LoggingSupport.testLogger();

	@Test
	public void testRun() throws ProcessingException, IOException {
		String template = """
				{{< parent}}
				{{$one}}{{< nested}} {{$ignore}} asdfsadf {{/ignore}} {{/nested}}{{/one}}
				{{#blah}}
				{{$two}}ignore{{/two}}
				{{/blah}}
				{{/parent}}{{blah}} foo
				some other stuff
				{{rough}}
				""";
		String partialName = "parent";
		NamedReader reader = new NamedReader(new StringReader(template), "inline", "inline");
		var processor = new PartialParameterProcessor(partialName, logging);

		processor.run(reader);

		var blocks = processor.getBlocks();
		var one = blocks.get("one");

		assertNotNull(one);

		String actual = one.content();

		assertFalse(blocks.containsKey("two"));

		String expected = "{{<nested}} {{$ignore}} asdfsadf {{/ignore}} {{/nested}}";

		assertEquals(expected, actual);

		assertEquals("{{blah}} foo\n" + "", processor.getEndContent().toString());

		assertEquals(3, processor.getEndTokens().size());

	}

}
