/*
 * Copyright (c) 2015, Victor Nazarov <asviraspossible@gmail.com>
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

import java.util.function.Function;

/**
 * Can be used as layout.
 * <p>
 * <tt>{@code Layout&lt;Html&gt; }</tt> is supposed to generate
 * html output.
 *
 * @param <T> marks given renderable with it's format
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
public class Layout<T> implements Layoutable<T> {
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final Layout CHROMELESS = new Layout(new ChromelessLayoutable());
    @SuppressWarnings("unchecked")
    public static <T> Layout<T> chromeless() {
        return CHROMELESS;
    }

    public static <T> Layout<T> of(Layoutable<T> layoutable) {
        if (layoutable instanceof Layout)
            return (Layout<T>)layoutable;
        else
            return new Layout<T>(layoutable);
    }

    public static <T> Layout<T> of(Function<Appendable, Renderer> header, Function<Appendable, Renderer> footer) {
        return new Layout<T>(new FunctionLayoutable<T>(header, footer));
    }

    private final Layoutable<T> layoutable;
    private Layout(Layoutable<T> layoutable) {
        this.layoutable = layoutable;
    }

    @Override
    public Renderer createHeaderRenderer(Appendable appendable) {
        return layoutable.createHeaderRenderer(appendable);
    }

    @Override
    public Renderer createFooterRenderer(Appendable appendable) {
        return layoutable.createFooterRenderer(appendable);
    }

    public Layout<T> enclose(Layoutable<T> thatLayoutable) {
        return new Layout<T>(new EnclosedLayoutable<T>(this.layoutable, thatLayoutable));
    }

    private static class FunctionLayoutable<T> implements Layoutable<T> {
        private final Function<Appendable, Renderer> header;
        private final Function<Appendable, Renderer> footer;

        FunctionLayoutable(Function<Appendable, Renderer> header, Function<Appendable, Renderer> footer) {
            this.header = header;
            this.footer = footer;
        }

        @Override
        public Renderer createHeaderRenderer(Appendable appendable) {
            var r =  header.apply(appendable);
            if (r == null) throw new NullPointerException();
            return r;
        }

        @Override
        public Renderer createFooterRenderer(Appendable appendable) {
            var r = footer.apply(appendable);
            if (r == null) throw new NullPointerException();
            return r;
        }
    }

    private static class ChromelessLayoutable<T> implements Layoutable<T> {
        @Override
        public Renderer createHeaderRenderer(Appendable appendable) {
            return Renderer.blank();
        }

        @Override
        public Renderer createFooterRenderer(Appendable appendable) {
            return Renderer.blank();
        }
    }

    private static class EnclosedLayoutable<T> implements Layoutable<T> {
        private final Layoutable<T> enclosing;
        private final Layoutable<T> enclosed;

        EnclosedLayoutable(Layoutable<T> enclosing, Layoutable<T> enclosed) {
            this.enclosing = enclosing;
            this.enclosed = enclosed;
        }

        @Override
        public Renderer createHeaderRenderer(Appendable appendable) {
            return enclosing.createHeaderRenderer(appendable).andThen(enclosed.createHeaderRenderer(appendable));
        }

        @Override
        public Renderer createFooterRenderer(Appendable appendable) {
            return enclosed.createHeaderRenderer(appendable).andThen(enclosing.createHeaderRenderer(appendable));
        }
    }
}
