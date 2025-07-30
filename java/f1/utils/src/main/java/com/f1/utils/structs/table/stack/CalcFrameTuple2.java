package com.f1.utils.structs.table.stack;

import com.f1.base.CalcFrame;

public class CalcFrameTuple2 implements CalcFrame {

	private CalcFrame f0, f1;
	private CalcTypesTuple2 types;

	public CalcFrameTuple2(CalcFrame f0, CalcFrame f1) {
		this.f0 = f0;
		this.f1 = f1;
		this.types = new CalcTypesTuple2(f0, f1);
	}

	@Override
	public Object getValue(String key) {
		if (f0.getType(key) != null)
			return f0.getValue(key);
		return f1.getValue(key);
	}

	@Override
	public Object putValue(String key, Object value) {
		throw new UnsupportedOperationException();
	}

	public void setFrame(CalcFrame f0, CalcFrame f1) {
		this.f0 = f0;
		this.f1 = f1;
		this.types.setTypes(f0, f1);
	}

	public void setFrame1(CalcFrame f1) {
		this.f1 = f1;
		this.types.setType1(f1);
	}

	public void setFrame0(CalcFrame f0) {
		this.f0 = f0;
		this.types.setType0(f0);
	}

	@Override
	public Class<?> getType(String key) {
		return this.types.getType(key);
	}

	@Override
	public boolean isVarsEmpty() {
		return this.types.isVarsEmpty();
	}

	@Override
	public Iterable<String> getVarKeys() {
		return this.types.getVarKeys();
	}

	@Override
	public int getVarsCount() {
		return this.types.getVarsCount();
	}

	public String toString() {
		return "{" + f0 + "," + f1 + "}";
	}
}
