package com.ramailo.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import org.reflections.Reflections;

public class ClassFinder {

	private static final char PKG_SEPARATOR = '.';

	private static final char DIR_SEPARATOR = '/';

	private static final String CLASS_FILE_SUFFIX = ".class";

	private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

	private String packagePath;
	private Reflections reflections;

	public ClassFinder(String packagePath) {
		this.packagePath = packagePath;

		this.reflections = new Reflections(packagePath);
	}

	public Set<Class<?>> findHavingAnnotation(Class<? extends Annotation> annotation) {
		return this.reflections.getTypesAnnotatedWith(annotation);
	}
}

/*
	public List<Class<?>> findHavingAnnotation(Class<? extends Annotation> annotation) {
		return findHavingAnnotation(path, annotation);
	}

	public static List<Class<?>> find(String scannedPackage) {
		String scannedPath = scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
		System.out.println("scannedPath=" + scannedPath);
		URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
		if (scannedUrl == null) {
			throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
		}
		System.out.println("scannedUrl=" + scannedUrl);
		File scannedDir = new File(scannedUrl.getFile());
		System.out.println("scannedDir=" + scannedDir.listFiles());
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (File file : scannedDir.listFiles()) {
			classes.addAll(find(file, scannedPackage));
		}
		return classes;
	}

	public static List<Class<?>> findHavingAnnotation(String scannedPackage, Class<? extends Annotation> annotation) {
		List<Class<?>> classes = find(scannedPackage);
		return classes.stream().filter(cls -> cls.isAnnotationPresent(annotation)).collect(Collectors.toList());
	}

	private static List<Class<?>> find(File file, String scannedPackage) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		String resource = scannedPackage + PKG_SEPARATOR + file.getName();
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				classes.addAll(find(child, resource));
			}
		} else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
			int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
			String className = resource.substring(0, endIndex);
			try {
				classes.add(Class.forName(className));
			} catch (ClassNotFoundException ignore) {
			}
		}
		return classes;
	}
	*/