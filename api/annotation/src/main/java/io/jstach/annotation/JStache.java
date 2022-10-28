/*
 * Copyright (c) 2014, Victor Nazarov <asviraspossible@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.jstach.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.jstach.annotation.JStacheContentType.AutoContentType;
import io.jstach.annotation.JStacheFormatter.AutoFormatter;

/**
 * Generates a JStachio Renderer from a template and a model (the annotated class).
 * <p>
 * Classes annotated are typically called "models" as they will be the root context for
 * the template.
 * <p>
 * The format of the templates should by default be Mustache. The syntax is informally
 * explained by the
 * <a href="https://jgonggrijp.gitlab.io/wontache/mustache.5.html">mustache manual</a> and
 * formally explained by the <a href="https://github.com/mustache/spec">spec</a>. There
 * are some subtle differences in JStachio version of Mustache due to the static nature
 * that are discussed elsewhere.
 * <p>
 * Template resolution is as follows:
 * <ol>
 * <li><code>path</code> which is a classpath with slashes following the same format as
 * the ClassLoader resources. The path maybe augmented with {@link JStachePath}.
 * <li><code>template</code> which if not empty is used as the template contents
 * <li>if the above is not set then the name of the class suffixed with ".mustache" is
 * used as the resource
 * </ol>
 * @author agentgt
 * @see JStachePath
 * @see JStacheFormatterTypes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JStache {

	/**
	 * Resource path to template
	 * @return Path to mustache template
	 * @see JStachePath
	 */
	String path() default "";

	/**
	 * @return An inline template
	 */
	String template() default "";

	/**
	 * Name of generated class.
	 * <p>
	 * adapterName can be omitted. "{{className}}Renderer" name is used by default.
	 * @return Name of generated class
	 */
	String adapterName() default ":auto";

	/**
	 * Class representing template content type to be used by escapers.
	 * <p>
	 * You can create custom escapers using {@link JStacheContentType} annotation.
	 * @return contentType of given template. If not provided it will be resolved (HTML is
	 * the default if the jstachio runtime is found).
	 */
	Class<?> contentType() default AutoContentType.class;

	/**
	 * Class providing the base formatter.
	 * <p>
	 * You can create custom formatters using {@link JStacheFormatter} annotation.
	 * @return formatter of given template. The default will be resolved (a non null that
	 * will throw NPE is the default if the jstachio runtime is found)
	 *
	 * @see JStacheFormatterTypes
	 */
	Class<?> formatter() default AutoFormatter.class;

	/**
	 * Encoding of given template file.
	 * <p>
	 * charset can be omitted. Default system charset is used by default.
	 * @return encoding of given template file
	 */
	String charset() default ":default";

}
