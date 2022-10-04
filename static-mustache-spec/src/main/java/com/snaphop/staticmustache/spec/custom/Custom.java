package com.snaphop.staticmustache.spec.custom;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
//import com.snaphop.staticmustache.spec.interpolation.InterpolationSpecTemplate;
//import com.snaphop.staticmustache.spec.sections.SectionsSpecTemplate;
import com.snaphop.staticmustache.spec.interpolation.InterpolationSpecTemplate;
import com.snaphop.staticmustache.spec.sections.SectionsSpecTemplate;

public class Custom {
    
    public record Person(String name) {
        public static final Person Joe = new Person("Joe");
    }
    
    @GenerateRenderableAdapter(template = InterpolationSpecTemplate.DOTTED_NAMES___BASIC_INTERPOLATION_FILE)
    public record DottedNamesBasicInterpolation(Person person) {
    }
    
    @GenerateRenderableAdapter(template = InterpolationSpecTemplate.DOTTED_NAMES___AMPERSAND_INTERPOLATION_FILE)
    public record DottedNamesAmpersandInterpolation(Person person) {
    }
    
    @GenerateRenderableAdapter(template = InterpolationSpecTemplate.DOTTED_NAMES___TRIPLE_MUSTACHE_INTERPOLATION_FILE)
    public record DottedNamesTripleMustacheInterpolation(Person person) {
    }
    
    @GenerateRenderableAdapter(template = SectionsSpecTemplate.CONTEXT_FILE)
    public record Context(Person context) {
    }

}
