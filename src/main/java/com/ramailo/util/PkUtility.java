package com.ramailo.util;

import java.lang.reflect.Field;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class PkUtility {

	public static Object castToPkType(Class<?> entity, String value) {
		Field field = findPkField(entity);
		Class<?> type = field.getType();

		return TypeCaster.cast(value, type);
	}

	public static Field findAutoPkField(Class<?> entity) {
		for (Field field : entity.getDeclaredFields()) {
			if (field.isAnnotationPresent(GeneratedValue.class)
					&& field.getAnnotation(GeneratedValue.class).strategy().equals(GenerationType.IDENTITY)) {
				return field;
			}
		}
		return null;
	}

	private static Field findPkField(Class<?> entity) {
		for (Field field : entity.getDeclaredFields()) {
			if (field.isAnnotationPresent(Id.class)) {
				return field;
			}
		}
		return null;
	}
}
