package io.jstach.spi;

import java.io.IOException;

public interface Escaper extends Appender {

    default Appender getDownstream() {
        return Appender.DefaultAppender.INSTANCE;
    }
    
    @Override
    public void append(Appendable a, CharSequence s) throws IOException;

    @Override
    public void append(Appendable a, CharSequence csq, int start, int end) throws IOException;

    @Override
    public void append(Appendable a, char c) throws IOException;

    @Override
    default void append(Appendable a, short s) throws IOException {
        getDownstream().append(a, s);
    }

    @Override
    default void append(Appendable a, int i) throws IOException {
        getDownstream().append(a, i);
    }

    @Override
    default void append(Appendable a, long l) throws IOException {
        getDownstream().append(a, l);
    }

    @Override
    default void append(Appendable a, double d) throws IOException {
        getDownstream().append(a, d);
    }

    @Override
    default void append(Appendable a, boolean b) throws IOException {
        getDownstream().append(a, b);
    }
    

}
