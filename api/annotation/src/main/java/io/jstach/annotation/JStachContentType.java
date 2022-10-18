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

/**
 * Marks classes defining text format.
 * <p>
 * Each text format should be represented as a stand-alone class.
 * Each class should define pecularities specific for each text format.
 * Such classes should all be marked with TextFormat annotation.
 * <p>
 * There two requirements for marked class
 * <ul>
 *   <li>it should have no type variables
 *   <li>it should provide method with the following signature:
 * <blockquote><pre>{@code
 *     public static Appendable createEscapingAppendable(Appendable appendable)
 * }</pre></blockquote>
 * </ul>
 *
 * An implementation of createEscapingAppendable method should decorate given appendable argument
 * to create new appendable that will escape any special characters, specific to given format.
 * <p>
 * Decorated appendable should never buffer any data.
 * Escaped text should be written to original appendable immediately.
 * <p>
 * For example, HTML implementation should escape '&amp;', '&lt;' and '&gt;' characters.
 * <p>
 * <pre>{@code
 *     Appendable htmlAppendable = Html.createEscapingAppendable(System.out);
 *     htmlAppendable.append(" if a < b & b < c then a < c ");
 * }</pre>
 * <p>
 * The result when running code above should be
 * <p>
 * <pre>{@code
 *  if a &lt; b &amp; b &lt; c then a &lt; c
 * }</pre>
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JStachContentType {
    String providesMethod() default "provides";
}
