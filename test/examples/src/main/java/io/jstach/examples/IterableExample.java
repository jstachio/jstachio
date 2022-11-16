package io.jstach.examples;

import java.util.List;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;

/**
 * Iterable example
 * @author agent
 *
 */
@JStache(template = """
		{{#items}}
		{{^flag}}hello{{/flag}}
		{{^-first}}-------{{/-first}}
		{{#-first}}I am first{{/-first}}{{#-last}}I am last{{/-last}} {{.}}
		    My one based index is {{-index}}
		    My zero based index is {{@index}}
		{{/items}}
		{{^items}}
		no show
		{{/items}}
		""")
@JStacheFlags(flags = Flag.DEBUG)
public record IterableExample(List<String> items, boolean flag) {

}
