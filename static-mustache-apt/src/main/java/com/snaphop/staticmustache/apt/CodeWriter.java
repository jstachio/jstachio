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
package com.snaphop.staticmustache.apt;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;

import com.github.sviperll.staticmustache.TemplateCompilerFlags;
import com.github.sviperll.staticmustache.context.RenderingCodeGenerator;
import com.github.sviperll.staticmustache.context.TemplateCompilerContext;
import com.github.sviperll.staticmustache.context.VariableContext;
import com.snaphop.staticmustache.apt.NamedTemplate.FileTemplate;
import com.snaphop.staticmustache.apt.NamedTemplate.InlineTemplate;
import com.snaphop.staticmustache.apt.TemplateCompilerLike.TemplateCompilerType;
import com.snaphop.staticmustache.apt.TemplateCompilerLike.TemplateLoader;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class CodeWriter {
    private final Messager messager;
    private final SwitchablePrintWriter writer;
    private final RenderingCodeGenerator codeGenerator;
    private final Map<String, NamedTemplate> templatePaths;
    private final Set<TemplateCompilerFlags.Flag> flags;

    CodeWriter(Messager messager, 
            SwitchablePrintWriter writer, 
            RenderingCodeGenerator codeGenerator, 
            Map<String, NamedTemplate>templatePaths,
            Set<TemplateCompilerFlags.Flag> flags) {
        this.messager = messager;
        this.writer = writer;
        this.codeGenerator = codeGenerator;
        this.templatePaths = templatePaths;
        this.flags = flags;
    }

    TemplateCompilerContext createTemplateContext(String templateName, TypeElement element, String rootExpression, VariableContext variableContext) {
        return codeGenerator.createTemplateCompilerContext(templateName, element, rootExpression, variableContext);
    }

    void println(String s) {
        writer.println(s);
    }

    void compileTemplate(TextFileObject resource, 
            TemplateCompilerContext context, 
            TemplateCompilerType templateCompilerType) 
            throws IOException, ProcessingException {
        
        String templateName = context.getTemplateStack().getTemplateName();
        
        TemplateLoader templateLoader = (name) -> { 
            
            NamedTemplate nt = templatePaths.get(name);
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
        
//        try(InputStream inputStream = resource.openInputStream(templateName)) {
//            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//            try {
//                Reader inputReader = new InputStreamReader(inputStream, resource.charset());
//                try {
//                    NamedReader reader = new NamedReader(inputReader, templateName);
//                    try {
//                        //      factory.createTemplateCompiler(templateLoader, templateName, writer, context);
//                        TemplateCompiler templateCompiler = TemplateCompiler.createCompiler(templateName, templateLoader, writer, context, templateCompilerType);
//                        templateCompiler.run();
//                    } finally {
//                        reader.close();
//                    }
//                } finally {
//                    inputReader.close();
//                }
//            } finally {
//                try {
//                    bufferedInputStream.close();
//                } catch (Exception ex) {
//                    messager.printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
//                }
//            }
//        }
    }


}
