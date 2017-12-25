package com.ramailo.service;

import javax.persistence.EntityManager;

public abstract class BaseAction<T> {

	protected final T entity;
	protected EntityManager em;

	public BaseAction(T entity) {
		this.entity = entity;
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
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
