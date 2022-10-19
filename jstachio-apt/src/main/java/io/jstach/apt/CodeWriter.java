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
package io.jstach.apt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.TypeElement;

import io.jstach.annotation.JStacheFlags;
import io.jstach.apt.NamedTemplate.FileTemplate;
import io.jstach.apt.NamedTemplate.InlineTemplate;
import io.jstach.apt.TemplateCompilerLike.TemplateCompilerType;
import io.jstach.apt.TemplateCompilerLike.TemplateLoader;
import io.jstach.apt.context.RenderingCodeGenerator;
import io.jstach.apt.context.TemplateCompilerContext;
import io.jstach.apt.context.TemplateStack;
import io.jstach.apt.context.TemplateStack.RootTemplateStack;
import io.jstach.apt.context.VariableContext;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class CodeWriter {
    private final SwitchablePrintWriter writer;
    private final RenderingCodeGenerator codeGenerator;
    private final Map<String, NamedTemplate> partials;
    private final Set<JStacheFlags.Flag> flags;

    CodeWriter( 
            SwitchablePrintWriter writer, 
            RenderingCodeGenerator codeGenerator, 
            Map<String, NamedTemplate> partials,
            Set<JStacheFlags.Flag> flags) {
        this.writer = writer;
        this.codeGenerator = codeGenerator;
        this.partials = partials;
        this.flags = flags;
    }

    TemplateCompilerContext createTemplateContext(NamedTemplate template, TypeElement element, String rootExpression, VariableContext variableContext) throws AnnotatedException {
        return codeGenerator.createTemplateCompilerContext(TemplateStack.ofRoot(template), element, rootExpression, variableContext);
    }

    void println(String s) {
        writer.println(s);
    }

    void compileTemplate(TextFileObject resource, 
            TemplateCompilerContext context, 
            TemplateCompilerType templateCompilerType) 
            throws IOException, ProcessingException {
        
        
        TemplateStack stack = context.getTemplateStack();
        String templateName = stack.getTemplateName();
        
        NamedTemplate rootTemplate;
        
        if (stack instanceof RootTemplateStack rt) {
            rootTemplate = rt.template();
        }
        else {
            throw new IllegalStateException("Expected root template");
        }
        
        TemplateLoader templateLoader = (name) -> { 
            NamedTemplate nt;
            if (name.equals(templateName)) {
                 nt = rootTemplate;
            }
            else {
                nt = partials.get(name);
            }
            if (nt == null) {
                nt = new FileTemplate(name, name);
            }
            if (nt instanceof FileTemplate ft) {
                String path = ft.path();
                return new NamedReader(
                        new InputStreamReader(new BufferedInputStream(resource.openInputStream(path)), resource.charset()), name, path);
            }
            else if (nt instanceof InlineTemplate it) {
                String template = it.template();
                StringReader sr = new StringReader(template);
                return new NamedReader(
                       sr, name, "INLINE");
            }
            else {
                throw new IllegalStateException();
            }


        };
        
        try (TemplateCompiler templateCompiler = TemplateCompiler.createCompiler(templateName, templateLoader, writer, context, templateCompilerType, flags)) {
            templateCompiler.run();
        }
    }


}
