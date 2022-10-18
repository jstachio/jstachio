package io.jstach.escapers;

import java.io.IOException;

import io.jstach.Appender;

public class HtmlEscaper implements Appender<Appendable> {


    private static final String QUOT = "&quot;";
    private static final String GT = "&gt;";
    private static final String LT = "&lt;";
    private static final String AMP = "&amp;";

    @Override
    public void append(Appendable a, CharSequence s) throws IOException {
        s = s == null ? "null" : s;
        append(a , s, 0, s.length());
    }

    @Override
    public void append(Appendable a, CharSequence csq, int start, int end) throws IOException {
        csq = csq == null ? "null" : csq;
        for (int i = start; i < end; i++) {
            char c = csq.charAt(i);
            switch (c) {
            case '&' -> {
                a.append(csq, start, i);
                start = i + 1;
                a.append(AMP);

            }
            case '<' -> {
                a.append(csq, start, i);
                start = i + 1;
                a.append(LT);

            }
            case '>' -> {
                a.append(csq, start, i);
                start = i + 1;
                a.append(GT);
            }
            case '"' -> {
                a.append(csq, start, i);
                start = i + 1;
                a.append(QUOT);
            }

            }
        }
        a.append(csq, start, end);

    }

    @Override
    public void append(Appendable a, char c) throws IOException {
        switch (c) {
        case '&' -> {
            a.append(AMP);
        }
        case '<' -> {
            a.append(LT);
        }
        case '>' -> {
            a.append(GT);
        }
        case '"' -> {
            a.append(QUOT);
        }
        default -> {
            a.append(c);
        }
        }
    }

}
