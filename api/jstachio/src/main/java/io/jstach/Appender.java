package io.jstach;

import java.io.IOException;

public interface Appender {

    public void append(Appendable a, CharSequence s) throws IOException;
    
    public void append(Appendable a, CharSequence csq, int start, int end) throws IOException;

    public void append(Appendable a, char c) throws IOException; 

    default void append(Appendable a, short s) throws IOException {
        append(a, String.valueOf(s));
    }

    default void append(Appendable a, int i) throws IOException {
        append(a, String.valueOf(i));
    }

    default void append(Appendable a, long l) throws IOException {
        append(a, String.valueOf(l));
    }

    default void append(Appendable a, double d) throws IOException {
        append(a, String.valueOf(d));
    }

    default void append(Appendable a, boolean b) throws IOException {
        append(a, String.valueOf(b));
    }
    
    enum DefaultAppender implements Appender {
        INSTANCE;

        @Override
        public void append(Appendable a, CharSequence s) throws IOException {
            a.append(s);
        }

        @Override
        public void append(Appendable a, CharSequence csq, int start, int end) throws IOException {
            a.append(csq, start, end);
        }

        @Override
        public void append(Appendable a, char c) throws IOException {
            a.append(c);
        }
    }

}
