package io.jstach.jstachio;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheContentType;

/**
 * Template meta data like its location, formatters, escapers and or its contents.
 * <p>
 * This data is usually available on generated {@link Template}s.
 *
 * @author agentgt
 */
public interface TemplateInfo {

	/**
	 * The logical name of the template which maybe different than
	 * {@link #templatePath()}.
	 * @return logical name of template. Never null.
	 */
	public String templateName();

	/**
	 * If the template is a classpath resource file this will return the location that was
	 * originally resolved via {@linkplain JStacheConfig config resolution}.
	 * @return the location of the template or empty if the template is inlined.
	 * @apiNote since the return is the original path resolved by the annotation processor
	 * it may return a path with a starting "/" and thus it is recommend you call
	 * {@link #normalizePath()} if you plan on loading the resource.
	 *
	 * @see #normalizePath()
	 */
	public String templatePath();

	/**
	 * Normalizes the path to used by {@link ClassLoader#getResource(String)}.
	 *
	 * If the templatePath starts with a "/" it is stripped.
	 * @return normalized path
	 */
	default String normalizePath() {
		String p = templatePath();
		if (p.startsWith("/")) {
			return p.substring(1);
		}
		return p;
	}

	/**
	 * The raw contents of the template. Useful if the template is inline. To determine if
	 * the template is actually inline use {@link #templateSource()}.
	 * @apiNote An empty or blank template string maybe a valid inline template and does
	 * not mean it is not inline.
	 * @return the raw contents of the template never null.
	 * @see TemplateSource
	 */
	default String templateString() {
		return "";
	}

	/**
	 * The template content type is the class annotated with {@link JStacheContentType}
	 * which also describes the escaper to be used.
	 * @apiNote The class returned must be annotated with {@link JStacheContentType}.
	 * @return the template content type.
	 */
	Class<?> templateContentType();

	/**
	 * The template {@link Charset} which is the original format of the template file and
	 * should ideally be used when encoding an HTTP response or similar. Furthermore
	 * ideally the template charset matches the chosen {@link #templateContentType()}
	 * {@link JStacheContentType#charsets()} otherwise the escaper may not appropriately
	 * escape.
	 * <p>
	 * IF the template is inline or charset was not set this will usually be
	 * {@link StandardCharsets#UTF_8}.
	 * @return the template Charset.
	 * @see JStacheContentType#charsets()
	 * @see JStacheConfig#charset()
	 */
	String templateCharset();

	/**
	 * The escaper to be used on the template. See {@link Escaper#of(Function)}.
	 * @apiNote While the return signature is {@link Function} the function is often an
	 * {@link Escaper} but does not have to be.
	 * @return the escaper.
	 * @see Escaper
	 */
	Function<String, String> templateEscaper();

	/**
	 * The base formatter to be used on the template. See {@link Formatter#of(Function)}.
	 * @apiNote While the return signature is {@link Function} the function is often a
	 * {@link Formatter} but does not have to be.
	 * @return the formatter.
	 * @see Formatter
	 */
	@SuppressWarnings("exports")
	Function<@Nullable Object, String> templateFormatter();

	/**
	 * Checks to see if a template supports the model class.
	 * @param type the class of the model.
	 * @return if this renderer supports the class.
	 */
	public boolean supportsType(Class<?> type);

	/**
	 * Where the template contents were retrieved from.
	 * @return an enum never null.
	 */
	default TemplateSource templateSource() {
		return templatePath().isEmpty() ? TemplateSource.STRING : TemplateSource.RESOURCE;
	}

	/**
	 * Symbols representing where the template was retrieved from.
	 * @author agentgt
	 */
	public enum TemplateSource {

		/**
		 * Template was retrieved from the classpath at compile time.
		 */
		RESOURCE,
		/**
		 * Template was inlined as a String literal
		 */
		STRING

	}

	/**
	 * The last loaded time if applicable.
	 *
	 * For statically compiled templates this will always be a negative number. For
	 * dynamically loaded templates this more likely will return a non negative number
	 * indicating some hint of when the template was last modified or loaded.
	 * @return the last modified or negative if not applicable
	 */
	default long lastLoaded() {
		return -1;
	}

	/**
	 * Utility method similar to toString that describes the template meta data.
	 * @return description of the template.
	 */
	default String description() {
		return String.format("TemplateInfo[name=%s, path=%s, contentType=%s, charset=%s]", templateName(),
				templatePath(), templateContentType(), templateCharset());
	}

}
