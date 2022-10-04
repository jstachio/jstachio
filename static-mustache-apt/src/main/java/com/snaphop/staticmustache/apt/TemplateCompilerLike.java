package com.snaphop.staticmustache.apt;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import com.github.sviperll.staticmustache.TemplateCompilerFlags;
import com.snaphop.staticmustache.apt.CodeAppendable.StringCodeAppendable;

interface TemplateCompilerLike extends AutoCloseable {

    void run() throws ProcessingException, IOException;

    void close() throws IOException;
    
    TemplateCompilerType getCompilerType();
        
    String getTemplateName();

    @Nullable
    TemplateCompilerLike getCaller();

    default TemplateLoader getTemplateLoader() {
        return Objects.requireNonNull(getCaller()).getTemplateLoader();
    }

    default CodeAppendable getWriter() {
        return Objects.requireNonNull(getCaller()).getWriter();
    }
    
    default Set<TemplateCompilerFlags.Flag> flags() {
        return Objects.requireNonNull(getCaller()).flags();
    }
    
    @Nullable ParameterPartial currentParameterPartial();

    ParameterPartial createParameterPartial(String templateName) throws ProcessingException, IOException;

    /*
     * TODO rename to TemplateType
     */
    public enum TemplateCompilerType {
        SIMPLE,
        HEADER,
        FOOTER,
        PARTIAL_TEMPLATE,
        PARAM_PARTIAL_TEMPLATE; // aka parent aka {{< parent }}
    }
    
    interface TemplateLoader {

        NamedReader open(String name) throws IOException;
    }
    
    abstract class AbstractPartial implements AutoCloseable {
        private final TemplateCompilerLike templateCompiler;
        
        public AbstractPartial(TemplateCompilerLike templateCompiler) {
            super();
            this.templateCompiler = templateCompiler;
        }
        
        public String getTemplateName() {
            return templateCompiler.getTemplateName();
        }
        
        void run() throws ProcessingException, IOException {
            templateCompiler.run();
        }
        
        @Override
        public void close() throws IOException {
            templateCompiler.close();
        }

    }

    class Partial extends AbstractPartial {

        public Partial(TemplateCompilerLike templateCompiler) {
            super(templateCompiler);
        }
        
        @Override
        public String toString() {
            return "Partial(template = " + getTemplateName() + ")";
        }
    }
    
    class ParameterPartial extends AbstractPartial {

        private final Map<String, StringCodeAppendable> blockArgs = new LinkedHashMap<>();

        public ParameterPartial(TemplateCompilerLike templateCompiler) {
            super(templateCompiler);
        }
        
        public Map<String, StringCodeAppendable> getBlockArgs() {
            return blockArgs;
        }
        
        @Override
        public String toString() {
            return "ParameterPartial(template = " + getTemplateName() + " args=" + blockArgs + ")";
        }

    }

}