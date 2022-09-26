/*
 * Copyright (c) 2015, Victor Nazarov <asviraspossible@gmail.com>
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class TextFileObject {
  //  private final FileObject resource;
    private final ProcessingEnvironment env;
    private final Charset charset;
    
    TextFileObject(ProcessingEnvironment env, Charset charset) {
        //this.resource = resource;
        this.env = env;
        this.charset = charset;
    }
    

    InputStream openInputStream(String name) throws IOException {
        FileObject resource = env.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", name);
        if (resource.getLastModified() > 0) {
            return resource.openInputStream();
        }
        
        if (resource.getClass().getName().contains("eclipse")) {
            FileObject dummy = 
                    env.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", "dummy");
            // target/classes/dummy
            Path projectPath = Paths.get(dummy.toUri()).getParent().getParent().getParent();
            
            Path filePath = Path.of("src/main/resources", name);
            
            Path fullPath = projectPath.resolve(filePath);
            
            return Files.newInputStream(fullPath);
        }
        
        return resource.openInputStream();
    }

    Charset charset() {
        return charset;
    }
}
