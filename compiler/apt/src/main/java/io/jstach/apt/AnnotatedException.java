package io.jstach.apt;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

public class AnnotatedException extends Exception {
    
    private static final long serialVersionUID = 1L;
    private final Element element;
    private final String message;

    public AnnotatedException(String message, Element element) {
        super(message);
        this.element = element;
        this.message = message;
    }
    
    public AnnotatedException(Element element, String message) {
        this(message, element);
    }
    
    public Element getElement() {
        return element;
    }
    
    public void report(Messager messager) {
        messager.printMessage(Kind.ERROR, message, element);
    }

}
