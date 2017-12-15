package com.ramailo.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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

	public static void copyAttributes(Object copyTo, Object copyFrom)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Field[] fields = copyTo.getClass().getDeclaredFields();

		for (Field field : fields) {
			String fieldName = field.getName();

			if (field.isAnnotationPresent(RamailoField.class)) {
				Object value = PropertyUtils.getSimpleProperty(copyFrom, fieldName);
				PropertyUtils.setSimpleProperty(copyTo, field.getName(), value);
			} else if (field.isAnnotationPresent(ManyToOne.class)) {
				Object childTo = PropertyUtils.getSimpleProperty(copyTo, fieldName);
				Object childFrom = PropertyUtils.getSimpleProperty(copyFrom, fieldName);

				copyAttributes(childTo, childFrom);
			} else if (field.isAnnotationPresent(RamailoList.class)) {
				List<?> copyToChildren = (List<?>) PropertyUtils.getSimpleProperty(copyTo, fieldName);
				List<?> copyFromChildren = (List<?>) PropertyUtils.getSimpleProperty(copyFrom, fieldName);

				List tempList = new ArrayList();

				/**
				 * 1.1 Iterate over all elements from source
				 * 2.1 If the item exists in destination collection, copy attributes from src to dest
				 * 2.2 Add the item from dest to a temp list 
				 * 3.1 If the item does not exist, add the item from src to a temp collection
				 * 4.1 Clear the dest collection
				 * 4.2 Add all items from temp collection to dest collection
				 */
				for (Object copyFromChild : copyFromChildren) {
					Object copyToChild = find(copyToChildren, copyFromChild);
					if (copyToChild != null) {
						copyAttributes(copyToChild, copyFromChild);
						tempList.add(copyToChild);
					} else {
						tempList.add(copyFromChild);
					}

				}

				copyToChildren.clear();
				copyToChildren.addAll(tempList);
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
