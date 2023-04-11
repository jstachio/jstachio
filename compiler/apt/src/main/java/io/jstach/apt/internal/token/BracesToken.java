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
 *     character materials provided with the distribution.
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
 * @author Victor Nazarov
 */
public sealed interface BracesToken {

	public enum TokenType {

		TWO_OPEN, TWO_CLOSE, THREE_OPEN, THREE_CLOSE, CHARACTER, EOF

	}

	public record BToken(TokenType type) implements BracesToken {
		@Override
		public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
			return switch (type()) {
				case TWO_OPEN -> visitor.twoOpenBraces();
				case TWO_CLOSE -> visitor.twoClosingBraces();
				case THREE_OPEN -> visitor.threeOpenBraces();
				case THREE_CLOSE -> visitor.threeClosingBraces();
				case CHARACTER -> throw new UnsupportedOperationException("Unimplemented case: " + type());
				case EOF -> throw new UnsupportedOperationException("Unimplemented case: " + type());
			};
		}
	}

	public record CToken(char character) implements BracesToken {
		public <R, E extends Exception> R accept(BracesToken.Visitor<R, E> visitor) throws E {
			return visitor.character(character);
		}
	}

	public record EOFToken() implements BracesToken {
		public <R, E extends Exception> R accept(BracesToken.Visitor<R, E> visitor) throws E {
			return visitor.endOfFile();
		};
	}

	public static BracesToken twoOpenBraces() {
		return new BToken(TokenType.TWO_OPEN);
	}

	public static BracesToken twoClosingBraces() {
		return new BToken(TokenType.TWO_CLOSE);
	}

	public static BracesToken threeOpenBraces() {
		return new BToken(TokenType.THREE_OPEN);
	}

	public static BracesToken threeClosingBraces() {
		return new BToken(TokenType.THREE_CLOSE);
	}

	public static BracesToken character(final char s) {
		return new CToken(s);
	}

	public static BracesToken endOfFile() {
		return new EOFToken();
	}

	public abstract <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E;

	public interface Visitor<R, E extends Exception> {

		R twoOpenBraces() throws E;

		R twoClosingBraces() throws E;

		R threeOpenBraces() throws E;

		R threeClosingBraces() throws E;

		R character(char c) throws E;

		R endOfFile() throws E;

	}

}
