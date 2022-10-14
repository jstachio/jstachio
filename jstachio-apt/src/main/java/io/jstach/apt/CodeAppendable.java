package io.jstach.apt;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.Nullable;

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
    
    public class HiddenCodeAppendable  implements CodeAppendable {
        private final Consumer<CharSequence> sink;
        
        public HiddenCodeAppendable(Consumer<CharSequence> sink) {
            super();
            this.sink = sink;
        }

        @Override
        public HiddenCodeAppendable append(@Nullable CharSequence csq) {
            if (csq != null) {
                sink.accept(csq);
            }
            return this;
        }

        @Override
        public HiddenCodeAppendable append(@Nullable CharSequence csq, int start, int end) {
            if (csq != null) {
                sink.accept(csq);
            }
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
        public StringCodeAppendable append(@Nullable CharSequence csq) {
            buffer.append(csq);
            return this;
        }

        @Override
        public StringCodeAppendable append(@Nullable CharSequence csq, int start, int end) {
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
    
    public static String stringLiteralConcat(String s) {
        int i = 0;
        StringBuilder code = new StringBuilder();
        for (String line : split(s, "\\n")) {
            if (i > 0) {
                code.append(" +");
            }
            code.append("\n    \"");
            code.append(line);
            code.append("\"");
            i++;
        }
        String result = code.toString();
        if (result.isEmpty()) {
            result = "\"\"";
        }
        return result;
    }
    
    static List<String> split(String s , String delim) {
        int dl = delim.length();
        int sl = s.length();
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < sl; ) {
            int end = s.indexOf(delim, i);
            end = end < 0 ? sl : Integer.min(end + dl, sl);
            String chunk = s.substring(i, end);
            tokens.add(chunk);
            i = end;
        }
        return tokens;
    }
    
    public static String escapeJava(String s) {
        s = s.replace("\"", "\\\"");
        s = s.replace("\\", "\\\\");
        s = s.replace("\n", "\\n");
        return s;
    }

}
