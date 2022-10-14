package io.jstach.spi;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.context.ContextNode;

public interface Formatter {

    public boolean format(Appendable a, String path, @Nullable Object o) throws IOException;
    
    default boolean format(Appendable a, String path, Class<?> c, @Nullable Object o) throws IOException {
        return format(a, path, o);
    }
    
    default boolean format(Appendable a, String path, char c) throws IOException {
        return format(a, path, char.class, c);
    }
    
    default boolean format(Appendable a, String path, short s) throws IOException {
        return format(a, path, short.class, s);
    }
    
    default boolean format(Appendable a, String path, int i) throws IOException {
        return format(a, path, int.class, i);
    }
    
    default boolean format(Appendable a, String path, long l) throws IOException {
        return format(a, path, long.class, l);
    }
    
    default boolean format(Appendable a, String path, double d) throws IOException {
        return format(a, path, double.class, d);
    }
    
    default boolean format(Appendable a, String path, boolean b) throws IOException {
        return format(a, path, boolean.class, b);
    }
    
    default boolean format(Appendable a, String path, String s) throws IOException {
        return format(a, path, String.class, s);
    }
    
    public enum DefaultFormatter implements Formatter {
        INSTANCE;
        
        @Override
        public boolean format(Appendable a, String path, @Nullable Object o) throws IOException {
            if (o == null) {
                throw new NullPointerException("null at: " + path);
            }
            else if (o instanceof ContextNode m) {
                a.append(m.renderString());
            }
            else {
                a.append(String.valueOf(o));
            }
            return true;
        }
    }

}
