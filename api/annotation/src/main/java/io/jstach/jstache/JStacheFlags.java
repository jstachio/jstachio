package io.jstach.jstache;

import java.io.OutputStream;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Compiler feature flags that are subject to change. Use at your own risk!
 * <p>
 * <strong>Flags maybe added without a major version change unlike the rest of the
 * API.</strong> If a flag becomes popular enough it will eventually make its way to
 * {@link JStacheConfig} so please file an issue if you depend on flag and would like it
 * to remain in the library.
 * <p>
 * Order of flag lookup and precedence is as follows:
 * <ol>
 * <li>type annotated with JStache and this annotation.
 * <li>enclosing class (of type annotated with JStache) with this annotation with inner to
 * outer order.
 * <li>package annotated with this annotation.
 * <li>module annotated with this annotation.
 * <li>annotation processor compiler arg options (<code>-A</code>). The flags are
 * lowercased and prefixed with "<code>jstache.</code>"</li>
 * </ol>
 * <em>The flags are NOT combined but rather the first found dictates the flags set or not
 * (including empty)</em>
 *
 * @author agentgt
 * @apiNote the retention policy is purposely {@link RetentionPolicy#SOURCE} as these
 * flags only impact compiling of the template.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.MODULE, ElementType.PACKAGE, ElementType.TYPE })
@Documented
public @interface JStacheFlags {

	/**
	 * Compiler flags that will be used on for this model.
	 * @return flags
	 * @see JStacheFlags
	 */
	Flag[] flags();

	/**
	 * Compiler flags. Besides setting with {@link JStacheFlags} the flags are also
	 * available as annotation processor options but are prefixed with
	 * "<code>jstache.</code>" and lowercased
	 * <p>
	 * For example {@link Flag#DEBUG} would be: <code>-Ajstache.debug=true/false</code>
	 *
	 * @apiNote SUBJECT TO CHANGE!
	 * @author agentgt
	 *
	 */
	public enum Flag {

		/**
		 * This will produce additional logging that is sent to standard out while the
		 * annotation processor runs (not during runtime).
		 */
		DEBUG,
		/**
		 * Per mustache spec dotted names can actually not exist at all for inverted
		 * sections. This flag disables that so that a compiler failure will happen if the
		 * fields are missing.
		 *
		 * For example assume "missing" is not present on "data" as in data has no field
		 * or method called "missing".
		 *
		 * <pre>
		 * {{^data.missing}}
		 * {{/data.missing}}
		 * </pre>
		 *
		 * Normally the above will compile just fine per the spec but this can lead to
		 * bugs. To not allow what the spec calls "dotted broken chains" you can use this
		 * flag.
		 */
		NO_INVERTED_BROKEN_CHAIN,

		/**
		 * <strong>EXPERIMENTAL:</strong> Normally falsey is either empty list, boolean
		 * false, or <code>null</code>. This flag disables <code>null</code> as a falsey
		 * check.
		 *
		 * For example when opening a section like: <pre><code class="language-hbs">
		 * {{#myNonNull}}
		 * Hi!
		 * {{/myNonNull}}
		 * </code> </pre>
		 *
		 * JStachio would produce code that checks if <code>myNonNull</code> is null as
		 * well as iterate if it is a list or check if true if it is a boolean.
		 *
		 * <p>
		 * However null checking will still be done if JStachio can find a
		 * {@link ElementType#TYPE_USE} annotation with the {@link Class#getSimpleName()}
		 * of <code>Nullable</code> on the type that is being accessed as a section. This
		 * follows <a href="https://github.com/jspecify/jspecify">JSpecify rules</a> but
		 * not other nullable annotations like
		 * <a href="https://spotbugs.github.io/">SpotBugs</a> that are not
		 * {@link ElementType#TYPE_USE}.
		 *
		 * <h4>Benefits</h4>
		 *
		 * The advantages of disabling null checking are:
		 * <ul>
		 * <li>Failing fast instead of just not rendering something which may make finding
		 * bugs easier.</li>
		 * <li>Less generated code which maybe easier to read</li>
		 * <li>Avoid warnings of superfluous null checking by static analysis tools</li>
		 * <li>Possible slight improvement of performance</li>
		 * </ul>
		 *
		 * <h4>Caveats</h4>
		 *
		 * <h5>JDK Bug</h5> Because of JDK bug:
		 * <a href="https://bugs.openjdk.org/browse/JDK-8225377">JDK-8225377</a> this
		 * <em>nullable detection will only work if the type that is being checked is
		 * currently within the same compile boundary as the JStache model being
		 * analyzed!</em>
		 *
		 * <h5>Manually checking for null</h5> If JStachio cannot detect that the type is
		 * nullable because it is not annotated or because of the aforementioned JDK bug
		 * then it will conclude that it can never be null and thus you will be unable to
		 * use section like conditions to check if is null. One workaround is to use a
		 * custom {@link JStacheLambda} to check for null.
		 *
		 * @apiNote This is currently experimental and a flag because of the JDK bug. In
		 * the future more comprehensive support will be put in {@link JStacheConfig}.
		 */
		NO_NULL_CHECKING,

		/**
		 * If set the templates will have pre-encoded bytes of the static parts of the
		 * template and the generated {@link JStacheType#JSTACHIO} code will implement
		 * <code>io.jstach.jstachio.Template.EncodedTemplate</code> which will allow
		 * writing binary to an {@link OutputStream}.
		 */
		PRE_ENCODE;

	}

}
