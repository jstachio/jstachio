package com.snaphop.staticmustache.apt;

import java.io.IOException;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

interface TemplateCompilerLike extends AutoCloseable {
    

    void run() throws ProcessingException, IOException;

    void close() throws IOException;
    
    @Nullable TemplateCompilerLike getParent();
    
    default TemplateLoader getTemplateLoader() {
        return Objects.requireNonNull(getParent()).getTemplateLoader();
    }
    
    default CodeAppendable getWriter() {
        return Objects.requireNonNull(getParent()).getWriter();

    }
    
    PartialTemplateCompiler createPartialCompiler(String templateName) throws ProcessingException, IOException;
    
    interface TemplateLoader {
        NamedReader open(String name) throws IOException;
    }
    
    class PartialTemplateCompiler implements AutoCloseable {
        
        private final TemplateCompilerLike templateCompiler;

        public PartialTemplateCompiler(TemplateCompilerLike templateCompiler) {
            super();
            this.templateCompiler = templateCompiler;
        }
        
        void run() throws ProcessingException, IOException {
        }
        
        @Override
        public void close() throws IOException {
            templateCompiler.close();
            
        }

        
    }
    
}