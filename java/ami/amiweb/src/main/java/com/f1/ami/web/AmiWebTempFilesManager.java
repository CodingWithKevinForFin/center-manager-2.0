package com.f1.ami.web;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.f1.suite.web.WebStatesManager;

public class AmiWebTempFilesManager {

	private static final String ATTRIBUTE_ID = "TMP_FILE_MANAGER";
	private ConcurrentMap<String, String> layouts = new ConcurrentHashMap<String, String>();

	public static AmiWebTempFilesManager getTempFilesManager(WebStatesManager manager) {
		Object r = manager.getAttributes().get(ATTRIBUTE_ID);
		if (r == null) {
			Object t = manager.getAttributes().putIfAbsent(ATTRIBUTE_ID, r = new AmiWebTempFilesManager());
			if (t != null)
				r = t;
		}
		return (AmiWebTempFilesManager) r;
	}

	public String getLayout(String name) {
		return this.layouts.get(name);
	}

	public String putLayout(String name, String json) {
		return this.layouts.put(name, json);
	}

	public Set<String> getLayoutNames() {
		return this.layouts.keySet();
	}
	public String removeLayout(String name) {
		return this.layouts.remove(name);
	}
}
