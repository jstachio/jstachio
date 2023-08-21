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
package io.jstach.apt.internal.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.context.types.KnownType;
import io.jstach.apt.internal.context.types.KnownTypes;
import io.jstach.apt.internal.context.types.NativeType;
import io.jstach.apt.internal.context.types.ObjectType;
import io.jstach.apt.internal.context.types.TypesMixin;

/**
 * @author Victor Nazarov
 */
public class JavaLanguageModel implements TypesMixin {

	private static @Nullable JavaLanguageModel INSTANCE;

	public static JavaLanguageModel createInstance(Types types, Elements elements, Messager messager) {
		KnownTypes knownTypes = KnownTypes.createInstace(elements, types);
		var self = new JavaLanguageModel(types, elements, messager, knownTypes);
		INSTANCE = self;
		return self;
	}

	public static JavaLanguageModel getInstance() {
		var i = INSTANCE;
		if (i == null) {
			throw new IllegalStateException("Java Language Model not bound yet");
		}
		return i;
	}

	private final Types operations;

	private final Elements elements;

	private final Messager messager;

	private final KnownTypes knownTypes;

	JavaLanguageModel(Types operations, Elements elements, Messager messager, KnownTypes knownTypes) {
		this.operations = operations;
		this.knownTypes = knownTypes;
		this.elements = elements;
		this.messager = messager;
	}

	public Messager getMessager() {
		return messager;
	}

	@Override
	public Types getTypes() {
		return operations;
	}

	public Elements getElements() {
		return this.elements;
	}

	public KnownTypes knownTypes() {
		return knownTypes;
	}

	boolean isUncheckedException(TypeMirror exceptionType) {
		return operations.isAssignable(exceptionType, operations.getDeclaredType(knownTypes._Error.typeElement()))
				|| operations.isAssignable(exceptionType,
						operations.getDeclaredType(knownTypes._RuntimeException.typeElement()));
	}

	JavaExpression expression(String text, NativeType type) {
		return new JavaExpression(this, text, type.typeMirror(), List.of());
	}

	JavaExpression expression(String text, TypeMirror type) {
		return new JavaExpression(this, text, type, List.of());
	}

	String eraseType(DeclaredType dt) {
		return operations.erasure(dt).toString();
	}

	TypeMirror getGenericDeclaredType(TypeElement element) {
		List<? extends TypeParameterElement> typeParameters = element.getTypeParameters();
		int numberOfParameters = typeParameters.size();
		List<TypeMirror> typeArguments = new ArrayList<>(numberOfParameters);
		for (int i = 0; i < numberOfParameters; i++) {
			typeArguments.add(operations.getWildcardType(null, null));
		}
		var filled = typeArguments.toArray(new TypeMirror[] {});
		return getDeclaredType(element, filled);
	}

	@Nullable
	DeclaredType getSupertype(DeclaredType type, ObjectType supertypeDeclaration) {
		return getSupertype(type, supertypeDeclaration.typeElement());
	}

	@Nullable
	DeclaredType getSupertype(DeclaredType type, TypeElement supertypeDeclaration) {
		if (type.asElement().equals(supertypeDeclaration))
			return type;
		else {
			List<? extends TypeMirror> supertypes = operations.directSupertypes(type);
			for (TypeMirror supertype : supertypes) {
				DeclaredType result = getSupertype((DeclaredType) supertype, supertypeDeclaration);
				if (result != null)
					return result;
			}
			return null;
		}
	}

	public Stream<DeclaredType> supers(DeclaredType type) {
		Stream<DeclaredType> self = Stream.of(type);
		Stream<DeclaredType> supers = operations.directSupertypes(type) //
				.stream() //
				.<TypeMirror>flatMap(tm -> operations.directSupertypes(tm).stream()) //
				.filter(tm -> tm instanceof DeclaredType).map(DeclaredType.class::cast);
		return Stream.concat(self, supers);
	}

	public Stream<TypeElement> supers(TypeElement type) {
		TypeMirror tm = type.asType();
		if (tm instanceof DeclaredType dt) {
			return supers(dt).map(t -> t.asElement()) //
					.filter(t -> t instanceof TypeElement) //
					.map(TypeElement.class::cast);
		}
		return Stream.empty();
	}

	TypeElement asElement(DeclaredType declaredType) {
		if (operations.asElement(declaredType) instanceof TypeElement te) {
			return te;
		}
		throw new IllegalStateException("unable to find type element for: " + declaredType);
	}

	boolean isType(TypeMirror type, KnownType knownType) {
		if (knownType instanceof NativeType nativeType) {
			return isSameType(type, nativeType.typeMirror());
		}
		if (knownType instanceof ObjectType objectType) {
			return isSubtype(type, getDeclaredType(objectType.typeElement()));
		}
		throw new IllegalStateException();

	}

	boolean isType(TypeMirror type, Optional<? extends KnownType> knownType) {
		if (!knownType.isPresent()) {
			return false;
		}
		// This bull shit is because Checker and Eclipse have different ideas
		// on what can be passed to Optional.orElse
		KnownType kt = knownType.orElseThrow();
		return isType(type, kt);
	}

	public Optional<KnownType> resolveType(TypeMirror type) throws TypeException {
		if (type instanceof WildcardType wt) {
			var eb = wt.getExtendsBound();
			if (eb == null)
				return Optional.empty();
			return resolveType(eb);
		}
		for (var nt : knownTypes.getNativeTypes()) {
			if (isType(type, nt)) {
				return Optional.of(nt);
			}
		}
		for (var ot : knownTypes.getObjectTypes()) {
			if (isType(type, ot)) {
				return Optional.of(ot);
			}
		}
		return Optional.empty();
	}

}
