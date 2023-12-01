package io.jstach.examples.recursion;

import java.util.List;

import io.jstach.examples.recursion.Navigation.NavNode;
import io.jstach.jstache.JStache;

/*
 * JStachio does not currently support recursion.
 * However you can simulate it by doing the traversal yourself
 * and turning that traversal into a stream.
 *
 * See Navigation.
 */
@JStache(template = """
		{{#navigation}}
		{{#type}}
		{{#START}}
		{{indent}}<ol>
		{{/START}}
		{{#VALUE}}
			{{indent}}<li>{{depth}} - {{value.label}}</li>
		{{/VALUE}}
		{{#END}}
		{{indent}}</ol>
		{{/END}}
		{{/type}}
		{{/navigation}}
		""")
public record Page(Navigation navigation) {

	public static Page ofExample() {
		NavNode widget = NavNode.ofLeaf("widget");
		NavNode products = NavNode.of("products", List.of(widget));
		NavNode about = NavNode.ofLeaf("about");
		Navigation nav = Navigation.of(List.of(about, products));

		return new Page(nav);
	}
}
