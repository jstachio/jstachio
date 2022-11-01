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
import java.util.ServiceLoader;

/**
 * Register escapers or filters.
 *
 * A class that is annotated represents a content type such as Html and will be used as a
 * factory for creating escapers as well as a marker for the content type.
 * <p>
 * There are two supported escaper types:
 * <ul>
 * <li>{@code io.jstach.Escaper}
 * <li>{@code java.util.function.Function<String,String>}
 * </ul>
 *
 * The Function one is desirable if you would like no reference of jstachio runtime api in
 * your code base and or just an easier interface to implement.
 * <p>
 * On the otherhand the Escaper interfaces allows potentially greater performance if you
 * need to escape native types.
 *
 * <em>n.b. the class annotated does not need to implement the interfaces</em>
 *
 * @author agentgt
 * @author Victor Nazarov
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JStacheContentType {

	/**
	 * A static method that will return an implementation of {@code io.jstach.Escaper} or
	 * {@code Function<String,String> }
	 * @return default method name is "provides" just like the {@link ServiceLoader}
	 */
	String providesMethod() default "provides";

	/**
	 * Media Type of the template to help in renderer lookup.
	 * @return media type of the template or empty string no media type
	 */
	String mediaType() default "";

	/**
	 * A content type marker to auto resolve the content type.
	 *
	 * @apiNote The provides method is purposely missing to avoid coupling with the
	 * runtime.
	 * @author agentgt
	 */
	@JStacheContentType
	public final class AutoContentType {

		private AutoContentType() {
		}

	}

}
