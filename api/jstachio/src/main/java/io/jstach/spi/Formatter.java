package io.jstach.spi;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.context.ContextNode;

public interface Formatter {

    //public void format(Appender downstream, Appendable a, String path, @Nullable Object o) throws IOException;
    
    public void format(Appender downstream, Appendable a, String path, Class<?> c, @Nullable Object o) throws IOException;
    
    public void format(Appender downstream, Appendable a, String path, char c) throws IOException;
    
    public void format(Appender downstream, Appendable a, String path, short s) throws IOException;
    
    public void format(Appender downstream, Appendable a, String path, int i) throws IOException;
    
    public void format(Appender downstream, Appendable a, String path, long l) throws IOException;
    
    public void format(Appender downstream, Appendable a, String path, double d) throws IOException;
    
    public void format(Appender downstream, Appendable a, String path, boolean b) throws IOException;
    
    public void format(Appender downstream, Appendable a, String path, String s) throws IOException;
    
    
    interface SimpleFormatter extends Formatter {
        
        default void format(Appender downstream, Appendable a, String path, char c) throws IOException {
            format(downstream, a, path, char.class, c);
        }
        
        default void format(Appender downstream, Appendable a, String path, short s) throws IOException {
            format(downstream, a, path, short.class, s);
        }
        
        default void format(Appender downstream, Appendable a, String path, int i) throws IOException {
            format(downstream, a, path, int.class, i);
        }
        
        default void format(Appender downstream, Appendable a, String path, long l) throws IOException {
            format(downstream, a, path, long.class, l);
        }
        
        default void format(Appender downstream, Appendable a, String path, double d) throws IOException {
            format(downstream, a, path, double.class, d);
        }
        
        default void format(Appender downstream, Appendable a, String path, boolean b) throws IOException {
            format(downstream, a, path, boolean.class, b);
        }
        
        default void format(Appender downstream, Appendable a, String path, String s) throws IOException {
            format(downstream, a, path, String.class, s);
        }
    }
    
    interface DownstreamFormatter extends Formatter {

        default void format(Appender downstream, Appendable a, String path, char c) throws IOException {
            downstream.append(a, c);
        }
        
        default void format(Appender downstream, Appendable a, String path, short s) throws IOException {
            downstream.append(a, s);
        }
        
        default void format(Appender downstream, Appendable a, String path, int i) throws IOException {
            downstream.append(a, i);
        }
        
        default void format(Appender downstream, Appendable a, String path, long l) throws IOException {
            downstream.append(a, l);
        }
        
        default void format(Appender downstream, Appendable a, String path, double d) throws IOException {
            downstream.append(a, d);
        }
        
        default void format(Appender downstream, Appendable a, String path, boolean b) throws IOException {
            downstream.append(a,b);
        }
        
        default void format(Appender downstream, Appendable a, String path, String s) throws IOException {
            downstream.append(a, s);
        }
    }
    
    interface ForwardingFormatter extends Formatter {

        public Formatter formatter();
        
        default void format(Appender downstream, Appendable a, String path, Class<?> c, @Nullable Object o)
                throws IOException {
            formatter().format(downstream, a, path, c, o);
        }

        default void format(Appender downstream, Appendable a, String path, char c) throws IOException {
            formatter().format(downstream, a, path, c);
        }

        default void format(Appender downstream, Appendable a, String path, short s) throws IOException {
            formatter().format(downstream, a, path, s);
        }

        default void format(Appender downstream, Appendable a, String path, int i) throws IOException {
            formatter().format(downstream, a, path, i);
        }

        default void format(Appender downstream, Appendable a, String path, long l) throws IOException {
            formatter().format(downstream, a, path, l);
        }

        default void format(Appender downstream, Appendable a, String path, double d) throws IOException {
            formatter().format(downstream, a, path, d);
        }

        default void format(Appender downstream, Appendable a, String path, boolean b) throws IOException {
            formatter().format(downstream, a, path, b);
        }

        default void format(Appender downstream, Appendable a, String path, String s) throws IOException {
            formatter().format(downstream, a, path, s);
        }
        
    }
    
    enum DefaultFormatter implements DownstreamFormatter {
        INSTANCE;

        @Override
        public void format(Appender downstream, Appendable a, String path, Class<?> c, @Nullable Object o)
                throws IOException {
            if (o == null) {
                throw new NullPointerException("null at: " + path);
            } else if (o instanceof ContextNode m) {
                downstream.append(a, m.renderString());
            } else {
                downstream.append(a, String.valueOf(o));
            }
        }

    }

}
