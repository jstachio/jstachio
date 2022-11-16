package io.jstach.jstachio.escapers;

import io.jstach.jstache.JStacheContentType;
import io.jstach.jstachio.Escaper;

/**
 * Provides a mustache spec based HTML escaper which is the default in normal mustache.
 * <p>
 * The escaper simply escapes:
 * <ul>
 * <li>'&quot;'
 * <li>'&gt;'
 * <li>'&lt;'
 * <li>'&amp;'
 * </ul>
 *
 * <em>N.B. Unlike many XML escapers this escaper does not differentiate attribute and
 * element content. If that is needed a custom lambda could be used to preserve the
 * whitespace in attributes. </em>
 *
 * @author Victor Nazarov
 * @author agentgt
 */
@JStacheContentType(mediaType = "text/html")
public enum Html {

	;
	/**
	 * Provides the escaper.
	 * @return HTML escaper.
	 */
	public static Escaper provides() {
		return new HtmlEscaper();
	}

}
