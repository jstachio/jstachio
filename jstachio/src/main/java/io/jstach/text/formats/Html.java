/*
 * Copyright (c) 2014, Victor Nazarov <asviraspossible@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.jstach.text.formats;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

/**
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
@TextFormat
public class Html {

    public static Appendable createEscapingAppendable(Appendable appendable) {
        return new HtmlAppendale(appendable);
    }

    private static class HtmlAppendale implements Appendable {

        private final Appendable appendable;

        public HtmlAppendale(Appendable appendable) {
            this.appendable = appendable;
        }

        @Override
        public Appendable append(@Nullable CharSequence csq) throws IOException {
            csq = csq == null ? "null" : csq;
            return append(csq, 0, csq.length());
        }

        @Override
        public Appendable append(@Nullable CharSequence csq, int start, int end) throws IOException {
            csq = csq == null ? "null" : csq;
            for (int i = start; i < end; i++) {
                char c = csq.charAt(i);
                //TODO use switch here
                //TODO better yet get rid of this class for something better
                if (c == '&') {
                    appendable.append(csq, start, i);
                    start = i + 1;
                    appendable.append("&amp;");
                } else if (c == '<') {
                    appendable.append(csq, start, i);
                    start = i + 1;
                    appendable.append("&lt;");
                } else if (c == '>') {
                    appendable.append(csq, start, i);
                    start = i + 1;
                    appendable.append("&gt;");
                } else if (c == '"') {
                    appendable.append(csq, start, i);
                    start = i + 1;
                    appendable.append("&quot;");
                }
                
            }
            appendable.append(csq, start, end);
            return this;
        }

        @Override
        public Appendable append(char c) throws IOException {
            if (c == '&')
                appendable.append("&amp;");
            else if (c == '<')
                appendable.append("&lt;");
            else if (c == '>')
                appendable.append("&gt;");
            else
                appendable.append(c);
            return this;
        }
    }

    private Html() {
    }
}
