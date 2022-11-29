package io.jstach.jstache;

/**
 * Tells the annotation processor what kind of code to generate namely whether to generate
 * full fledged jstachio templates (default {@link JStacheType#JSTACHIO}) or zero
 * dependency templates (StacheType{@link #STACHE}).
 * @author agentgt
 * @see JStacheConfig#type()
 */
public enum JStacheType {

	/**
	 * This effectively means not set and to let other {@link JStacheConfig} determine the
	 * setting.
	 */
	AUTO,
	/**
	 * The default code generation which allows reflective access to templates and
	 * requires the jstachio runtime (io.jstach.jstachio).
	 */
	JSTACHIO,
	/**
	 * Zero runtime dependency renderers are generated if this is selected. Code will not
	 * have a single reference to JStachio runtime interfaces.
	 * <p>
	 * If all templates in a project are generated this way then you can and ideally
	 * should set:
	 * <ul>
	 * <li>The <code>jstachio-annotation</code> dependency as an optional dependency (e.g.
	 * in Maven {@code <optional>true</optional>})</li>
	 * <li>as well as set in your module-info
	 * <code>requires transitive io.jstach.jstache</code>.</li>
	 * </ul>
	 * The above will minimize your deployed footprint and downstream dependencies will
	 * have not transitively need jstachio. <strong>N.B if you go this route you will not
	 * be able to use jstachio runtime extensions</strong>
	 *
	 *
	 * @apiNote if this is selected jstachio runtime extensions will not work for the
	 * generated renderers.
	 */
	STACHE;

}