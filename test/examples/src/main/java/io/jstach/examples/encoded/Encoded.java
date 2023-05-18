package io.jstach.examples.encoded;

import java.util.List;

import io.jstach.jstache.JStache;

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
	public record EncodedUtf8(String header, List<String> messages) {
	}

}
