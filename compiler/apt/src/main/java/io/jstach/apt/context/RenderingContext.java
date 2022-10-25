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
package io.jstach.apt.context;

import java.util.function.Predicate;

import org.eclipse.jdt.annotation.Nullable;

interface RenderingContext {

	default String beginSectionRenderingCode() {
		var p = getParent();
		if (p != null) {
			return p.beginSectionRenderingCode();
		}
		return "";
	}

	default String endSectionRenderingCode() {
		var p = getParent();
		if (p != null) {
			return p.endSectionRenderingCode();
		}
		return "";
	}

	/**
	 * Gets the method (or field) directly in this context. This is for dotted names as
	 * they cannot look up the context stack.
	 * @param name
	 * @return
	 * @throws ContextException
	 */
	@Nullable
	JavaExpression get(String name) throws ContextException;

	/**
	 * This should be mostly equivalent to {{.}}
	 * @return
	 */
	default JavaExpression get() {
		return currentExpression();
	}

	/**
	 * Looks for a method or or field up the context stack starting in the current context
	 * first and then delgating to the parent.
	 * @param name
	 * @return
	 * @throws ContextException
	 */
	// @Nullable JavaExpression find(String name) throws ContextException;

	default @Nullable JavaExpression find(String name, Predicate<RenderingContext> filter) throws ContextException {

		JavaExpression result = null;
		// TODO figure out why the below breaks for lambda
		// if (filter.test(this)) {
		// result = get(name);
		// }
		// if (result != null) {
		// return result;
		// }
		var p = getParent();
		if (p != null) {
			result = p.find(name, filter);
		}
		return result;
	}

	JavaExpression currentExpression();

	VariableContext createEnclosedVariableContext();

	@Nullable
	RenderingContext getParent();

	default String printStack() {
		StringBuilder sb = new StringBuilder();
		@Nullable
		RenderingContext parent = this;
		while (parent != null) {
			sb.append("\n\t<- ");
			sb.append(parent.description());
			parent = parent.getParent();
		}

		return sb.toString();
	}

	default String description() {
		return getClass().getName();
	}

}
