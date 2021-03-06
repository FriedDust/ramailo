package com.ramailo.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.ramailo.meta.Action;
import com.ramailo.meta.Argument;
import com.ramailo.meta.Attribute;
import com.ramailo.meta.AutoPkAttribute;
import com.ramailo.meta.ListAttribute;
import com.ramailo.meta.Resource;
import com.ramailo.meta.annotation.RamailoAction;
import com.ramailo.meta.annotation.RamailoArg;
import com.ramailo.meta.annotation.RamailoField;
import com.ramailo.meta.annotation.RamailoList;
import com.ramailo.meta.annotation.RamailoResource;
import com.ramailo.meta.validation.Mandatory;
import com.ramailo.meta.validation.Size;
import com.ramailo.meta.validation.Validation;
import com.ramailo.util.StringUtility;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class MetaBuilder {
	private Class<?> clazz;

	public MetaBuilder(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Resource read() {
		Resource resource = new Resource();

		RamailoResource resourceAnnotation = readResourceAnnotation();
		String resourceName = resourceAnnotation.value();
		List<Attribute> attributes = readAttributes();
		List<Action> actions = readActions();
		List<Action> staticActions = readStaticActions();

		resource.setName(resourceName);
		resource.setType(clazz.getSimpleName());
		resource.setLabel(StringUtility.labelize(resourceName));
		resource.setStringify(resourceAnnotation.stringify());
		resource.setAttributes(attributes);
		resource.setActions(actions);
		resource.setStaticActions(staticActions);
		resource.setGridHeaders(resourceAnnotation.gridHeaders());

		return resource;
	}

	private Map<String, Validation> readValidations(Field field) {
		Map<String, Validation> validations = new HashMap<>();

		if (field.isAnnotationPresent(NotNull.class)) {
			Mandatory m = new Mandatory(true);
			validations.put(m.getValidationName(), m);
		}

		if (field.isAnnotationPresent(javax.validation.constraints.Size.class)) {
			javax.validation.constraints.Size annotation = field.getAnnotation(javax.validation.constraints.Size.class);
			Size size = new Size();
			size.setMax(annotation.max());
			size.setMin(annotation.min());
			
			validations.put(size.getValidationName(), size);
		}

		return validations;
	}

	private List<Argument> readArguments(Method method) {
		List<Argument> args = new ArrayList<>();

		for (Parameter param : method.getParameters()) {
			Argument arg = new Argument();
			RamailoArg annotation = param.getAnnotation(RamailoArg.class);

			String label = annotation.label();
			label = label.isEmpty() ? StringUtility.labelize(annotation.name()) : label;

			arg.setName(annotation.name());
			arg.setLabel(label);
			arg.setType(param.getType().getSimpleName());

			args.add(arg);
		}

		return args;
	}

	private List<Action> readActions() {
		List<Action> actions = new ArrayList<>();

		Class<? extends BaseAction<?>> actionClass[] = clazz.getAnnotation(RamailoResource.class).actions();
		if (actionClass.length == 0)
			return actions;

		for (Method method : actionClass[0].getMethods()) {
			if (!Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(RamailoAction.class)) {
				RamailoAction annotation = method.getAnnotation(RamailoAction.class);

				String label = annotation.label();
				label = label.isEmpty() ? StringUtility.labelize(method.getName()) : label;

				String pathName = annotation.pathName();
				pathName = pathName.isEmpty() ? method.getName() : pathName;

				Action action = new Action();
				action.setName(method.getName());
				action.setPathName(pathName);
				action.setMethodType(annotation.methodType());
				action.setLabel(label);
				action.setArguments(readArguments(method));
				action.setStaticMethod(false);

				actions.add(action);
			}
		}
		return actions;
	}

	private List<Action> readStaticActions() {
		List<Action> actions = new ArrayList<>();

		Class<? extends BaseAction<?>> actionClass[] = clazz.getAnnotation(RamailoResource.class).actions();
		if (actionClass.length == 0)
			return actions;

		for (Method method : actionClass[0].getMethods()) {
			if (Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(RamailoAction.class)) {
				RamailoAction annotation = method.getAnnotation(RamailoAction.class);

				String label = annotation.label();
				label = label.isEmpty() ? StringUtility.labelize(method.getName()) : label;

				String pathName = annotation.pathName();
				pathName = pathName.isEmpty() ? method.getName() : pathName;

				Action action = new Action();
				action.setName(method.getName());
				action.setPathName(pathName);
				action.setMethodType(annotation.methodType());
				action.setLabel(label);
				action.setArguments(readArguments(method));
				action.setStaticMethod(true);

				actions.add(action);
			}
		}
		return actions;
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

			Map<String, Validation> validations = readValidations(field);
			attribute.setValidations(validations);

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
