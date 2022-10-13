package io.jstach.apt;


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

}
