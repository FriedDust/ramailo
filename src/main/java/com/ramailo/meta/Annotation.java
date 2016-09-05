package com.ramailo.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class Annotation {

	private String name;
	private List<AnnotationProperty> properties = new ArrayList<>();

	public List<AnnotationProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<AnnotationProperty> properties) {
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
