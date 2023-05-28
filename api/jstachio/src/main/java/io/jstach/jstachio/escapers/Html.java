package io.jstach.jstachio.escapers;

import java.nio.charset.StandardCharsets;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheContentType;
import io.jstach.jstachio.Escaper;

/**
 * Provides a mustache spec based HTML escaper which is the default in normal mustache.
 * <p>
 * The escaper simply escapes:
 * <table border="1">
 * <caption><strong>Escape table</strong></caption>
 * <tr>
 * <th>Character</th>
 * <th>Escaped String</th>
 * </tr>
 * <tr>
 * <td>'<code>&quot;</code>'</td>
 * <td>{@value HtmlEscaper#QUOT}</td>
 * </tr>
 * <tr>
 * <td>'<code>&amp;</code>'</td>
 * <td>{@value HtmlEscaper#AMP}</td>
 * </tr>
 * <tr>
 * <td>'<code>&#x27;</code>'</td>
 * <td>{@value HtmlEscaper#APOS}</td>
 * </tr>
 * <tr>
 * <td>'<code>&lt;</code>'</td>
 * <td>{@value HtmlEscaper#LT}</td>
 * </tr>
 * <tr>
 * <td>'<code>=</code>'</td>
 * <td>{@value HtmlEscaper#EQUAL}</td>
 * </tr>
 * <tr>
 * <td>'<code>&gt;</code>'</td>
 * <td>{@value HtmlEscaper#GT}</td>
 * </tr>
 * <tr>
 * <td>'<code>&#x60;</code>'</td>
 * <td>{@value HtmlEscaper#BACK_TICK}</td>
 * </tr>
 * </table>
 * <br />
 * <em>N.B. Unlike many XML escapers this escaper does not differentiate attribute and
 * element content. Furthermore Mustache unlike many other templating languages is content
 * agnostic. If more flexibile attribute escaping is needed a custom lambda could be used
 * to preserve the whitespace in attributes. </em>
 * <p>
 * <strong>This escaper assumes UTF-8 which is the predominate encoding of HTML these days
 * and thus will not encode characters other then the ones mentioned above. </strong> Thus
 * if you intend escape for example {@link StandardCharsets#US_ASCII} a different HTML
 * escaper should be used to properly escape non ascii characters as HTML entities.
 *
 * @author agentgt
 * @author Victor Nazarov
 * @see JStacheConfig#contentType()
 */
@JStacheContentType(mediaType = "text/html", charsets = { "UTF-8" })
public final class Html {

	private Html() {
	}

	/**
	 * Provides the escaper.
	 * @return HTML escaper.
	 */
	public static Escaper provider() {
		return HtmlEscaper.Html;
	}

	/**
	 * Provides the escaper.
	 * @return HTML escaper.
	 */
	public static Escaper of() {
		return HtmlEscaper.Html;
	}

}
