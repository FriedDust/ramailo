package com.ramailo.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.ramailo.annotation.RamailoField;
import com.ramailo.annotation.RamailoList;
import com.ramailo.annotation.RamailoResource;
import com.ramailo.meta.Attribute;
import com.ramailo.meta.AutoPkAttribute;
import com.ramailo.meta.ListAttribute;
import com.ramailo.meta.Resource;
import com.ramailo.util.StringUtility;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class MetaService {
	private Class<?> clazz;

	public MetaService(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Resource read() {
		Resource resource = new Resource();

		RamailoResource resourceAnnotation = readResourceAnnotation();
		String resourceName = resourceAnnotation.value();
		List<Attribute> attributes = readAttributes();

		resource.setName(resourceName);
		resource.setType(clazz.getSimpleName());
		resource.setLabel(StringUtility.labelize(resourceName));
		resource.setStringify(resourceAnnotation.stringify());
		resource.setAttributes(attributes);
		resource.setGridHeaders(resourceAnnotation.gridHeaders());

		return resource;
	}

	private List<Attribute> readAttributes() {
		List<Attribute> attributes = new ArrayList<>();
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;

			Attribute attribute = null;

			if (field.isAnnotationPresent(RamailoList.class)) {
				attribute = readRamailoList(field);

			} else if (field.isAnnotationPresent(RamailoField.class)) {
				if (field.isAnnotationPresent(Id.class)) {
					attribute = readRamailoPkField(field);
				} else {
					attribute = readRamailoField(field);
				}

			}
			attributes.add(attribute);
		}

		return attributes;
	}

	private Attribute readRamailoList(Field field) {
		ListAttribute attribute = new ListAttribute();
		RamailoList listAnnotation = field.getAnnotation(RamailoList.class);

		String name = field.getName();
		String label = listAnnotation.label();
		label = label.isEmpty() ? StringUtility.labelize(name) : label;

		attribute.setName(name);
		attribute.setLabel(label);
		attribute.setType(field.getType().getSimpleName());
		attribute.setChildrenType(listAnnotation.childrenType().getSimpleName());

		return attribute;
	}

	private Attribute readRamailoField(Field field) {
		Attribute attribute = new Attribute();
		RamailoField fieldAnnotation = field.getAnnotation(RamailoField.class);

		String name = field.getName();
		String label = fieldAnnotation.label();
		label = label.isEmpty() ? StringUtility.labelize(name) : label;

		attribute.setName(name);
		attribute.setLabel(label);
		attribute.setType(field.getType().getSimpleName());

		return attribute;
	}

	private Attribute readRamailoPkField(Field field) {
		AutoPkAttribute attribute = new AutoPkAttribute();
		Attribute attr = readRamailoField(field);

		if (field.isAnnotationPresent(GeneratedValue.class)
				&& field.getAnnotation(GeneratedValue.class).strategy().equals(GenerationType.IDENTITY)) {
			attribute.setAutoPk(true);
		}

		attribute.setLabel(attr.getLabel());
		attribute.setName(attr.getName());
		attribute.setType(attr.getType());

		return attribute;
	}

	private RamailoResource readResourceAnnotation() {
		return clazz.getAnnotation(RamailoResource.class);
	}
}
