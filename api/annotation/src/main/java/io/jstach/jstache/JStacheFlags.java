package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Compiler flags that are subject to change. Use at your own risk.
 * <p>
 * <strong>Flags maybe added without a major version change unlike the rest of the
 * API.</strong>
 * <p>
 * Order of flag lookup and precedence is as follows:
 * <ol>
 * <li>type annotated with JStache and this annotation.
 * <li>enclosing class (of type annotated with JStache) with this annotation with inner to
 * outer order.
 * <li>package annotated with this annotation.
 * <li>module annotated with this annotation.
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
	 */
	Flag[] flags();

	/**
	 * Compiler flags.
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
		 * Normally falsey is either empty list, boolean false, or <code>null</code>. This
		 * flag disables <code>null</code> as a falsey check.
		 */
		NO_NULL_CHECKING;

	}

}
