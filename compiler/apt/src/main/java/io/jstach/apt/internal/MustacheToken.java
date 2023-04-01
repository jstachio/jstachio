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
 *     text materials provided with the distribution.
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

import java.io.IOException;
import java.io.UncheckedIOException;

import io.jstach.apt.internal.token.MustacheTagKind;

/**
 * @author Victor Nazarov
 * @author agentgt
 */
public sealed interface MustacheToken {

	public record TagToken(MustacheTagKind tagKind, String name) implements MustacheToken {
		@Override
		public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
			return switch (tagKind()) {
				case BEGIN_SECTION -> visitor.beginSection(name);
				case BEGIN_BLOCK_SECTION -> visitor.beginBlockSection(name);
				case BEGIN_INVERTED_SECTION -> visitor.beginInvertedSection(name);
				case BEGIN_PARENT_SECTION -> visitor.beginParentSection(name);
				case END_SECTION -> visitor.endSection(name);
				case UNESCAPED_VARIABLE_THREE_BRACES -> visitor.unescapedVariable(name);
				case UNESCAPED_VARIABLE_TWO_BRACES -> visitor.unescapedVariable(name);
				case VARIABLE -> visitor.variable(name);
				case PARTIAL -> visitor.partial(name);

			};
		}

		public boolean isTagToken() {
			return true;
		}

		public boolean isSectionToken() {
			return tagKind().isSection();
		}

		public boolean isSectionEndToken(String name) {
			return tagKind().isEndSection() && name().equals(name);
		}

		public boolean isStandaloneToken() {
			return isSectionToken() || tagKind == MustacheTagKind.PARTIAL;
		}

		public boolean isIndented() {
			return tagKind == MustacheTagKind.PARTIAL || tagKind == MustacheTagKind.BEGIN_PARENT_SECTION;
		}

		public void appendRawText(Appendable a) throws IOException {
			// TODO hmm probably need to fix this for change delimeter support
			switch (tagKind()) {
				case BEGIN_SECTION -> a.append("{{").append("#").append(name).append("}}");
				case BEGIN_BLOCK_SECTION -> a.append("{{").append("$").append(name).append("}}");
				case BEGIN_INVERTED_SECTION -> a.append("{{").append("^").append(name).append("}}");
				case BEGIN_PARENT_SECTION -> a.append("{{").append("<").append(name).append("}}");
				case END_SECTION -> a.append("{{").append("/").append(name).append("}}");
				case UNESCAPED_VARIABLE_THREE_BRACES -> a.append("{{{").append(name).append("}}}");
				case UNESCAPED_VARIABLE_TWO_BRACES -> a.append("{{&").append(name).append("}}");
				case VARIABLE -> a.append("{{").append(name).append("}}");
				case PARTIAL -> a.append("{{").append(">").append(name).append("}}");
			}
			;
		}
	}

	public record TextToken(String text) implements MustacheToken {
		@Override
		public <R, E extends Exception> R accept(MustacheToken.Visitor<R, E> visitor) throws E {
			return visitor.text(text);
		}

		public void appendRawText(Appendable a) throws IOException {
			a.append(text);
		}
	}

	public record SpecialCharacterToken(SpecialChar specialChar) implements MustacheToken {
		@Override
		public <R, E extends Exception> R accept(MustacheToken.Visitor<R, E> visitor) throws E {
			return visitor.specialCharacter(specialChar);
		}

		public void appendRawText(Appendable a) throws IOException {
			a.append(specialChar.character());
		}

		public void appendEscapedJava(StringBuilder sb) {
			sb.append(specialChar.javaEscaped());
		}
	}

	public record NewlineToken(NewlineChar newlineChar) implements MustacheToken {
		@Override
		public <R, E extends Exception> R accept(MustacheToken.Visitor<R, E> visitor) throws E {
			return visitor.newline(newlineChar);
		}

		@Override
		public boolean isNewlineToken() {
			return true;
		}

		public void appendRawText(Appendable a) throws IOException {
			a.append(newlineChar.characters());
		}

		public void appendEscapedJava(StringBuilder sb) {
			sb.append(newlineChar.javaEscaped());
		}
	}

	public record EndOfFileToken() implements MustacheToken {

		@Override
		public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
			return visitor.endOfFile();
		}

		public void appendRawText(Appendable a) throws IOException {
		}

	}

	public enum NewlineChar {

		LF("\n"), CRLF("\r\n");

		private final String characters;

		private NewlineChar(String characters) {
			this.characters = characters;
		}

		public String characters() {
			return characters;
		}

		public String javaEscaped() {
			return switch (this) {
				case LF -> "\\n";
				case CRLF -> "\\r\\n";
			};
		}

	}

	public enum SpecialChar {

		QUOTATION_MARK('"'), // "
		BACKSLASH('\\'); // \

		private final char character;

		private SpecialChar(char character) {
			this.character = character;
		}

		public char character() {
			return character;
		}

		public String javaEscaped() {
			return switch (this) {
				case QUOTATION_MARK -> "\\\"";
				case BACKSLASH -> "\\\\";
			};
		}

	}

	/**
	 * N.B this does not include newline!
	 */
	default boolean isWhitespaceToken() {
		return isWhitespaceToken(this);
	}

	default boolean isTagToken() {
		return false;
	}

	default boolean isSectionToken() {
		return false;
	}

	default boolean isSectionEndToken(String name) {
		return false;
	}

	default boolean isStandaloneToken() {
		return false;
	}

	default boolean isNewlineToken() {
		return false;
	}

	default boolean isNewlineOrEOF() {
		return isNewlineToken() || isEOF();
	}

	default boolean isEOF() {
		return this instanceof EndOfFileToken;
	}

	default boolean isIndented() {
		return false;
	}

	public void appendRawText(Appendable a) throws IOException;

	default void appendRawText(StringBuilder sb) {
		try {
			appendRawText((Appendable) sb);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	default void appendEscapedJava(StringBuilder sb) {
		appendRawText(sb);
	}

	/**
	 * N.B this does not include newline!
	 */
	public static boolean isWhitespaceToken(MustacheToken token) {
		if (token instanceof TextToken tt) {
			return tt.text().isBlank();
		}
		return false;
	}

	public abstract <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E;

	public interface Visitor<R, E extends Exception> {

		R beginSection(String name) throws E;

		R beginInvertedSection(String name) throws E;

		R beginParentSection(String name) throws E;

		R beginBlockSection(String name) throws E;

		R endSection(String name) throws E;

		R partial(String name) throws E;

		R variable(String name) throws E;

		R unescapedVariable(String name) throws E;

		R specialCharacter(SpecialChar specialChar) throws E;

		R newline(NewlineChar c) throws E;

		R text(String s) throws E;

		R endOfFile() throws E;

	}

}
