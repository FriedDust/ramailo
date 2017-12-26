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
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RamailoList {

	String label() default "";

	Class<?> childrenType();
}