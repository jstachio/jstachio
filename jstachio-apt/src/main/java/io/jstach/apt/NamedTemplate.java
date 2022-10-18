package io.jstach.apt;

import java.nio.charset.Charset;

public sealed interface NamedTemplate {
    
    String name();
    
    Type type();
    
    public enum Type {
        FILE,
        INLINE
    }
    
    public record FileTemplate(String name, String path) implements NamedTemplate {
        @Override
        public Type type() {
            return Type.FILE;
        }
    }
    
    public record InlineTemplate(String name, String template) implements NamedTemplate {
        @Override
        public Type type() {
            return Type.INLINE;
        }
    }
    
    public record RootTemplate(
            String name,
            String className,
            String adapterName,
            String template, 
            String path, 
            String basePath,
            Charset charset,
            Type type) implements NamedTemplate  {
        
    }

}
