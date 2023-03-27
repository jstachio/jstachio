package io.jstach.examples.i18n;

import io.jstach.jstache.JStache;

@JStache(template = """
		{{#name}}
		{{#i18n}}hello{{/i18n}}
		{{/name}}
		""")
public record I18NExampleModel(String name) implements MessageSupport {

}
