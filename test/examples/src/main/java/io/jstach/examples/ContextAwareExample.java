package io.jstach.examples;

import java.util.Optional;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;
import io.jstach.jstache.JStacheLambda;
import io.jstach.jstachio.context.ContextNode;

@JStache(template = """
		{{@context.csrf}}
		{{#@context.user}}
		{{.}}
		{{/@context.user}}
		{{message}}
		{{id.id}}
		{{#@context}}
		{{#myLambda}}
		{{.}}
		{{/myLambda}}
		{{/@context}}
		""")
@JStacheFlags(flags = Flag.CONTEXT_SUPPORT)
record ContextAwareExample(String message, IdContainer id) {

	@JStacheLambda
	public String myLambda(ContextNode node) {
		var csrf = Optional.ofNullable(node.get("csrf")).map(n -> n.object()).map(o -> o.toString())
				.orElse("MISSING CSRF");
		return "From myLambda " + csrf;
	}
}
