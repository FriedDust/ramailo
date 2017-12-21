package com.ramailo.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * TypeCaster tries to cast String value to a given type.
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class TypeCaster {

	/**
	 * 
	 * @param value
	 *            a string value to be casted
	 * @param type
	 *            type to be converted to
	 * @return an instance of given type
	 * 
	 * @exception ClassCastException
	 *                if given type neither has a public static method valueOf nor
	 *                has a constructor with single argument of String
	 */
	public static <T> Object cast(String value, Class<T> type) {
		if (value == null)
			return null;
		if (type.equals(String.class))
			return value;

		Method method = null;
		try {
			method = type.getMethod("valueOf", String.class);
			return method.invoke(null, value);
		} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new ClassCastException("Cannot convert to " + type.getName());
		}
	}
}