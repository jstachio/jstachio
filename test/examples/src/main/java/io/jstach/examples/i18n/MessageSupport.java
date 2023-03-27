package io.jstach.examples.i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import io.jstach.jstache.JStacheLambda;
import io.jstach.jstachio.escapers.Html;

public interface MessageSupport {

	default Locale locale() {
		return Locale.getDefault();
	}

	/**
	 * We use the newline to delimit i18n MessageFormat parameters. The first parameter is
	 * assumed to be the "key". If there are additional lines with non blank text they
	 * will be used as parameters. <strong> There is a special case where if no parameters
	 * other than the key are passed but the message format expects one parameter the
	 * context will be used. </strong>
	 * @param body parameters delmited by newline.
	 * @return the formatted message
	 * @throws IllegalArgumentException if there is no parameters are passed (lambda body
	 * is empty).
	 */
	@JStacheLambda
	@JStacheLambda.Raw
	default String i18n(@JStacheLambda.Raw String body, Object context) {
		List<String> parameters = Stream.of(body.split("\n")) //
				.map(s -> s.trim()) //
				.filter(s -> !s.isEmpty()) //
				.toList();
		if (parameters.isEmpty()) {
			throw new IllegalArgumentException("Missing key");
		}
		String key = parameters.get(0);

		List<String> args = parameters.stream().skip(1).toList();

		var locale = locale();

		// Should cache this
		ResourceBundle rb = ResourceBundle.getBundle("messages", locale);

		// This will throw an exception if the key is not found.
		String m = rb.getString(key);
		MessageFormat format = new MessageFormat(m, locale);
		var formats = format.getFormats();
		String unescaped;
		if (formats.length == 0) {
			unescaped = m;
		}
		else if (formats.length == 1 && args.size() == 1) {
			unescaped = format.format(args.toArray());
		}
		else if (formats.length == 1) {
			unescaped = format.format(new Object[] { context });
		}
		else {
			unescaped = format.format(args.toArray());
		}
		return Html.provider().apply(unescaped);
	}

}
