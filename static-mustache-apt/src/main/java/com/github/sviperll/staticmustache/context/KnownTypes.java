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

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.github.sviperll.staticmustache.text.Layoutable;
import com.github.sviperll.staticmustache.text.RenderFunction;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class KnownTypes {
    static KnownTypes createInstace(Elements declarations, Types types) {
        return new KnownTypes(declarations, types);
    }

    public final TypeElement _Renderable;
    public final TypeElement _Error;
    public final TypeElement _RuntimeException;
    public final TypeMirror _int;
    public final TypeMirror _short;
    public final TypeMirror _long;
    public final TypeMirror _char;
    public final TypeMirror _byte;
    public final TypeMirror _float;
    public final TypeMirror _double;
    public final TypeMirror _boolean;
    public final TypeElement _Integer;
    public final TypeElement _Short;
    public final TypeElement _Long;
    public final TypeElement _Character;
    public final TypeElement _Byte;
    public final TypeElement _Float;
    public final TypeElement _Double;
    public final TypeElement _String;
    public final TypeElement _Boolean;
    public final TypeElement _Iterable;
    public final TypeElement _Layoutable;

    private KnownTypes(Elements declarations, Types types) {
        _Renderable = declarations.getTypeElement(RenderFunction.class.getName());
        _String = declarations.getTypeElement(String.class.getName());
        _int = types.getPrimitiveType(TypeKind.INT);
        _short = types.getPrimitiveType(TypeKind.SHORT);
        _long = types.getPrimitiveType(TypeKind.LONG);
        _char = types.getPrimitiveType(TypeKind.CHAR);
        _byte = types.getPrimitiveType(TypeKind.BYTE);
        _float = types.getPrimitiveType(TypeKind.FLOAT);
        _double = types.getPrimitiveType(TypeKind.DOUBLE);
        _boolean = types.getPrimitiveType(TypeKind.BOOLEAN);
        _Integer = declarations.getTypeElement(Integer.class.getName());
        _Short = declarations.getTypeElement(Short.class.getName());
        _Long = declarations.getTypeElement(Long.class.getName());
        _Character = declarations.getTypeElement(Character.class.getName());
        _Byte = declarations.getTypeElement(Byte.class.getName());
        _Float = declarations.getTypeElement(Float.class.getName());
        _Double = declarations.getTypeElement(Double.class.getName());
        _Boolean = declarations.getTypeElement(Boolean.class.getName());
        _Error = declarations.getTypeElement(Error.class.getName());
        _RuntimeException = declarations.getTypeElement(RuntimeException.class.getName());
        _Iterable = declarations.getTypeElement(Iterable.class.getName());
        _Layoutable = declarations.getTypeElement(Layoutable.class.getName());
    }
}
