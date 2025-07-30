package com.f1.ami.web;

import java.util.Arrays;
import java.util.HashMap;

import com.f1.utils.SH;

public class AmiWebObject_FeedPositions implements Cloneable {

	final private HashMap<String, Integer> paramPositions;
	private String[] paramNames;
	final private String type;

	public AmiWebObject_FeedPositions(String type) {
		this.paramPositions = new HashMap<String, Integer>();
		paramNames = new String[64];
		this.type = type;
	}
	public AmiWebObject_FeedPositions(AmiWebObject_FeedPositions other) {
		this.paramPositions = (HashMap<String, Integer>) other.paramPositions.clone();
		this.paramNames = other.paramNames.clone();
		this.type = other.type;
	}

	public int getParamPosition(String param) {
		Integer i = paramPositions.get(param);
		if (i != null)
			return i.intValue();
		int r = paramPositions.size();
		paramPositions.put(param, r);
		if (r == paramNames.length)
			paramNames = Arrays.copyOf(paramNames, paramNames.length << 1);
		paramNames[r] = param;
		return r;
	}

	public int getParamsCount() {
		return this.paramPositions.size();
	}

	public String getParamName(int i) {
		return paramNames[i];
	}

	public String getTypeName() {
		return this.type;
	}

	@Override
	public String toString() {
		return "FeedPositions: " + type + "[" + SH.join(',', paramNames) + "]";
	}
}
