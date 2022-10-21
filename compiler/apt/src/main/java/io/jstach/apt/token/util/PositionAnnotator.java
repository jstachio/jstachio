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
package io.jstach.apt.token.util;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.Position;
import io.jstach.apt.PositionedToken;
import io.jstach.apt.ProcessingException;
import io.jstach.apt.TokenProcessor;

/**
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class PositionAnnotator implements TokenProcessor<@Nullable Character> {

	private final String fileName;

	private final TokenProcessor<PositionedToken<@Nullable Character>> processor;

	private int row = 1;

	private StringBuilder currentLine = new StringBuilder();

	public PositionAnnotator(String fileName, TokenProcessor<PositionedToken<@Nullable Character>> processor) {
		this.fileName = fileName;
		this.processor = processor;
	}

	@Override
	public void processToken(@Nullable Character token) throws ProcessingException {
		if (token != null && token != '\n') {
			currentLine.append(token.charValue());
		}
		else {
			String line = currentLine.toString();
			char[] chars = line.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				processor.processToken(
						new PositionedToken<@Nullable Character>(new Position(fileName, row, line, i + 1), chars[i]));
			}
			processor.processToken(new PositionedToken<@Nullable Character>(
					new Position(fileName, row, line, chars.length + 1), token));
			currentLine = new StringBuilder();
			row++;
		}
	}

}
