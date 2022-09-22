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
package com.github.sviperll.staticmustache.context;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.Nullable;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class VariableContext {
    public static VariableContext createDefaultContext() {
        TreeMap<String, Integer> variables = new TreeMap<String, Integer>();
        variables.put("writer", 1);
        variables.put("unescapedWriter", 1);
        return new VariableContext("writer", "unescapedWriter", variables, null);
    }

    private final String writer;
    private final String unescapedWriter;
    private final Map<String, Integer> variables;
    private final @Nullable VariableContext parent;

    VariableContext(String writer, String unescapedWriter, Map<String, Integer> variables, @Nullable VariableContext parent) {
        this.writer = writer;
        this.unescapedWriter = unescapedWriter;
        this.variables = variables;
        this.parent = parent;
    }

    public String writer() {
        return writer;
    }

    public String unescapedWriter() {
        return unescapedWriter;
    }
    
    public String getFormatter() {
        return "formatter";
    }

    VariableContext unescaped() {
        return new VariableContext(unescapedWriter, unescapedWriter, variables, parent);
    }

    private Integer lookupVariable(String baseName) {
        Integer result = variables.get(baseName);
        if (result != null || parent == null)
            return result;
        else
            return parent.lookupVariable(baseName);
    }

    public String introduceNewNameLike(String baseName) {
        int subscriptIndex = baseName.length();
        while (Character.isDigit(baseName.charAt(subscriptIndex - 1))) {
            subscriptIndex--;
        }
        if (subscriptIndex == baseName.length()) {
            Integer count = lookupVariable(baseName);
            if (count == null) {
                variables.put(baseName, 1);
                return baseName;
            } else {
                variables.put(baseName, count + 1);
                return baseName + count;
            }
        } else {
            Integer requestedCount = Integer.parseInt(baseName.substring(subscriptIndex));
            baseName = baseName.substring(0, subscriptIndex);
            Integer currentCount = lookupVariable(baseName);
            int count = currentCount == null || currentCount < requestedCount ? requestedCount : currentCount;
            variables.put(baseName, count + 1);
            return baseName + count;
        }
    }

    VariableContext createEnclosedContext() {
        return new VariableContext(writer, unescapedWriter, new TreeMap<String, Integer>(), this);
    }
}
