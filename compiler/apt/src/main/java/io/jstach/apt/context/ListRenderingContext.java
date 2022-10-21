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
package io.jstach.apt.context;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class ListRenderingContext implements RenderingContext {

	private final JavaExpression listExpression;

	private final String indexVariableName;

	private final RenderingContext parent;

	public ListRenderingContext(JavaExpression listExpression, String indexVariableName, RenderingContext parent) {
		this.listExpression = listExpression;
		this.indexVariableName = indexVariableName;
		this.parent = parent;
	}

	@Override
	public String beginSectionRenderingCode() {
		return parent.beginSectionRenderingCode()
				+ String.format("for (int %s = 0; %s < %s; %s++) { ", indexVariableName, indexExpression().text(),
						listExpression.listSize().text(), indexExpression().text());
	}

	@Override
	public String endSectionRenderingCode() {
		return "}" + parent.endSectionRenderingCode();
	}

	JavaExpression componentExpession() {
		var model = listExpression.model();
		DeclaredType dt = (DeclaredType) listExpression.type();
		var all = model.getElements().getAllMembers((TypeElement) dt.asElement());
		ExecutableElement getMethod = ElementFilter.methodsIn(all).stream()
				.filter(e -> "get".equals(e.getSimpleName().toString()) && e.getModifiers().contains(Modifier.PUBLIC)
						&& !e.getModifiers().contains(Modifier.STATIC) && e.getReturnType().getKind() != TypeKind.VOID
						&& e.getParameters().size() == 1
						&& model.isType(e.getParameters().get(0).asType(), model.knownTypes()._int))
				.findFirst().orElse(null);

		if (getMethod == null) {
			throw new IllegalStateException("List missing get? bug.");
		}
		return listExpression.methodCall(getMethod, indexExpression());
	}

	@Override
	public @Nullable JavaExpression get(String name) throws ContextException {

		return null;
	}

	@Override
	public JavaExpression currentExpression() {
		return listExpression;
	}

	@Override
	public VariableContext createEnclosedVariableContext() {
		return parent.createEnclosedVariableContext();
	}

	private JavaExpression indexExpression() {
		return listExpression.model().expression(indexVariableName, listExpression.model().knownTypes()._int);
	}

	@Override
	public @Nullable RenderingContext getParent() {
		return parent;
	}

}
