package com.snaphop.staticmustache.apt;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

public interface CodeAppendable extends Appendable {
    
    default void print(String s) {
        try {
            append(s);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    default void println() {
        print(System.lineSeparator());
    }
    
    public boolean suppressesOutput();

    public void enableOutput();

    public void disableOutput();
    
    public record HiddenCodeAppendable(Consumer<CharSequence> sink)   implements CodeAppendable {
        
        
        @Override
        public HiddenCodeAppendable append(CharSequence csq) {
            sink.accept(csq);
            return this;
        }

        @Override
        public HiddenCodeAppendable append(CharSequence csq, int start, int end) {
            sink.accept(csq);
            return this;
        }

        @Override
        public HiddenCodeAppendable append(char c)  {
            append(String.valueOf(c));
            return this;
        }


        @Override
        public boolean suppressesOutput() {
            return false;
        }

        @Override
        public void enableOutput() {
        }

        @Override
        public void disableOutput() {
        }
        
    }
    
    public class StringCodeAppendable implements CodeAppendable {
        private final StringBuilder buffer;

        public StringCodeAppendable() {
            this(new StringBuilder());
        }
        
        public StringCodeAppendable(StringBuilder buffer) {
            super();
            this.buffer = buffer;
        }
        
        @Override
        public String toString() {
            return buffer.toString();
        }

        @Override
        public StringCodeAppendable append(CharSequence csq) {
            buffer.append(csq);
            return this;
        }

        @Override
        public StringCodeAppendable append(CharSequence csq, int start, int end) {
            buffer.append(csq, start, end);
            return this;
        }

        @Override
        public StringCodeAppendable append(char c) {
            buffer.append(c);
            return this;
        }

        @Override
        public boolean suppressesOutput() {
            return false;
        }

        @Override
        public void enableOutput() {
            
        }

        @Override
        public void disableOutput() {
        }
        
    }

}
