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
package io.jstach.apt.context.types;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import io.jstach.RenderFunction;
import io.jstach.context.ContextNode;

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
    public final ObjectType _Optional;
    
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
    public final ObjectType _MapNode;

    public final ObjectType _UUID;
    public final ObjectType _URI;
    public final ObjectType _URL;
    
    private final List<NativeType> nativeTypes;
    private final List<ObjectType> objectTypes;
    
    public final ObjectType _Object;

    
    private KnownTypes(Elements declarations, Types types) {

        
        var b = new Builder(declarations, types);
        
        _int =  b.nativeType(TypeKind.INT, Integer.class, int.class);
        _short = b.nativeType(TypeKind.SHORT, Short.class, short.class);
        _long = b.nativeType(TypeKind.LONG, Long.class, long.class);
        _char = b.nativeType(TypeKind.CHAR, Character.class, char.class);
        _byte = b.nativeType(TypeKind.BYTE, Byte.class, byte.class);
        _float = b.nativeType(TypeKind.FLOAT, Float.class, float.class);
        _double = b.nativeType(TypeKind.DOUBLE, Double.class, double.class);
        _boolean = b.nativeType(TypeKind.BOOLEAN, Boolean.class, boolean.class);
        
        _Renderable = b.objectType(RenderFunction.class);
        _String = b.objectType(String.class);
        
        _Integer = b.objectType(Integer.class);
        _Short = b.objectType(Short.class);
        _Long = b.objectType(Long.class);
        _Character = b.objectType(Character.class);
        _Byte = b.objectType(Byte.class);
        _Float = b.objectType(Float.class);
        _Double = b.objectType(Double.class);
        _Boolean = b.objectType(Boolean.class);
        _Error = b.objectType(Error.class);
        _RuntimeException = b.objectType(RuntimeException.class);
        _Optional = b.objectType(Optional.class);
        _MapNode = b.objectType(ContextNode.class); // MapNode needs to be above _Iterable
        _Iterable = b.objectType(Iterable.class);
        _Map = b.objectType(Map.class);
        _UUID = b.objectType(UUID.class);
        _URI = b.objectType(URI.class);
        _URL = b.objectType(URL.class);
        
        this.nativeTypes = List.copyOf(b.nativeTypes);
        this.objectTypes = List.copyOf(b.objectTypes);
        
        var typeElement = Objects.requireNonNull(declarations.getTypeElement(Object.class.getName()));
        var ot = new ObjectType(typeElement, Object.class);
        
        _Object = ot;
        
    }
    
    public List<NativeType> getNativeTypes() {
        return nativeTypes;
    }
    
    public List<ObjectType> getObjectTypes() {
        return objectTypes;
    }
    
    private static class Builder  {
        private final List<NativeType> nativeTypes = new ArrayList<>();
        private final List<ObjectType> objectTypes = new ArrayList<>();
        private final Elements elements;
        private final Types types;
        
        public Builder(Elements elements, Types types) {
            super();
            this.elements = elements;
            this.types = types;
        }

        private NativeType nativeType(TypeKind kind, Class<?> boxedType, Class<?> unboxedType) {
            var typeMirror = types.getPrimitiveType(kind);
            var nt = new NativeType(typeMirror, boxedType, unboxedType);
            nativeTypes.add(nt);
            return nt;
        }
        
        private ObjectType objectType(Class<?> type) {
            var typeElement = Objects.requireNonNull(elements.getTypeElement(type.getName()));
            var ot = new ObjectType(typeElement, type);
            objectTypes.add(ot);
            return ot;
        }
    }
}
