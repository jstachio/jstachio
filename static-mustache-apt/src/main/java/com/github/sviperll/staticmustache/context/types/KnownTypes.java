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
package com.github.sviperll.staticmustache.context.types;

import java.util.List;
import java.util.Map;

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
public class KnownTypes {
    public static KnownTypes createInstace(Elements declarations, Types types) {
        return new KnownTypes(declarations, types);
    }
    
    public final NativeType _int;
    public final NativeType _short;
    public final NativeType _long;
    public final NativeType _char;
    public final NativeType _byte;
    public final NativeType _float;
    public final NativeType _double;
    public final NativeType _boolean;

    public final ObjectType _Renderable;
    public final ObjectType _Error;
    public final ObjectType _RuntimeException;
    
    public final ObjectType _Integer;
    public final ObjectType _Short;
    public final ObjectType _Long;
    public final ObjectType _Character;
    public final ObjectType _Byte;
    public final ObjectType _Float;
    public final ObjectType _Double;
    public final ObjectType _String;
    public final ObjectType _Boolean;
    public final ObjectType _Iterable;
    public final ObjectType _Map;
    public final ObjectType _Layoutable;
    
    private final List<NativeType> nativeTypes;
    private final List<ObjectType> objectTypes;

    private KnownTypes(Elements declarations, Types types) {

        
        _int =  nativeType(types.getPrimitiveType(TypeKind.INT), Integer.class, int.class);
        _short = nativeType(types.getPrimitiveType(TypeKind.SHORT), Short.class, short.class);
        _long = nativeType(types.getPrimitiveType(TypeKind.LONG), Long.class, long.class);
        _char = nativeType(types.getPrimitiveType(TypeKind.CHAR), Character.class, char.class);
        _byte = nativeType(types.getPrimitiveType(TypeKind.BYTE), Byte.class, byte.class);
        _float = nativeType(types.getPrimitiveType(TypeKind.FLOAT), Float.class, float.class);
        _double = nativeType(types.getPrimitiveType(TypeKind.DOUBLE), Double.class, double.class);
        _boolean = nativeType(types.getPrimitiveType(TypeKind.BOOLEAN), Boolean.class, boolean.class);
        
        _Renderable = objectType(declarations,RenderFunction.class);
        _String = objectType(declarations,String.class);
        
        _Integer = objectType(declarations,Integer.class);
        _Short = objectType(declarations,Short.class);
        _Long = objectType(declarations,Long.class);
        _Character = objectType(declarations,Character.class);
        _Byte = objectType(declarations,Byte.class);
        _Float = objectType(declarations,Float.class);
        _Double = objectType(declarations,Double.class);
        _Boolean = objectType(declarations,Boolean.class);
        _Error = objectType(declarations,Error.class);
        _RuntimeException = objectType(declarations,RuntimeException.class);
        _Iterable = objectType(declarations,Iterable.class);
        _Map = objectType(declarations, Map.class);
        _Layoutable = objectType(declarations,Layoutable.class);
        
        List<NativeType> nativeTypes = List.of(_int, _short, _long, _char, _byte, _float, _double, _boolean);
        List<ObjectType> objectTypes = List.of(_Renderable, _String, _Integer, _Short, _Long, _Character, _Byte, _Float,
                _Double, _Boolean, _Error, _RuntimeException, _Iterable, _Layoutable);
        this.nativeTypes = nativeTypes;
        this.objectTypes = objectTypes;
        
    }
    
    
    public List<NativeType> getNativeTypes() {
        return nativeTypes;
    }
    
    public List<ObjectType> getObjectTypes() {
        return objectTypes;
    }
    
    private NativeType nativeType(TypeMirror typeMirror, Class<?> boxedType, Class<?> unboxedType) {
        return new NativeType(typeMirror, boxedType, unboxedType);
    }
    
    private ObjectType objectType(Elements declarations, Class<?> type) {
        var typeElement = declarations.getTypeElement(type.getName());
        return new ObjectType(typeElement, type);
    }
}
