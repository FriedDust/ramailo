package com.ramailo.util;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class StringUtility {
	public static String splitCamelCase(String s) {
		return s.replaceAll(String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
	}

	public static String labelize(String s) {
		s = splitCamelCase(s);
		s = s.substring(0, 1).toUpperCase() + s.substring(1);

		return s;
	}
}
