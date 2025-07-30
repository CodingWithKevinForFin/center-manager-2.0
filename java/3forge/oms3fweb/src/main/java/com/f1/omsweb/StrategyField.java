package com.f1.omsweb;

public class StrategyField {
	private String name;
	private int[] tags;

	public StrategyField(String name, int[] tags) {
		this.name = name;
		this.tags = tags;
	}

	public String getName() {
		return name;
	}

	public int[] getTags() {
		return tags;
	}
}
