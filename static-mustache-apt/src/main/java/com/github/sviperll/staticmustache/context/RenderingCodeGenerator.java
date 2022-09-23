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

import java.text.MessageFormat;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

import com.github.sviperll.staticmustache.context.TemplateCompilerContext.ChildType;
import com.github.sviperll.staticmustache.context.types.KnownType;
import com.github.sviperll.staticmustache.context.types.KnownTypes;
import com.github.sviperll.staticmustache.context.types.NativeType;
import com.github.sviperll.staticmustache.context.types.ObjectType;

/**
 * This class allows to create TemplateCompilerContext instance
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class RenderingCodeGenerator {
    /**
     * Creates instance.
     *
     * @param javaModel language model to allow java expression manipulation
     * @param formatClass type declaration denoting text format. formatClass should not contain type variables.
     * @return
     */
    public static RenderingCodeGenerator createInstance(JavaLanguageModel javaModel, TypeElement formatClass) {
        return new RenderingCodeGenerator(javaModel.knownTypes(), javaModel, formatClass);
    }

    private final KnownTypes knownTypes;
    private final JavaLanguageModel javaModel;
    private final TypeElement templateFormatElement;

    private RenderingCodeGenerator(KnownTypes types, JavaLanguageModel javaModel, TypeElement formatClass) {
        this.knownTypes = types;
        this.javaModel = javaModel;
        this.templateFormatElement = formatClass;

    }
    String generateRenderingCode(JavaExpression expression, VariableContext variables) throws TypeException {
        TypeMirror type = expression.type();
        final String text = expression.text();
        String path = expression.path();
        if (type instanceof WildcardType) {
            return generateRenderingCode(javaModel.expression(text, ((WildcardType)type).getExtendsBound()), variables);
        }
        
        
        if (javaModel.isSubtype(type, javaModel.getGenericDeclaredType(knownTypes._Renderable.typeElement()))) {
            return text + ".render(" + variables.unescapedWriter()  + "); ";
        }
        
        KnownType knownType = javaModel.resolvetype(type).orElse(null);
        
        if (knownType != null && knownType instanceof ObjectType) {
            String cname = knownType.renderClassName() + ".class";
            return renderFormatCall(variables, path, text, cname);

        }
        else if (knownType != null && knownType instanceof NativeType) {
            return "format(" + variables.writer() + ", " + "\"" + path + "\"" + ", " + text + ");"; 
        }
        else if (type instanceof DeclaredType dt) {
            String cname = javaModel.eraseType(dt)  + ".class";
            return renderFormatCall(variables, path, text, cname);
            //return variables.writer() + ".append((" + text + ").toString());";
        }
        
        throw new TypeException(MessageFormat.format("Can''t render {0} expression of {1} type", text, type));
    }
    private String renderFormatCall(VariableContext variables, String path, String text, String cname) {
        return "format(" + variables.writer() //
                + ", " + "\"" + path + "\"" //
                + ", " + cname //
                + ", " + text + ");";
    }

    /**
     * creates TemplateCompilerContext instance.
     *
     * @param element root of the data binding context. Element should not contain type-variables.
     * @param expression java expression of type corresponding to given TypeElement
     * @param variables declared variables to use in generated code
     * @return new TemplateCompilerContext
     */
    public TemplateCompilerContext createTemplateCompilerContext(TypeElement element, String expression, VariableContext variables) {
        RootRenderingContext root = new RootRenderingContext(variables);
        JavaExpression javaExpression = javaModel.expression(expression, javaModel.getDeclaredType(element));
        DeclaredTypeRenderingContext rootRenderingContext = new DeclaredTypeRenderingContext(javaExpression, element, root);
        return new TemplateCompilerContext(this, variables, rootRenderingContext);
    }

    RenderingContext createRenderingContext(ChildType childType, JavaExpression expression, RenderingContext enclosing) throws TypeException {
        if (expression.type() instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)expression.type();
            return createRenderingContext(childType, javaModel.expression(expression.text(), wildcardType.getExtendsBound()), enclosing);
        } else if (javaModel.isSubtype(expression.type(), javaModel.getGenericDeclaredType(knownTypes._Layoutable.typeElement()))) {
            if (!javaModel.isSubtype(expression.type(), javaModel.getDeclaredType(knownTypes._Layoutable.typeElement(), javaModel.getDeclaredType(templateFormatElement)))) {
                throw new TypeException(MessageFormat.format("Can''t render {0} expression of {1} type: expression is Layoutable, but wrong format", expression.text(), expression.type()));
            } else {
                VariableContext context = enclosing.createEnclosedVariableContext();
                return new LayoutableRenderingContext(expression, context, enclosing);
            }
        } else if (javaModel.isType(expression.type(), knownTypes._boolean)) {
            return new BooleanRenderingContext(expression.text(), enclosing);
        } else if (javaModel.isType(expression.type(), knownTypes._Boolean)) {
            RenderingContext nullableContext = nullableRenderingContext(expression, enclosing);
            BooleanRenderingContext booleanContext = new BooleanRenderingContext(expression.text(), nullableContext);
            return booleanContext;
        } else if (javaModel.isType(expression.type(), knownTypes._Iterable)) {
            RenderingContext nullable = nullableRenderingContext(expression, enclosing);
            VariableContext variableContext = nullable.createEnclosedVariableContext();
            String elementVariableName = variableContext.introduceNewNameLike("element");
            RenderingContext variables = new VariablesRenderingContext(variableContext, nullable);
            IterableRenderingContext iterable = new IterableRenderingContext(expression, elementVariableName, variables);
            return createRenderingContext(childType, iterable.elementExpession(), iterable);
        } else if (javaModel.isType(expression.type(), knownTypes._Map)) {
            RenderingContext nullable = nullableRenderingContext(expression, enclosing);
            DeclaredType mapType = (DeclaredType) expression.type();
            MapRenderingContext map = new MapRenderingContext(expression, javaModel.asElement(mapType), nullable);
            return map;
            
        } else if (expression.type().getKind() == TypeKind.ARRAY) {
            RenderingContext nullable = nullableRenderingContext(expression, enclosing);
            VariableContext variableContext = nullable.createEnclosedVariableContext();
            String indexVariableName = variableContext.introduceNewNameLike("i");
            RenderingContext variables = new VariablesRenderingContext(variableContext, nullable);
            ArrayRenderingContext array = new ArrayRenderingContext(expression, indexVariableName, variables);
            return createRenderingContext(childType,array.componentExpession(), array);
        } else if (expression.type().getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType)expression.type();
            RenderingContext ctx = switch (childType) {
            case ESCAPED_VAR, UNESCAPED_VAR -> enclosing;
            case PATH, INVERTED, SECTION -> nullableRenderingContext(expression, enclosing);
            };
            DeclaredTypeRenderingContext declaredContext = new DeclaredTypeRenderingContext(expression, javaModel.asElement(declaredType), ctx);
            return declaredContext;
        } else {
            return new NoDataContext(expression, enclosing);
        }
    }

    RenderingContext createInvertedRenderingContext(JavaExpression expression, RenderingContext enclosing) throws TypeException {
        if (expression.type() instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)expression.type();
            return createRenderingContext(ChildType.INVERTED, javaModel.expression(expression.text(), wildcardType.getExtendsBound()), enclosing);
        } else if (javaModel.isType(expression.type(), knownTypes._boolean)) {
            return new BooleanRenderingContext("!(" + expression.text() + ")", enclosing);
        } else if (javaModel.isType(expression.type(), knownTypes._Boolean)) {
            return new BooleanRenderingContext("(" + expression.text() + ") == null || !(" + expression.text() + ")", enclosing);
        } else if (expression.type() instanceof DeclaredType dt) {
            DeclaredTypeRenderingContext declaredContext = new DeclaredTypeRenderingContext(expression, javaModel.asElement(dt), enclosing);
            return new BooleanRenderingContext("(" + expression.text() + ") == null", declaredContext);
        } else if (expression.type() instanceof ArrayType) {
            return new BooleanRenderingContext("(" + expression.text() + ") == null || (" + expression.text() + ").length == 0", enclosing);
        } else
            throw new TypeException(MessageFormat.format("Can''t invert {0} expression of {1} type",
                                                         expression.text(),
                                                         expression.type()));
    }

    private RenderingContext nullableRenderingContext(JavaExpression expression, RenderingContext context) {
        return new BooleanRenderingContext(expression.text() + " != null", context);
    }
}
