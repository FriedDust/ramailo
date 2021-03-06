package com.ramailo.meta.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ramailo.service.BaseAction;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RamailoResource {

	String value();

	String stringify();

	String[] gridHeaders() default {};

	Class<? extends BaseAction<?>>[] actions() default {};

}
