package io.jstach.spi;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.Appender;
import io.jstach.context.ContextNode;

public interface Formatter {

    
    <A extends Appendable, APPENDER extends Appender<A>> //
    void format(APPENDER downstream, A a, String path, Class<?> c, @Nullable Object o) throws IOException;
    
    default <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path, char c) throws IOException {
        downstream.append(a, c);
    }
    
    default <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path, short s) throws IOException {
        downstream.append(a, s);
    }
    
    default <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path, int i) throws IOException {
        downstream.append(a, i);
    }
    
    default <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path, long l) throws IOException {
        downstream.append(a, l);
    }
    
    default <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path, double d) throws IOException {
        downstream.append(a, d);
    }
    
    default <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path, boolean b) throws IOException {
        downstream.append(a,b);
    }
    
    default <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path, String s) throws IOException {
        downstream.append(a, s);
    }
   
    
    enum DefaultFormatter implements Formatter {
        INSTANCE;

        @Override
        public <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path, Class<?> c, @Nullable Object o)
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
