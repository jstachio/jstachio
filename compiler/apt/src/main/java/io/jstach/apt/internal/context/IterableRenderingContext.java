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

import java.util.Map;
import java.util.function.Predicate;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.util.Interpolator;
import io.jstach.apt.internal.util.ToStringTypeVisitor;

/**
 * @author Victor Nazarov
 */
class IterableRenderingContext implements RenderingContext {

	private final JavaExpression expression;

	private final String elementVariableName;

	private final String indexVariableName;

	private final RenderingContext parent;

	private final String iteratorVariableName;

	public IterableRenderingContext(JavaExpression expression, String elementVariableName, String indexVariableName,
			RenderingContext parent) {
		this.expression = expression;
		this.elementVariableName = elementVariableName;
		this.indexVariableName = indexVariableName;
		this.iteratorVariableName = elementVariableName + "It";
		this.parent = parent;
	}

	@Override
	public String beginSectionRenderingCode() {
		/*
		 *
		 * int i = 0; for (Iterator<String> it = list.iterator(); it.hasNext(); i++) { var
		 * _item = it.next(); boolean first = i == 0; boolean last = ! it.hasNext();
		 *
		 * System.out.println(_item + " " + i); }
		 */
		// return parent.beginSectionRenderingCode()
		// + String.format("for (%s %s: %s) { ",
		// elementExpession().type(),
		// elementVariableName,
		// expression.text());

		String loop = """

				int ${i} = 0;
				for (java.util.Iterator<? extends ${elementGeneric}> ${iteratorVar} = ${iterableVar}.iterator(); ${iteratorVar}.hasNext(); ${i}++) {
				    ${elementType} ${elementVar} = ${iteratorVar}.next();
				""";
		String elementType = ToStringTypeVisitor.toCodeSafeString(elementExpession().type());
		Map<String, String> names = Map.of( //
				"i", indexVariableName, //
				"elementGeneric", elementType, //
				"elementType", elementType, //
				"elementVar", elementVariableName, //
				"iteratorVar", iteratorVariableName, //
				"iterableVar", expression.text() //
		);
		loop = Interpolator.of().interpolate(loop, names::get);

		return parent.beginSectionRenderingCode() + loop;
	}

	@Override
	public String endSectionRenderingCode() {
		return " }" + parent.endSectionRenderingCode();
	}

	@Override
	public @Nullable JavaExpression find(String name, Predicate<RenderingContext> filter) throws ContextException {
		return switch (name) {
			case "-first", "-last", "@first", "@last", "-index", "@index" -> get(name);
			default -> RenderingContext.super.find(name, filter);
		};
	}

	@Override
	public @Nullable JavaExpression get(String name) throws ContextException {
		// https://handlebarsjs.com/api-reference/data-variables.html#root
		// https://github.com/samskivert/jmustache#-first-and--last
		return switch (name) {
			case "-first", "@first" -> first();
			case "-last", "@last" -> last();
			case "-index" -> oneBasedIndex();
			case "@index" -> zeroBasedIndex();
			default -> parent.get(name);
		};
	}

	JavaExpression first() {
		var model = expression.model();
		return model.expression("(" + indexVariableName + " == 0 )", model.knownTypes()._boolean);
	}

	JavaExpression last() {
		var model = expression.model();
		return model.expression("( ! " + iteratorVariableName + ".hasNext() )", model.knownTypes()._boolean);
	}

	JavaExpression oneBasedIndex() {
		var model = expression.model();
		return model.expression("( " + indexVariableName + " + 1 )", model.knownTypes()._int);
	}

	JavaExpression zeroBasedIndex() {
		var model = expression.model();
		return model.expression(indexVariableName, model.knownTypes()._int);
	}

	@Override
	public JavaExpression currentExpression() {
		return expression;
	}

	@Override
	public VariableContext createEnclosedVariableContext() {
		return parent.createEnclosedVariableContext();
	}

	JavaExpression elementExpession() {
		DeclaredType expressionType = (DeclaredType) expression.type();
		var model = expression.model();
		DeclaredType iterableType = model.getSupertype(expressionType, model.knownTypes()._Iterable);
		if (iterableType == null) {
			throw new IllegalStateException("expected iterable type. bug.");
		}

		/*
		 * We need to restore the annotations as getSupertype filters them out. This is an
		 * unusual case as normally jstachio does not make variables and just repeats the
		 * entire expression and thus a variable declaration is not needed.
		 *
		 * However with iterables we need a variable declaration.
		 *
		 * Normally we would do the next commented line.
		 *
		 * TypeMirror elementType = iterableType.getTypeArguments().iterator().next();
		 *
		 * but for some reason Types#directSupertypes() filters out annotations probably
		 * because of previous annotation bugs like
		 * https://bugs.openjdk.org/browse/JDK-8281238
		 *
		 * TODO this should probably be configurable
		 *
		 * For example let us assume we have the expression type of
		 *
		 * List<@Nullable String> someList;
		 *
		 * Our iterable type should be:
		 *
		 * Iterable<@Nullable String> iterable;
		 *
		 * but Types#directSupertypes will return:
		 *
		 * Iterable<String>
		 *
		 * but asMemberOf will fix it provided you pass it the type parameter element.
		 *
		 */
		var iterableElement = (TypeElement) iterableType.asElement();
		var parameterElement = iterableElement.getTypeParameters().iterator().next();

		TypeMirror elementType = model.getTypes().asMemberOf(expressionType, parameterElement);
		if (elementType instanceof @NonNull WildcardType wildcardType) {
			elementType = wildcardType.getExtendsBound();
			if (elementType == null) {
				throw new IllegalStateException("expected upper bounds. bug.");
			}
		}
		return model.expression(elementVariableName, elementType);
	}

	@Override
	public @Nullable RenderingContext getParent() {
		return parent;
	}

	@Override
	public String description() {
		return toString();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [\n\t\texpression=" + expression + ",\n\t\telementVariableName="
				+ elementVariableName + "]";
	}

}
