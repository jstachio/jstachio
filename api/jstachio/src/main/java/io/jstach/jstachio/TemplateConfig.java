package io.jstach.jstachio;

import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A Container for optional template collaborators for ease of wiring generated
 * {@link Template}s.
 *
 * @apiNote the default methods return <code>null</code> to indicate to the template
 * constructor to use the static default collaborator and is the reason why
 * {@link TemplateInfo} does not extend this interface.
 * @author agentgt
 *
 */
public interface TemplateConfig {

	/**
	 * The escaper to be used on the template. See {@link Escaper#of(Function)}.
	 * @apiNote While the return signature is {@link Function} the function is often an
	 * {@link Escaper} but does not have to be.
	 * @return the escaper or <code>null</code>
	 * @see Escaper
	 */
	default @Nullable Function<String, String> escaper() {
		return null;
	}

	/**
	 * The base formatter to be used on the template. See {@link Formatter#of(Function)}.
	 * @apiNote While the return signature is {@link Function} the function is often a
	 * {@link Formatter} but does not have to be.
	 * @return the formatter or <code>null</code>
	 * @see Formatter
	 */
	@SuppressWarnings("exports")
	default @Nullable Function<@Nullable Object, String> formatter() {
		return null;
	}

}
