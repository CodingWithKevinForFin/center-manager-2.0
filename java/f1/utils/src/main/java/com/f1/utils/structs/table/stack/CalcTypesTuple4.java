package com.f1.utils.structs.table.stack;

import java.util.Set;

import com.f1.base.CalcTypes;
import com.f1.utils.concurrent.HasherSet;

public class CalcTypesTuple4 implements CalcTypes {

	private CalcTypes f0, f1, f2, f3;
	private Set<String> keys = null;

	public CalcTypesTuple4(CalcTypes f0, CalcTypes f1, CalcTypes f2, CalcTypes f3) {
		this.f0 = f0;
		this.f1 = f1;
		this.f2 = f2;
		this.f3 = f3;
	}

	@Override
	public boolean isVarsEmpty() {
		return f0.isVarsEmpty() && f1.isVarsEmpty() && f2.isVarsEmpty() && f3.isVarsEmpty();
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
		for (String s : f2.getVarKeys())
			keys.add(s);
		for (String s : f3.getVarKeys())
			keys.add(s);
	}

	public void setTypes(CalcTypes f0, CalcTypes f1, CalcTypes f2, CalcTypes f3) {
		this.f0 = f0;
		this.f1 = f1;
		this.f2 = f2;
		this.f3 = f3;
		this.keys = null;
	}

	public void setType3(CalcTypes f3) {
		this.f3 = f3;
		this.keys = null;
	}
	public void setType2(CalcTypes f2) {
		this.f2 = f2;
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
		Class<?> r = f0.getType(key);
		if (r != null)
			return r;
		r = f1.getType(key);
		if (r != null)
			return r;
		r = f2.getType(key);
		if (r != null)
			return r;
		return f3.getType(key);
	}

	@Override
	public int getVarsCount() {
		ensureKeysBuilt();
		return keys.size();
	}
}
