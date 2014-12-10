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
package com.github.sviperll.staticmustache.typeelementcontext;

import com.github.sviperll.staticmustache.TypeException;
import javax.lang.model.type.TypeMirror;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class ArrayFieldContext implements FieldContext {
    private final String expression;
    private final FieldContext parent;
    private final TypeMirror elementType;
    private final TypeProcessor types;

    public ArrayFieldContext(String expression, FieldContext parent, TypeMirror elementType, TypeProcessor types) {
        this.expression = expression;
        this.parent = parent;
        this.elementType = elementType;
        this.types = types;
    }

    @Override
    public String startOfBlock() {
        return parent.startOfBlock() + "if (" + expression + " != null) { for (int i = 0; i < " + expression + ".length; i++) { ";
    }

    @Override
    public String endOfBlock() {
        return "}}" + parent.endOfBlock();
    }

    @Override
    public ContextEntry getEntry(String name) throws TypeException {
        if (name.equals("length")) {
            return new ContextEntry(expression + ".length", types.intType());
        } else if (parent != null)
            return parent.getEntry(name);
        else
            throw new TypeException(name + " field not found");
    }

    @Override
    public ContextEntry thisEntry() throws TypeException {
        return new ContextEntry(expression, types.arrayType(elementType));
    }

}
