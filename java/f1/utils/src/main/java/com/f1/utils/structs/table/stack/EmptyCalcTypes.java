package com.f1.utils.structs.table.stack;

import com.f1.base.CalcTypes;
import com.f1.base.NameSpaceIdentifier;
import com.f1.utils.EmptyIterable;
import com.f1.base.NameSpaceCalcTypes;

public class EmptyCalcTypes implements CalcTypes, NameSpaceCalcTypes {

	public static final EmptyCalcTypes INSTANCE = new EmptyCalcTypes();

	private EmptyCalcTypes() {
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
	public Class<?> getType(String key) {
		return null;
	}

	@Override
	public int getVarsCount() {
		return 0;
	}

	@Override
	public String toString() {
		return "{}";
	}

	@Override
	public Class<?> getType(NameSpaceIdentifier key) {
		return null;
	}

	@Override
	public int getPosition(String key) {
		return -1;
	}

	@Override
	public int getPosition(NameSpaceIdentifier key) {
		return -1;
	}

	@Override
	public Class<?> getTypeAt(int n) {
		throw new IndexOutOfBoundsException("" + n);
	}

}
