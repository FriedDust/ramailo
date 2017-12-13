package com.ramailo.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.ManyToOne;

import org.apache.commons.beanutils.PropertyUtils;

import com.ramailo.annotation.RamailoField;
import com.ramailo.annotation.RamailoList;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class AttributeUtility {

	public static void copyAttributes(Object to, Object from)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Field[] fields = to.getClass().getDeclaredFields();

		for (Field field : fields) {
			String fieldName = field.getName();

			if (field.isAnnotationPresent(RamailoField.class)) {
				Object value = PropertyUtils.getSimpleProperty(from, fieldName);
				PropertyUtils.setSimpleProperty(to, field.getName(), value);
			} else if (field.isAnnotationPresent(ManyToOne.class)) {
				Object childTo = PropertyUtils.getSimpleProperty(to, fieldName);
				Object childFrom = PropertyUtils.getSimpleProperty(from, fieldName);

				copyAttributes(childTo, childFrom);
			} else if (field.isAnnotationPresent(RamailoList.class)) {
				List<?> childrenTo = (List<?>) PropertyUtils.getSimpleProperty(to, fieldName);
				List<?> childrenFrom = (List<?>) PropertyUtils.getSimpleProperty(from, fieldName);

				List tempList = new ArrayList();

				for (Object o2 : childrenFrom) {
					Object o1 = find(childrenTo, o2);
					if (o1 != null) {
						copyAttributes(o1, o2);
						tempList.add(o1);
					} else {
						tempList.add(o2);
					}
					
				}

				childrenTo.clear();
				childrenTo.addAll(tempList);
			}
		}
	}

	private static Object find(List list, Object value) {
		for (Object o : list) {
			if (o.equals(value))
				return o;
		}
		return null;
	}
}
