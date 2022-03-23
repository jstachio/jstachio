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
package com.github.sviperll.staticmustache.text;

import java.io.IOException;

/**
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
public class Renderer implements RendererDefinition {
    private static final Renderer BLANK = new Renderer(new BlankRendererDefinition());
    public static Renderer blank() {
        return BLANK;
    }

    public static Renderer of(RendererDefinition definition) {
        if (definition instanceof Renderer)
            return (Renderer)definition;
        else
            return new Renderer(definition);
    }

    private final RendererDefinition definition;
    private Renderer(RendererDefinition definition) {
        this.definition = definition;
    }

    @Override
    public void render() throws IOException {
        definition.render();
    }

    public Renderer andThen(RendererDefinition thatDefinition) {
        if (thatDefinition instanceof Renderer) {
            Renderer renderer = (Renderer)thatDefinition;
            thatDefinition = renderer.definition;
        }
        return new Renderer(new SequencedPairRendererDefinition(this.definition, thatDefinition));
    }

    private static class BlankRendererDefinition implements RendererDefinition {
        @Override
        public void render() throws IOException {
        }
    }

    private static class SequencedPairRendererDefinition implements RendererDefinition {
        private final RendererDefinition first;
        private final RendererDefinition second;

        public SequencedPairRendererDefinition(RendererDefinition first, RendererDefinition second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public void render() throws IOException {
            first.render();
            second.render();
        }
    }
}
