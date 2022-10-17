package io.jstach.text.formats;

import java.io.IOException;

import io.jstach.spi.Escaper;

public class HtmlEscaper implements Escaper {


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
        var downstream = getDownstream();
        csq = csq == null ? "null" : csq;
        for (int i = start; i < end; i++) {
            char c = csq.charAt(i);
            switch (c) {
            case '&' -> {
                downstream.append(a, csq, start, i);
                start = i + 1;
                downstream.append(a, AMP);

            }
            case '<' -> {
                downstream.append(a, csq, start, i);
                start = i + 1;
                downstream.append(a, LT);

            }
            case '>' -> {
                downstream.append(a, csq, start, i);
                start = i + 1;
                downstream.append(a, GT);
            }
            case '"' -> {
                downstream.append(a, csq, start, i);
                start = i + 1;
                downstream.append(a, QUOT);
            }

            }
        }
        downstream.append(a, csq, start, end);

    }

    @Override
    public void append(Appendable a, char c) throws IOException {
        var downstream = getDownstream();
        switch (c) {
        case '&' -> {
            downstream.append(a, AMP);
        }
        case '<' -> {
            downstream.append(a, LT);
        }
        case '>' -> {
            downstream.append(a, GT);
        }
        case '"' -> {
            downstream.append(a, QUOT);
        }
        default -> {
            downstream.append(a, c);
        }
        }
    }

}
