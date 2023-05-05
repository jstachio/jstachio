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
package io.jstach.apt.internal;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.context.ContextException;
import io.jstach.apt.internal.context.TemplateCompilerContext;

/**
 * @author Victor Nazarov
 */
public class ProcessingException extends Exception {

	private static final long serialVersionUID = 7396512855619812062L;

	private final Position position;

	protected ProcessingException(Position position, @Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
		this.position = position;
	}

	public ProcessingException(Position position, @Nullable String message) {
		this(position, message, null);
	}

	public ProcessingException(Position position, ContextException contextException) {
		this(position, contextException.getMessage(), contextException);
	}

	public ProcessingException(Position position, Exception contextException) {
		this(position, contextException.getClass().getName() + ": " + contextException.getMessage(), contextException);
	}

	public Position position() {
		return position;
	}

	public static class AnnotationProcessingException extends ProcessingException {

		private static final long serialVersionUID = 7475603035389163831L;

		private final AnnotatedException annotatedException;

		public AnnotationProcessingException(Position position, AnnotatedException annotedException) {
			super(position, annotedException);
			this.annotatedException = annotedException;
		}

		public AnnotatedException getAnnotatedException() {
			return annotatedException;
		}

	}

	public static class VariableProcessingException extends ProcessingException {

		private static final long serialVersionUID = -6932648568571932099L;

		public VariableProcessingException(Position position, ContextException contextException, String message) {
			super(position, message, contextException);
		}

		public static VariableProcessingException of(String name, //
				TemplateCompilerContext context, //
				Position position, //
				ContextException ex, //
				String message) {
			var templateStack = context.getTemplateStack();

			String m = message //
					+ "\n          var: \'" + name + "'" //
					+ "\n     position: \'" + position.description() + "'" //
					+ "\n     template: " + templateStack.describeTemplateStack() //
					+ "\n       reason: " + ex.getMessage() //
					+ "\ncontext stack: " + context.printStack() //
					+ "\n";

			return new VariableProcessingException(position, ex, m);
		}

	}

}
