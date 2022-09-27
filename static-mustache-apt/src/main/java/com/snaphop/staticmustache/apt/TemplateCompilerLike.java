package com.snaphop.staticmustache.apt;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import com.snaphop.staticmustache.apt.CodeAppendable.StringCodeAppendable;

interface TemplateCompilerLike extends AutoCloseable {

    void run() throws ProcessingException, IOException;

    void close() throws IOException;
    
    TemplateCompilerType getCompilerType();
        
    String getTemplateName();

    @Nullable
    TemplateCompilerLike getParent();

    default TemplateLoader getTemplateLoader() {
        return Objects.requireNonNull(getParent()).getTemplateLoader();
    }

    default CodeAppendable getWriter() {
        return Objects.requireNonNull(getParent()).getWriter();

    }
    
    @Nullable PartialTemplateCompiler currentPartial();

    PartialTemplateCompiler createPartialCompiler(String templateName) throws ProcessingException, IOException;

    /*
     * TODO rename to TemplateType
     */
    public enum TemplateCompilerType {
        SIMPLE,
        HEADER,
        FOOTER,
        PARAM_PARTIAL_TEMPLATE; // aka parent aka {{< parent }}
    }
    
    interface TemplateLoader {

        NamedReader open(String name) throws IOException;
    }

    class PartialTemplateCompiler implements AutoCloseable {

        private final TemplateCompilerLike templateCompiler;

        private final Map<String, StringCodeAppendable> blockArgs = new LinkedHashMap<>();

        private final Map<String, StringCodeAppendable> blocks = new LinkedHashMap<>();

        public PartialTemplateCompiler(TemplateCompilerLike templateCompiler) {
            super();
            this.templateCompiler = templateCompiler;
        }

        void run() throws ProcessingException, IOException {
            templateCompiler.run();
        }

        @Override
        public void close() throws IOException {
            templateCompiler.close();
        }
        
        public Map<String, StringCodeAppendable> getBlockArgs() {
            return blockArgs;
        }
        
        public Map<String, StringCodeAppendable> getBlocks() {
            return blocks;
        }
        
        @Override
        public String toString() {
            return "PartialTemplateCompiler: " + blockArgs;
        }

    }

}