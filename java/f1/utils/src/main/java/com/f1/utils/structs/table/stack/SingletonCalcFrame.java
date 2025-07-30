package com.f1.utils.structs.table.stack;

import com.f1.base.CalcFrame;

public class SingletonCalcFrame implements CalcFrame {

	private SingletonCalcTypes types;
	private Object value;

	public SingletonCalcFrame(String key, Class<?> type) {
		this.types = new SingletonCalcTypes(key, type);
	}
	public SingletonCalcFrame(String key, Class<?> type, Object value) {
		this(key, type);
		this.value = value;
	}

	public SingletonCalcFrame(SingletonCalcTypes var, Object val) {
		this.types = var;
		this.value = val;
	}
	public void setValue(Object o) {
		this.value = o;
	}

	public String getKey() {
		return types.getKey();
	}

	@Override
	public Object getValue(String key) {
		if (this.types.getKey().equals(key))
			return value;
		return null;
	}

	@Override
	public Object putValue(String key, Object value) {
		if (this.types.getKey().equals(key))
			this.value = value;
		return null;
	}
	@Override
	public Class<?> getType(String key) {
		return types.getType(key);
	}
	@Override
	public boolean isVarsEmpty() {
		return types.isVarsEmpty();
	}
	@Override
	public Iterable<String> getVarKeys() {
		return types.getVarKeys();
	}
	@Override
	public int getVarsCount() {
		return types.getVarsCount();
	}

	@Override
	public String toString() {
		return "{" + types.getKey() + "=" + value + "}";
	}

}
