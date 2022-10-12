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

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

import org.eclipse.jdt.annotation.Nullable;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class MapRenderingContext implements RenderingContext {
    protected final JavaExpression expression;
    protected final TypeElement definitionElement;
    private final RenderingContext parent;

    MapRenderingContext(JavaExpression expression, TypeElement element, RenderingContext parent) {
        this.expression = expression;
        this.definitionElement = element;
        this.parent = parent;
    }

    @Override
    public String beginSectionRenderingCode() {
        return parent.beginSectionRenderingCode();
    }

    @Override
    public String endSectionRenderingCode() {
        return parent.endSectionRenderingCode();
    }

    @Override
    public @Nullable JavaExpression get(String name) throws ContextException {
        var all = JavaLanguageModel.getInstance().getElements().getAllMembers(definitionElement);
        
        var getMethod = ElementFilter.methodsIn(all).stream()
                .filter(e -> "get".equals(e.getSimpleName().toString())
                        && e.getModifiers().contains(Modifier.PUBLIC) 
                        && ! e.getModifiers().contains(Modifier.STATIC)
                        && e.getReturnType().getKind() != TypeKind.VOID
                        && e.getParameters().size() == 1 ).findFirst().orElse(null);
        
        if (getMethod == null) {
            return null;
        }
        return expression.mapGet(getMethod, name);
    }
    
    @Override
    public JavaExpression find(String name) throws ContextException {
        //TODO this should probably check parent as well?
       var r = get(name);
       if (r == null) {
           r = parent.find(name);
       }
       return r;
    }


    @Override
    public JavaExpression currentExpression() {
        return expression;
    }

    @Override
    public VariableContext createEnclosedVariableContext() {
        return parent.createEnclosedVariableContext();
    }
    
    @Override
    public @Nullable RenderingContext getParent() {
        return this.parent;
    }
}
