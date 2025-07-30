package com.f1.utils;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.Transient;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;

public class MapBackedValuedSchema implements ValuedSchema {

	final private Class type;
	final private String[] params;
	final private Map<String, ValuedParam> valuedParams;
	final private ValuedParam[] valuedParamsArray;

	public MapBackedValuedSchema(Class type, Map<String, Class> data) {
		final int len = data.size();
		this.type = type;
		this.params = new String[len];
		this.valuedParams = new HashMap<String, ValuedParam>(len);
		this.valuedParamsArray = new ValuedParam[len];
		int loc = 0;
		for (Map.Entry<String, Class> e : data.entrySet()) {
			final String name = e.getKey();
			final Class returnType = e.getValue();
			final ValuedParam<Valued> vp = new BasicValuedParam<Valued>(name, returnType, Transient.NONE, Valued.NO_PID, loc);
			params[loc] = name;
			valuedParamsArray[loc] = vp;
			valuedParams.put(name, vp);
			loc++;
		}
	}

	@Override
	public Class askOriginalType() {
		return type;
	}

	@Override
	public int askParamsCount() {
		return params.length;
	}

	@Override
	public boolean askSupportsPids() {
		return false;
	}

	@Override
	public String[] askParams() {
		return params;
	}

	@Override
	public ValuedParam[] askValuedParams() {
		return valuedParamsArray;
	}

	@Override
	public byte[] askPids() {
		throw new RuntimeException("pids not supported");
	}

	@Override
	public Class askClass(String name_) {
		return valuedParams.get(name_).getReturnType();
	}

	@Override
	public byte askBasicType(String name_) {
		return OH.getBasicType(askClass(name_));
	}

	@Override
	public ValuedParam askValuedParam(String name) {
		return valuedParams.get(name);
	}

	@Override
	public int askPosition(String name) {
		return askValuedParam(name).askPosition();
	}

	@Override
	public boolean askParamValid(String param_) {
		return valuedParams.containsKey(param_);
	}

	@Override
	public Class askClass(byte pid_) {
		throw new RuntimeException("pids not supported");
	}

	@Override
	public byte askBasicType(byte pid_) {
		throw new RuntimeException("pids not supported");
	}

	@Override
	public ValuedParam askValuedParam(byte pid_) {
		throw new RuntimeException("pids not supported");
	}

	@Override
	public int askPosition(byte pid_) {
		throw new RuntimeException("pids not supported");
	}

	@Override
	public boolean askPidValid(byte pid_) {
		throw new RuntimeException("pids not supported");
	}

	@Override
	public byte askPid(String param_) {
		throw new RuntimeException("pids not supported");
	}

	@Override
	public String askParam(byte pid_) {
		throw new RuntimeException("pids not supported");
	}

	@Override
	public String toString() {
		return OH.getSimpleName(type) + " " + valuedParams;
	}

}
