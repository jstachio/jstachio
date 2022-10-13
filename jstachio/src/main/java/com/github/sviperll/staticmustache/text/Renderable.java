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

import com.github.sviperll.staticmustache.spi.RenderService;

/**
 * Can be rendered.
 * <p>
 * <tt>{@code Renderable&lt;Html&gt; }</tt> is supposed to generate html output.
 * 
 * @param <T>
 *                marks given renderable with it's format
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
public abstract class Renderable<T> implements RenderFunction {

    /**
     * Creates Renderer object that can be called to write out actual rendered text.
     * <p>
     * Any appendable can be used as argument: StringBuilder, Writer, OutputStream
     * 
     * @param appendable
     *                       appendable to write rendered text to
     * @return Renderer object
     */
    protected abstract RendererDefinition createRenderer(Appendable appendable);
    
    public abstract String getTemplate();
    
    public abstract Object getContext();
    
    @Override
    public final void render(Appendable a) throws IOException {
        RenderService rs = RenderService.findService();
        var rf = rs.renderer(getTemplate(), getContext(), (writer) -> {
            var r = createRenderer(writer);
            r.render();
        });
        rf.render(a);
    }
}
