package org.example.sql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * @author bonult 
 * <i>类前边加上此注解才能进行josn化</i>
 */
@Target({ TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface JSON {

	String value() default "";
}
