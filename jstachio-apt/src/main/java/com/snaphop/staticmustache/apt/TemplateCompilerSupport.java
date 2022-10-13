package com.snaphop.staticmustache.apt;

import com.snaphop.staticmustache.apt.TemplateCompilerLike.TemplateLoader;

public interface TemplateCompilerSupport {
    
    public TemplateLoader getTemplateLoader();
    
    public CodeAppendable getWriter();
    
}
