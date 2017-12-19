package com.ramailo.service;

public abstract class BaseActions<T> {

	protected final T entity;
	
	public BaseActions(T entity) {
		this.entity = entity;
	}

	public T getEntity() {
		return entity;
	}

	public void onBeforeSave() {
	}

	public void onSave() {
	}

	public void onBeforeDelete() {
	}

	public void onDelete() {
	}
}
