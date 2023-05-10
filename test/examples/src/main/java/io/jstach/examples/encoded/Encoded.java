package io.jstach.examples.encoded;

import java.util.List;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;

public class Encoded {

	@JStache(template = """
			<body>
			{{header}}
			<ul>
			{{#messages}}
			<li>{{.}}</li>
			{{/messages}}
			</ul>
			</body>
			""")
	@JStacheFlags(flags = Flag.PRE_ENCODE)
	public record EncodedUtf8(String header, List<String> messages) {
	}

}
