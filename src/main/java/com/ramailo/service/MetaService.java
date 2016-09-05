package com.ramailo.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.ramailo.RamailoResource;
import com.ramailo.meta.Annotation;
import com.ramailo.meta.AnnotationProperty;
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
		resource.setAnnotations(readAnnotations());

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

		attribute.setAnnotations(readAnnotations(field));

		return attribute;
	}

	private List<Annotation> readAnnotations() {
		List<Annotation> annotations = new ArrayList<>();

		for (java.lang.annotation.Annotation javaLangAnnotation : clazz.getDeclaredAnnotations()) {
			Annotation annotation = new Annotation();
			annotation.setName(javaLangAnnotation.annotationType().getSimpleName());
			annotation.setProperties(readAnnotationProperties(javaLangAnnotation));

			annotations.add(annotation);
		}

		return annotations;
	}

	private List<Annotation> readAnnotations(Field field) {
		List<Annotation> annotations = new ArrayList<>();

		for (java.lang.annotation.Annotation javaLangAnnotation : field.getDeclaredAnnotations()) {
			Annotation annotation = new Annotation();
			annotation.setName(javaLangAnnotation.annotationType().getSimpleName());
			annotation.setProperties(readAnnotationProperties(javaLangAnnotation));

			annotations.add(annotation);
		}

		return annotations;
	}

	private List<AnnotationProperty> readAnnotationProperties(java.lang.annotation.Annotation javaLangAnnotation) {
		List<AnnotationProperty> properties = new ArrayList<>();
		
		for (Method method : javaLangAnnotation.annotationType().getDeclaredMethods()) {
			AnnotationProperty property = new AnnotationProperty();
			property.setProperty(method.getName());

			try {
				Object value = method.invoke(javaLangAnnotation, (Object[]) null);
				property.setValue(value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}

			properties.add(property);
		}
		
		return properties;
	}

	private String readResourceName() {
		return clazz.getAnnotation(RamailoResource.class).value();
	}
}
