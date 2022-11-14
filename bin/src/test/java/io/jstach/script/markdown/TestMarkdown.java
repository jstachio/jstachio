package io.jstach.script.markdown;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;

import org.commonmark.internal.util.Escaping;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.CoreHtmlNodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;
import org.junit.Ignore;
import org.junit.Test;

public class TestMarkdown {

	PrintStream out = System.out;

	@Ignore
	@Test
	public void testMarkdown()
			throws Exception {
		Parser parser = Parser.builder().build();
		StringBuilder b = new StringBuilder();
		try (var r = Files.newBufferedReader(Path.of("../readme.md"))) {
			Node document = parser.parseReader(r);
			HtmlRenderer renderer = HtmlRenderer.builder().nodeRendererFactory(new HtmlNodeRendererFactory() {

				@Override
				public NodeRenderer create(
						HtmlNodeRendererContext context) {
					return new MyNodeRenderer(context);
				}
			}).build();
			b.append("<html><body>");
			b.append("<h1>jstachio</h1>");
			renderer.render(document, b);
			b.append("</body></html>");
		}
		Files.writeString(Path.of("../doc/src/main/javadoc/overview.html"), b, StandardOpenOption.WRITE);

	}

	private static class MyNodeRenderer extends CoreHtmlNodeRenderer {

		private final HtmlWriter html;

		public MyNodeRenderer(
				HtmlNodeRendererContext context) {
			super(
					context);
			html = context.getWriter();
		}

		@Override
		public void visit(
				FencedCodeBlock fencedCodeBlock) {
			String literal = fencedCodeBlock.getLiteral();
			Map<String, String> attributes = new LinkedHashMap<>();
			String info = fencedCodeBlock.getInfo();
			if (info != null && !info.isEmpty()) {
				int space = info.indexOf(" ");
				String language;
				if (space == -1) {
					language = info;
				} else {
					language = info.substring(0, space);
				}
				attributes.put("class", "language-" + language);
			}
			renderCodeBlock(literal, fencedCodeBlock, attributes);
		}

		private void renderCodeBlock(
				String literal,
				Node node,
				Map<String, String> attributes) {
			html.line();
			html.tag("pre", Map.of());
			html.tag("code", attributes);
			html.raw(Escaping.escapeHtml(literal).replace("@", "&#64;"));
			html.tag("/code");
			html.tag("/pre");
			html.line();
		}

		@Override
		public void visit(
				Heading heading) {
			String id = headerText(heading);
			id = id.replace(" ", "_");
			id = id.toLowerCase();
			id = id.trim();
			String htag = "h" + heading.getLevel();
			html.line();
			var attributes = Map.of("id", id);
			html.tag(htag, attributes);
			visitChildren(heading);
			html.tag('/' + htag);
			html.line();
		}

		static String headerText(
				Heading heading) {

			StringBuilder text = new StringBuilder();
			Node node = heading.getFirstChild();
			while (node != null) {
				if (node instanceof Text t) {
					text.append(t.getLiteral());
				}
				node = node.getNext();
			}
			return text.toString();

		}

	}
}
