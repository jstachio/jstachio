package io.jstach.jstache;

/**
 * Tells the annotation processor what kind of code to generate namely whether to generate
 * full fledged jstachio templates (default {@link JStacheType#JSTACHIO}) or zero
 * dependency templates ({@link #STACHE}).
 * <p>
 * JStachio will guarantee to generate the following methods for this specific major
 * version (this will only change on major version changes):
 *
 * <table border="1">
 * <caption><strong>Guaranteed Generated Methods</strong></caption>
 * <tr>
 * <th>Type</th>
 * <th>Method</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>{@link JStacheType#JSTACHIO}<br/>
 * {@link JStacheType#STACHE}</td>
 * <td>{@code <T extends Model> void execute(T model, Appendable appendable)}</td>
 * <td>Executes model</td>
 * </tr>
 * <tr>
 * <td>{@link JStacheType#JSTACHIO}</td>
 * <td>{@code <T extends Model> void execute(T model, Appendable appendable, Formatter formatter, Escaper escaper)}</td>
 * <td>Executes model with supplied formatter and escaper</td>
 * </tr>
 * <tr>
 * <td>{@link JStacheType#JSTACHIO}<br/>
 * {@link JStacheType#STACHE}</td>
 * <td>{@code Class<?> modelClass()}</td>
 * <td>Return the model class (root context class annotated with JStache) that generated
 * this template.</td>
 * </tr>
 * <tr>
 * <td>{@link JStacheType#JSTACHIO}<br/>
 * {@link JStacheType#STACHE}</td>
 * <td>{@code this()}</td>
 * <td>No arg constructor that will resolve the formatter and escaper based on
 * configuration.</td>
 * </tr>
 * <tr>
 * <td>{@link JStacheType#JSTACHIO}<br/>
 * {@link JStacheType#STACHE}</td>
 * <td>{@code this(Function<@Nullable Object,String> formatter, Function<String,String> escaper)}</td>
 * <td>Constructor that uses the supplied formatter and escaper for
 * {@code execute(T model, Appendable appendable)}.</td>
 * </tr>
 * <tr>
 * <td>{@link JStacheType#JSTACHIO}</td>
 * <td>{@code this(TemplateConfig templateConfig)}</td>
 * <td>Constructor that configures a JStachio template based on configuration.</td>
 * </tr>
 * <tr>
 * <td>{@link JStacheType#JSTACHIO}<br/>
 * {@link JStacheType#STACHE}</td>
 * <td>{@code public static GENERATED_CLASS of()}</td>
 * <td>Similar to the no arg constructor but reuses a single static singleton.</td>
 * </tr>
 * </table>
 * <br/>
 * Class that are generated with type {@link JStacheType#JSTACHIO} will implement
 * {@code io.jstach.jstachio.Template} interface and thus all methods on that interface
 * (and parent interfaces) will be generated if needed (ie no default method).
 *
 * @author agentgt
 * @see JStacheConfig#type()
 */
public enum JStacheType {

	/**
	 * This effectively means not set and to let other {@link JStacheConfig} determine the
	 * setting.
	 */
	UNSPECIFIED,

	/**
	 * The default code generation which allows reflective access to templates and
	 * requires the jstachio runtime (io.jstach.jstachio).
	 */
	JSTACHIO,
	/**
	 * Zero runtime dependency renderers are generated if this is selected. Code will not
	 * have a single reference to JStachio runtime interfaces.
	 * <p>
	 * Because there is no reference to the JStachio runtime the escaper and formatter are
	 * inline implementations that passthrough the result of
	 * <code>Object.toString()</code> directly to the appendable. Just like JStachios
	 * default formatter a <code>null</code> variable will fail fast with a null pointer
	 * exception. <em>If you need different escaping or formatting you will have to
	 * provide your own implementation!</em>
	 * <p>
	 * If all templates in a project are generated this way then you can and ideally
	 * should set:
	 * <ul>
	 * <li>The <code>jstachio-annotation</code> dependency as an optional dependency (e.g.
	 * in Maven {@code <optional>true</optional>})</li>
	 * <li>as well as set in your module-info
	 * <code>requires static io.jstach.jstache</code>.</li>
	 * </ul>
	 * The above will minimize your deployed footprint and downstream dependencies will
	 * not transitively need jstachio. <strong>N.B if you go this route you will not be
	 * able to use jstachio runtime extensions.</strong>
	 *
	 *
	 * @apiNote if this is selected jstachio runtime extensions will not work for the
	 * generated renderers.
	 */
	STACHE;

}