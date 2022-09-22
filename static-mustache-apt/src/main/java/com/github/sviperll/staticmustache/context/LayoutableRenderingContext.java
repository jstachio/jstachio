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
package com.github.sviperll.staticmustache.context;

import org.eclipse.jdt.annotation.Nullable;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class LayoutableRenderingContext implements RenderingContext {
    private final JavaExpression expression;
    private final VariableContext context;
    private final RenderingContext enclosing;

    public LayoutableRenderingContext(JavaExpression expression, VariableContext context, RenderingContext enclosing) {
        this.expression = expression;
        this.context = context;
        this.enclosing = enclosing;
    }

    @Override
    public String beginSectionRenderingCode() {
        return enclosing.beginSectionRenderingCode() + " " + expression.text() + ".createHeaderRenderer(" + context.unescapedWriter() + ").render(); ";
    }

    @Override
    public String endSectionRenderingCode() {
        return  " " + expression.text() + ".createFooterRenderer(" + context.unescapedWriter() + ").render(); " + enclosing.endSectionRenderingCode();
    }

    @Override
    public JavaExpression getDataOrDefault(String name, JavaExpression defaultValue) throws ContextException {
        return enclosing.getDataOrDefault(name, defaultValue);
    }

    @Override
    public JavaExpression currentExpression() {
        return enclosing.currentExpression();
    }

    @Override
    public VariableContext createEnclosedVariableContext() {
        return context.createEnclosedContext();
    }
    
    @Override
    public @Nullable RenderingContext getParent() {
        return null;
    }

}
