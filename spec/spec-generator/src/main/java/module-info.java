module io.jstach.spec.generator {
    
    exports io.jstach.spec.generator;
    
    requires transitive io.jstach;
    
    requires static org.eclipse.jdt.annotation;
    
    requires static org.kohsuke.metainf_services;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires org.apache.commons.text;
    requires org.apache.commons.lang3;
    requires com.samskivert.jmustache;
}