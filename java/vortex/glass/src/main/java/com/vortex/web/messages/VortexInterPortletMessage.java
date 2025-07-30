package com.vortex.web.messages;

import java.util.Set;

import com.f1.suite.web.portal.InterPortletMessage;

public class VortexInterPortletMessage implements InterPortletMessage {

	final private Set<?> removed;
	final private Set<?> added;
	final private Set<?> selected;

	final private String type;
	final Class datatype;

	public VortexInterPortletMessage(String type, Long datatype, Set<Long> removed, Set<Long> added, Set<Long> selected) {
		this.removed = removed;
		this.added = added;
		this.selected = selected;
		this.type = type;
		this.datatype = Long.class;

	}
	public VortexInterPortletMessage(String type, String datatype, Set<String> removed, Set<String> added, Set<String> selected) {
		this.removed = removed;
		this.added = added;
		this.selected = selected;
		this.type = type;
		this.datatype = String.class;
	}
	public Set<?> getRemoved() {
		return removed;
	}

	public Set<?> getAdded() {
		return added;
	}
	public Set<?> getSelected() {
		return selected;
	}
	public String getType() {
		return type;
	}
	public Class getClassType() {
		return datatype;
	}
}
