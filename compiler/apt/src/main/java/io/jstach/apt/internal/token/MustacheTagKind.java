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
package io.jstach.apt.internal.token;

/**
 *
 * @author Victor Nazarov
 */
public enum MustacheTagKind {

	VARIABLE, //
	UNESCAPED_VARIABLE_TWO_BRACES, UNESCAPED_VARIABLE_THREE_BRACES, PARTIAL, BEGIN_SECTION, END_SECTION, //
	BEGIN_INVERTED_SECTION, BEGIN_PARENT_SECTION, BEGIN_BLOCK_SECTION;

	public boolean isSection() {
		return switch (this) {
			case VARIABLE, UNESCAPED_VARIABLE_THREE_BRACES, UNESCAPED_VARIABLE_TWO_BRACES, PARTIAL -> false;
			default -> true;
		};
	}

	public boolean isBeginSection() {
		return switch (this) {
			case BEGIN_SECTION, BEGIN_INVERTED_SECTION, BEGIN_PARENT_SECTION, BEGIN_BLOCK_SECTION -> true;
			default -> false;
		};
	}

	public boolean isEndSection() {
		return END_SECTION == this;
	}

}
