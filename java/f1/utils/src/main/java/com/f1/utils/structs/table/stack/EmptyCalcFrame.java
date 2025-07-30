package com.f1.utils.structs.table.stack;

import com.f1.base.CalcFrame;
import com.f1.utils.EmptyIterable;

public class EmptyCalcFrame implements CalcFrame {

	public static final EmptyCalcFrame INSTANCE = new EmptyCalcFrame();

	public EmptyCalcFrame() {
	}

	@Override
	public Object getValue(String key) {
		return null;
	}

	@Override
	public Object putValue(String key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> getType(String key) {
		return null;
	}

	@Override
	public boolean isVarsEmpty() {
		return true;
	}

	@Override
	public Iterable<String> getVarKeys() {
		return EmptyIterable.INSTANCE;
	}

	@Override
	public int getVarsCount() {
		return 0;
	}

	@Override
	public String toString() {
		return "{}";
	}
}
