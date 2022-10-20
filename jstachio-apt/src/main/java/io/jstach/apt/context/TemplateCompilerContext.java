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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import javax.lang.model.type.DeclaredType;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.AnnotatedException;
import io.jstach.apt.ProcessingException;
import io.jstach.apt.context.ContextException.FieldNotFoundContextException;
import io.jstach.apt.context.Lambda.Lambdas;

/**
 * @see RenderingCodeGenerator#createTemplateCompilerContext
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class TemplateCompilerContext {
    private final TemplateStack templateStack;
    private final Lambdas lambdas;
    private final @Nullable EnclosedRelation enclosedRelation;
    private final RenderingContext context;
    private final RenderingCodeGenerator generator;
    private final VariableContext variables;
    private final ContextType childType;

    TemplateCompilerContext(
            TemplateStack templateStack,
            Lambdas lambdas,
            RenderingCodeGenerator processor, 
            VariableContext variables, 
            RenderingContext field,
            ContextType childType) {
        this(templateStack, lambdas,
                processor, variables, field, childType, null);
    }

    private TemplateCompilerContext(
            TemplateStack templateStack, 
            Lambdas lambdas,
            RenderingCodeGenerator processor, 
            VariableContext variables, 
            RenderingContext field, 
            ContextType childType,
             @Nullable EnclosedRelation parent) {
        this.templateStack = templateStack;
        this.lambdas = lambdas;
        this.enclosedRelation = parent;
        this.context = field;
        this.generator = processor;
        this.variables = variables;
        this.childType = childType;
    }

    private String sectionBodyRenderingCode(VariableContext variables) throws ContextException {
        JavaExpression entry = context.currentExpression();
        var er = enclosedRelation;
        String path =  er != null ? er.name() : "";
        try {
            return generator.generateRenderingCode(entry, variables, path);
        } catch (TypeException ex) {
            throw new ContextException("Unable to render field", ex);
        }
    }

    public String renderingCode() throws ContextException {
        return beginSectionRenderingCode() + sectionBodyRenderingCode(variables) + endSectionRenderingCode();
    }

    public String unescapedRenderingCode() throws ContextException {
        return beginSectionRenderingCode() + sectionBodyRenderingCode(variables.unescaped()) + endSectionRenderingCode();
    }
    
    public String lambdaRenderingCode(String rawBody, String javaCode, LambdaCompiler compiler) 
            throws ContextException, IOException, AnnotatedException, ProcessingException  {
        if (context instanceof LambdaRenderingContext lc) {
            Lambda lm = lc.getLambda();
            LambdaContext ctx = new LambdaContext(lc);
            JavaExpression entry;
            try {
                entry = lm.callExpression(javaCode, ctx);
            } catch (TypeException e) {
                throw new ContextException(e.getMessage(), e);
            }
            return switch(lm.method().returnType()) {
            case STRING -> {
                //TODO use formatter for non string types
                //return generator.generateRenderingCode(entry, variables, path);
                yield variables.unescapedWriter() 
                + ".append(" 
                + entry.text()
                +");";
            }
            case MODEL -> {
                DeclaredType modelType;
                if (lm.method().methodElement().getReturnType() instanceof @NonNull DeclaredType dt) {
                    modelType = dt;
                }
                else {
                    throw new IllegalStateException("Expected declaredType");
                }
                TemplateCompilerContext context = createForLambda(lm.name(), modelType);
                String variableName = context.context.currentExpression().text();
                String variableType = "var";
                
                StringReader sr = new StringReader(rawBody);
                StringBuilder lambdaCode = new StringBuilder();
                lambdaCode.append(variableType).append(" ").append(variableName).append(" = ").append(entry.text())
                        .append(";");
                lambdaCode.append(compiler.run(context, sr));
                yield lambdaCode.toString();
            }
            };
        }
        else {
            throw new IllegalStateException("bug expected lambda context");
        }
    }
    /*
     * This dumb callback interface is so the context does not have to know all about Template Compiling
     */
    public interface LambdaCompiler {
        public String run(TemplateCompilerContext rootContext, Reader reader) throws IOException, ProcessingException;
    }

    public String beginSectionRenderingCode() {
        return  debugComment() +  context.beginSectionRenderingCode();
    }
    
    private String debugComment() {
        return "/* RenderingContext: " + context.getClass() + " */\n" + //
                "/* TypeMirror: " + context.currentExpression().type() + " */\n";

    }

    public String endSectionRenderingCode() {
        return context.endSectionRenderingCode();
    }

    public TemplateCompilerContext createForParameterPartial(String template) {
        // No enclosing relation for new partials
        return new TemplateCompilerContext(templateStack.ofPartial(template), lambdas, generator, variables, context, ContextType.PARENT_PARTIAL);
    }
    
    public TemplateCompilerContext createForPartial(String template) {
        return new TemplateCompilerContext(templateStack.ofPartial(template),lambdas, generator, variables, context, ContextType.PARTIAL);
    }
    
    TemplateCompilerContext createForLambda(String lambdaName, DeclaredType model) throws AnnotatedException {
        String modelVariableName = variables.introduceNewNameLike(lambdaName);
        var templateStack = this.templateStack.ofLambda(lambdaName);
        return generator.createTemplateCompilerContext(templateStack, model, modelVariableName, variables);
    }

    
    public TemplateCompilerContext getChild(String path, ContextType childType) throws ContextException {
        return _getChild(path, childType);
    }

    List<String> splitNames(String name) {
        return List.of(name.split("\\."));
    }

    
    public enum ContextType {
        ROOT,
        ESCAPED_VAR,
        UNESCAPED_VAR,
        SECTION, // #
        LAMBDA, // #
        INVERTED { // ^
          @Override
        public ContextType pathType() {
              return INVERTED;
        }  
        },
        PARTIAL, // >
        BLOCK, // $
        PARENT_PARTIAL, // <
        PATH;
        
        public ContextType pathType() {
            return PATH;
        }
        
        public boolean isVar() {
            if (this == ESCAPED_VAR || this == UNESCAPED_VAR) {
                return true;
            }
            return false;
        }
    }
    
    private TemplateCompilerContext createEnclosed(String name, ContextType childType, RenderingContext enclosedField) {
        if (enclosedField instanceof LambdaRenderingContext) {
            childType = ContextType.LAMBDA;
        }
        return new TemplateCompilerContext(templateStack, lambdas, generator, variables, enclosedField, childType, 
                new EnclosedRelation(name, this));
    }
    
    private TemplateCompilerContext _getChild(String name, ContextType childType) throws ContextException {
        if (name.equals(".")) {
            RenderingContext enclosedField = _getChildRender(name, childType, new OwnedRenderingContext(context));
            return createEnclosed(name, childType, enclosedField);
        }
        
        switch (childType) {
        case PARTIAL, PARENT_PARTIAL, BLOCK -> {
            RenderingContext enclosedField = _getChildRender(name, childType, new OwnedRenderingContext(context));
            return createEnclosed(name, childType, enclosedField);
        }
        default -> {
        }
        }
        
        List<String> names = splitNames(name);
        
        if (names.size() == 0) {
            throw new IllegalStateException("names");
        }
        
        RenderingContext enclosing = new OwnedRenderingContext(context);
        //System.out.println(enclosing.printStack());
        var it = names.iterator();
        /*
         * A non dotted name can be resolved against parents
         */
        var start = _getChildRender(it.next(), it.hasNext() ? childType.pathType() : childType, enclosing, false);
        enclosing = start;
        /*
         * Each part of a dotted name should resolve only against its parent.
         * direct = true
         */
        while (it.hasNext()) {
            String n = it.next();
            /*
             * Inverted dotted fields can actually not exist.
             * TODO add compiler flag on whether or not to support this
             */
            if (childType == ContextType.INVERTED) {
                try {
                    enclosing = _getChildRender(n, childType, enclosing, true);
                } catch (FieldNotFoundContextException e) {
                    enclosing = start;
                    break;
                }
            }
            else {
                enclosing = _getChildRender(n, it.hasNext() ? childType.pathType() : childType, enclosing, true);
            }
        }
        return createEnclosed(name, childType, enclosing);
    }
    
    private RenderingContext _getChildRender(String name, ContextType childType, RenderingContext enclosing) throws ContextException {
        return _getChildRender(name, childType, enclosing, false);
    }
    
    private RenderingContext _getChildRender(String name, ContextType childType, RenderingContext enclosing, boolean direct) 
            throws ContextException {
        try {
            return __getChildRender(name, childType, enclosing, direct);
        }
        catch (TypeException ex) {
            throw new ContextException(MessageFormat.format("Can''t use ''{0}'' field for rendering", name), ex);
        }
    }
        
    private RenderingContext __getChildRender(String name, ContextType childType, RenderingContext enclosing, boolean direct) 
            throws ContextException, TypeException {
        if (name.equals(".")) {
            return switch (childType) {
            case ESCAPED_VAR, UNESCAPED_VAR, PATH -> enclosing;
            case SECTION ->  generator.createRenderingContext(childType, enclosing.currentExpression(), enclosing);
            case INVERTED -> throw new ContextException("Current section can't be inverted");
            case PARENT_PARTIAL, ROOT -> throw new ContextException("Current section can't be parent");
            case PARTIAL -> throw new ContextException("Current section can't be partial");
            case LAMBDA -> throw new ContextException("Current section can't be lambda");
            case BLOCK -> throw new ContextException("Current section can't be block");
            };
        }
        /*
         * dots are allowed in partials since they are file names
         */
        if (childType == ContextType.PARENT_PARTIAL || childType == ContextType.PARTIAL) {
            return enclosing;
        }
        if (name.contains(".")) {
            throw new IllegalStateException("dotted path not allowed here");
        }
        if (childType == ContextType.BLOCK) {
            return enclosing;
        }
        JavaExpression entry = direct ? enclosing.get(name) : enclosing.find(name, c -> !( c instanceof MapRenderingContext ));
        if (entry == null && childType == ContextType.SECTION) {
            var lambda = lambdas.lambdas().get(name);
            if (lambda != null) {
                return new LambdaRenderingContext(lambda, variables, enclosing);
            }
        }
        if (entry == null & ! direct) {
            // We retry for Map like contexts
            entry = enclosing.find(name, c -> true);
        }
        if (entry == null) {
            if (getTemplateStack().isDebug()) {
                getTemplateStack().debug("Field not found." + " field: " + name +  ", template: " 
                        +  templateStack.describeTemplateStack() + " context stack: " + enclosing.printStack() + " direct: " + direct + "\n");
            }
            throw new FieldNotFoundContextException(MessageFormat.format("Field not found in current context: ''{0}'' , template: " + templateStack.describeTemplateStack(), name));
        }
        RenderingContext enclosedField;
        enclosedField = switch (childType) {
        case ESCAPED_VAR, UNESCAPED_VAR, SECTION, PATH -> generator.createRenderingContext(childType,entry, enclosing);
        case INVERTED -> new InvertedRenderingContext(generator.createInvertedRenderingContext(entry, enclosing), direct);
        case PARENT_PARTIAL, ROOT -> throw new IllegalStateException("parent not allowed here");
        case PARTIAL -> throw new IllegalStateException("partial not allowed here");
        case BLOCK -> throw new IllegalStateException("block not allowed here");
        case LAMBDA -> throw new IllegalStateException("LAMBDA not allowed here");

        };
        return enclosedField;
    }

    public boolean isEnclosed() {
        return enclosedRelation != null;
    }
    
    public Optional<EnclosedRelation> enclosed() {
        return Optional.ofNullable(enclosedRelation);
    }

    public String currentEnclosedContextName() {
        return enclosed().orElseThrow().name();
    }
    
    public TemplateCompilerContext parentContext() {
        return enclosed().orElseThrow().parentContext();
    }

    public String unescapedWriterExpression() {
        return variables.unescapedWriter();
    }
    
    public ContextType getType() {
        return childType;
    }
    
    public TemplateStack getTemplateStack() {
        return templateStack;
    }
    
    public String printStack() {
        return context.printStack();
    }
}
