package io.jstach.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
@Documented
public @interface JStacheFormatterTypes {

	public Class<?>[] types() default {};

	public String[] patterns() default {};

}
