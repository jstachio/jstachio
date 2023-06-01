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
package io.jstach.apt.internal.token.util;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.Position;
import io.jstach.apt.internal.PositionedToken;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.TokenProcessor;

/**
 * @author Victor Nazarov
 * @param <T>
 */
public class PositionHodingTokenProcessor<T> implements TokenProcessor<T> {

	private final TokenProcessor<PositionedToken<T>> downstream;

	private @Nullable Position position = null;

	public PositionHodingTokenProcessor(TokenProcessor<PositionedToken<T>> downstream) {
		this.downstream = downstream;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	@Override
	public void processToken(T transformedToken) throws ProcessingException {
		var position = this.position;
		if (position == null) {
			throw new IllegalStateException("position was not set");
		}
		downstream.processToken(new PositionedToken<>(position, transformedToken));
	}

}
