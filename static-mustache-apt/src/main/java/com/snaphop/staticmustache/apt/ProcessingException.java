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
package com.snaphop.staticmustache.apt;

import org.eclipse.jdt.annotation.Nullable;

import com.github.sviperll.staticmustache.context.ContextException;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class ProcessingException extends Exception {
    private final Position position;

    private ProcessingException(Position position, @Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        this.position = position;
    }

    public ProcessingException(Position position, @Nullable String message) {
        this(position, message, null);
    }

    public ProcessingException(Position position, ContextException contextException) {
        this(position, contextException.getMessage(), contextException);
    }
    

    public ProcessingException(Position position, Exception contextException) {
        this(position, contextException.getClass().getName() + ": " + contextException.getMessage(), contextException);
    }

    public Position position() {
        return position;
    }
    
    public static class AnnotationProcessingException extends ProcessingException {
        
        private final AnnotatedException annotatedException;
       
        public AnnotationProcessingException(Position position, AnnotatedException annotedException) {
            super(position, annotedException);
            this.annotatedException = annotedException;
        }
        
        public AnnotatedException getAnnotatedException() {
            return annotatedException;
        }
        
    }
}
