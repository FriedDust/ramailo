package com.ramailo.util;

import java.lang.reflect.Field;

import javax.persistence.Id;

import com.ramailo.ResourceMeta;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class PkUtility {

	public static Object castToPkType(ResourceMeta resource) {
		Field field = findPkField(resource.getEntityClass());
		Class<?> type = field.getType();

		return TypeCaster.cast(resource.getId(), type);
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
