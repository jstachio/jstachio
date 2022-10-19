package io.jstach.apt.context;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.NamedTemplate;

public sealed interface TemplateStack {
    
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
    
    public static TemplateStack ofRoot(NamedTemplate template) {
        return new RootTemplateStack(template);
    }
    
    record SimpleTemplateStack(String templateName, @Nullable TemplateStack caller) implements TemplateStack {
        
        public String getTemplateName() {
            return templateName;
        }
        
        public @Nullable TemplateStack getCaller() {
            return caller;
        }
    }
    
    record RootTemplateStack(NamedTemplate template) implements TemplateStack {
        
        public String getTemplateName() {
            return template.name();
        }
        
        public @Nullable TemplateStack getCaller() {
            return null;
        }
    }

}
