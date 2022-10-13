package io.jstach;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Template {
    
    String name();
    String path() default "";
    String template() default NOT_SET;
    
    public static String NOT_SET = "__NOT_SET__";
}
