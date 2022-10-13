package io.jstach.apt.context;

import org.eclipse.jdt.annotation.Nullable;

public interface TemplateStack {
    
    public String getTemplateName();
    
    public @Nullable TemplateStack getCaller();
    
    default String describeTemplateStack() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTemplateName());
        @Nullable
        TemplateStack parent = getCaller();
        while(parent != null) {
            sb.append(" <- ");
            sb.append(parent.getTemplateName());
            parent = parent.getCaller();
        }

        return sb.toString();
    }
    
    default TemplateStack ofPartial(String templateName) {
        return new SimpleTemplateStack(templateName, this);
    }
    
    default TemplateStack ofLambda(String templateName) {
        return new SimpleTemplateStack(templateName, this);
    }
    
    public static TemplateStack of(String templateName) {
        return new SimpleTemplateStack(templateName, null);
    }
    
    record SimpleTemplateStack(String templateName, @Nullable TemplateStack caller) implements TemplateStack {
        
        public String getTemplateName() {
            return templateName;
        }
        
        public @Nullable TemplateStack getCaller() {
            return caller;
        }
    }

}
