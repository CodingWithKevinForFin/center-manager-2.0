package com.f1.ami.sim;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Getter;

public class AmiSimObject implements Getter<String, Object> {

	final private String id;
	final private long expires;
	final private String type;
	final private Map<String, Object> params = new HashMap<String, Object>();

	final private Map<String, AmiSimObject> references = new HashMap<String, AmiSimObject>();

	public AmiSimObject(String type, String id, long expires) {
		this.type = type;
		this.id = id;
		this.expires = expires;
	}

	public String getId() {
		return id;
	}

	public long getExpires() {
		return expires;
	}

	public String getType() {
		return type;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public AmiSimObject getReference(String type) {
		return references.get(type);
	}

	public void addReference(AmiSimObject ref) {
		this.references.put(ref.getType(), ref);
	}

	@Override
	public Object get(String key) {
		return params.get(key);
	}

}
