/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Caster;
import com.f1.utils.CH;

public class WebWindowState {
	private Map<String, Object> attributes;
	private WebState webState;

	public void init(WebState webState) {
		this.webState = webState;
		attributes = new HashMap<String, Object>();
	}

	public <T> T getAttribute(String key, Class<T> type, T defaultValue) {
		return CH.getOr(type, getAttributes(), key, defaultValue);
	}
	public <T> T getAttribute(String key, Caster<T> caster, T defaultValue) {
		return CH.getOr(caster, getAttributes(), key, defaultValue);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public <T> T getAttribute(String key, Class<T> type) {
		return CH.getOrThrow(type, getAttributes(), key, "attribute");
	}
	public Object removeAttribute(String key) {
		return getAttributes().remove(key);
	}

	public WebState getWebState() {
		return webState;
	}

	public void setAttribute(String key, Object value) {
		this.attributes.put(key, value);
	}
	public void reset() {
		this.attributes.clear();
	}
}
