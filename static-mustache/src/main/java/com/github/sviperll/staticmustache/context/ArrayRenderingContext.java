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
package com.github.sviperll.staticmustache.context;

import javax.lang.model.type.TypeMirror;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class ArrayRenderingContext implements RenderingContext {
    private final String expression;
    private final RenderingContext parent;
    private final TypeMirror elementType;
    private final RenderingCodeGenerator types;

    public ArrayRenderingContext(TypeMirror elementType, String expression, RenderingCodeGenerator types, RenderingContext parent) {
        this.expression = expression;
        this.parent = parent;
        this.elementType = elementType;
        this.types = types;
    }

    @Override
    public String beginSectionRenderingCode() {
        return parent.beginSectionRenderingCode() + "for (int i = 0; i < " + expression + ".length; i++) { ";
    }

    @Override
    public String endSectionRenderingCode() {
        return "}" + parent.endSectionRenderingCode();
    }

    @Override
    public RenderingData getDataOrDefault(String name, RenderingData defaultValue) {
        if (name.equals("length")) {
            return new RenderingData(expression + ".length", types.intType());
        } else if (parent != null)
            return parent.getDataOrDefault(name, defaultValue);
        else
            return defaultValue;
    }

    @Override
    public RenderingData currentData() {
        return new RenderingData(expression, types.arrayType(elementType));
    }

}
