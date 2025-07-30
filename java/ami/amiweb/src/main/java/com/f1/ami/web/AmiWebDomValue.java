package com.f1.ami.web;

import java.util.ArrayList;
import java.util.List;

public abstract class AmiWebDomValue implements AmiWebDomObject {
	private final AmiWebDomObject parentDomObj;
	private final String domValueKey;
	private final Class<?> domValueType;

	public AmiWebDomValue(AmiWebDomObject parent, String key, Class<?> type) {
		this.parentDomObj = parent;
		this.domValueKey = key;
		this.domValueType = type;
	}
	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return new ArrayList<AmiWebDomObject>();
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.parentDomObj;
	}

	@Override
	public Class<?> getDomClassType() {
		return this.domValueType;
	}

	public String getDomValueKey() {
		return domValueKey;
	}

	@Override
	public boolean isTransient() {
		return false;
	}
	@Override
	public void setTransient(boolean isTransient) {
		throw new UnsupportedOperationException("Invalid operation");
	}

}
