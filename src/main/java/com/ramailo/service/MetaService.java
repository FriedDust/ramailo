package com.ramailo.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.ramailo.RamailoResource;
import com.ramailo.meta.Annotation;
import com.ramailo.meta.Attribute;
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

		String resourceName = readResourceName();
		List<Attribute> attributes = readAttributes();

		resource.setName(resourceName);
		resource.setLabel(StringUtility.labelize(resourceName));
		resource.setAttributes(attributes);

		return resource;
	}

	private List<Attribute> readAttributes() {
		List<Attribute> attributes = new ArrayList<>();
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			
			Attribute attribute = readAttribute(field);

			attributes.add(attribute);
		}

		return attributes;
	}

	private Attribute readAttribute(Field field) {
		Attribute attribute = new Attribute();

		attribute.setName(field.getName());
		attribute.setLabel(StringUtility.labelize(field.getName()));
		attribute.setType(field.getType().getSimpleName());
		
		attribute.setAnnotations(readAttributeAnnotations(field));

		return attribute;
	}

	private List<Annotation> readAttributeAnnotations(Field field) {
		List<Annotation> annotations = new ArrayList<>();

		return annotations;
	}

	private String readResourceName() {
		return clazz.getAnnotation(RamailoResource.class).value();
	}
}
