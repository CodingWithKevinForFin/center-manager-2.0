package com.f1.utils.structs.table.stack;

import java.util.Set;

import com.f1.base.CalcTypes;
import com.f1.utils.concurrent.HasherSet;

public class CalcTypesTuple2 implements CalcTypes {

	private CalcTypes f0, f1;
	private Set<String> keys = null;

	public CalcTypesTuple2(CalcTypes f0, CalcTypes f1) {
		this.f0 = f0;
		this.f1 = f1;
	}

	@Override
	public boolean isVarsEmpty() {
		return f0.isVarsEmpty() && f1.isVarsEmpty();
	}

	@Override
	public Iterable<String> getVarKeys() {
		ensureKeysBuilt();
		return keys;
	}

	private void ensureKeysBuilt() {
		if (keys != null)
			return;
		keys = new HasherSet<String>();
		for (String s : f0.getVarKeys())
			keys.add(s);
		for (String s : f1.getVarKeys())
			keys.add(s);
	}

	public void setTypes(CalcTypes f0, CalcTypes f1) {
		this.f0 = f0;
		this.f1 = f1;
		this.keys = null;
	}

	public void setType1(CalcTypes f1) {
		this.f1 = f1;
		this.keys = null;
	}

	public void setType0(CalcTypes f0) {
		this.f0 = f0;
		this.keys = null;
	}

	@Override
	public Class<?> getType(String key) {
		final Class<?> r = f0.getType(key);
		return r != null ? r : f1.getType(key);
	}

	@Override
	public int getVarsCount() {
		ensureKeysBuilt();
		return keys.size();
	}
}
