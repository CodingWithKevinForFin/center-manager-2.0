package com.f1.ami.web.cloud;

import java.util.Map;

public class AmiWebCloudLayoutTree {
	final private String name;
	final private Map<String, String> layouts;
	final private Map<String, AmiWebCloudLayoutTree> children;
	final private String id;

	public AmiWebCloudLayoutTree(String id, String name, Map<String, String> layouts, Map<String, AmiWebCloudLayoutTree> children) {
		this.id = id;
		this.name = name;
		this.layouts = layouts;
		this.children = children;
	}

	public Map<String, AmiWebCloudLayoutTree> getChildren() {
		return children;
	}

	public Map<String, String> getLayoutNamesAndId() {
		return layouts;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

}
