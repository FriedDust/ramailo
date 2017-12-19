package com.ramailo.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Kailash Bijayananda <fried.dust@gmail.com>
 *
 */
public class Resource implements Serializable {

	private static final long serialVersionUID = -8665911458426136608L;

	private String name;
	private String label;
	private String type;
	private String stringify;
	private String[] gridHeaders;

	private List<Attribute> attributes = new ArrayList<>();
	private List<Action> actions = new ArrayList<>();
	private List<Action> staticActions = new ArrayList<>();

	public List<Action> getStaticActions() {
		return staticActions;
	}

	public void setStaticActions(List<Action> staticActions) {
		this.staticActions = staticActions;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public String[] getGridHeaders() {
		return gridHeaders;
	}

	public void setGridHeaders(String[] gridHeaders) {
		this.gridHeaders = gridHeaders;
	}

	public String getStringify() {
		return stringify;
	}

	public void setStringify(String stringify) {
		this.stringify = stringify;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
}
