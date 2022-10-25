package io.jstach.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Compiler flags that are subject to change. Use at your own risk.
 *
 * Flags maybe added without a major version change unlike the rest of the API.
 *
 * @author agentgt
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
public @interface JStacheFlags {

	Flag[] flags();

	public enum Flag {

		/**
		 * This will produce additional logging that is sent to standard out.
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
		NO_INVERTED_BROKEN_CHAIN

	}

}
