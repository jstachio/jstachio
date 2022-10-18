package io.jstach.spec.mustache.spec.custom;

import io.jstach.annotation.JStach;
import io.jstach.spec.mustache.spec.interpolation.InterpolationSpecTemplate;
import io.jstach.spec.mustache.spec.inverted.InvertedSpecTemplate;
import io.jstach.spec.mustache.spec.sections.SectionsSpecTemplate;

public class Custom {
    
    public record Person(String name) {
        public static final Person Joe = new Person("Joe");
    }
    
    @JStach(path = InterpolationSpecTemplate.DOTTED_NAMES___BASIC_INTERPOLATION_FILE)
    public record DottedNamesBasicInterpolation(Person person) {
    }
    
    @JStach(path = InterpolationSpecTemplate.DOTTED_NAMES___AMPERSAND_INTERPOLATION_FILE)
    public record DottedNamesAmpersandInterpolation(Person person) {
    }
    
    @JStach(path = InterpolationSpecTemplate.DOTTED_NAMES___TRIPLE_MUSTACHE_INTERPOLATION_FILE)
    public record DottedNamesTripleMustacheInterpolation(Person person) {
    }
    
    @JStach(path = SectionsSpecTemplate.CONTEXT_FILE)
    public record Context(Person context) {
    }
    
    @JStach(path = InvertedSpecTemplate.DOTTED_NAMES___TRUTHY_FILE)
    public record DottedNamesTruthy(A a) {
        public static DottedNamesTruthy test() {
            return new DottedNamesTruthy(new A(new B(true)));
        }
        public record A(B b) {
            
        }
        public record B(Boolean c) {
        }
    }


    
//    @GenerateRenderableAdapter(template = InterpolationSpecTemplate.DOTTED_NAMES___BROKEN_CHAIN_RESOLUTION_FILE)
//    public record DottedNamesBrokenChainResolution(A a, C c) {
//        public static DottedNamesBrokenChainResolution test() {
//            return new DottedNamesBrokenChainResolution(new A(new B()), new C("Jim"));
//        }
//
//        public record A(B b) {
//        }
//        
//        public record B() {
//        }
//        
//        public record C(String name) {
//        }
//    }

}
