package com.ramailo.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RamailoAction {

	String label() default "";

	String pathName() default "";

	String methodType() default "GET";
}
