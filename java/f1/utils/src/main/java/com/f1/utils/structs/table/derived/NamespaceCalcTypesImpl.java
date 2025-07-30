package com.f1.utils.structs.table.derived;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.CalcTypes;
import com.f1.base.NameSpaceIdentifier;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.base.NameSpaceCalcTypes;

public class NamespaceCalcTypesImpl implements NameSpaceCalcTypes {

	private BasicCalcTypes inner = new BasicCalcTypes();

	private String name;
	private CalcTypes value;
	private Map<String, CalcTypes> more;

	public void addNamespace(String name, CalcTypes value) {
		if (this.name == null) {
			this.name = name;
			this.value = value;
		} else {
			if (more == null)
				more = new HashMap<String, CalcTypes>();
			more.put(name, value);
		}
	}

	@Override
	public Class<?> getType(String key) {
		return inner.getType(key);
	}

	@Override
	public boolean isVarsEmpty() {
		return inner.isVarsEmpty();
	}

	@Override
	public Iterable<String> getVarKeys() {
		return inner.getVarKeys();
	}

	public void putType(String key, Class<?> caster) {
		inner.putType(key, caster);
	}

	@Override
	public String toString() {
		return inner.toString();
	}

	@Override
	public int getVarsCount() {
		return inner.getVarsCount();
	}

	public void putAll(CalcTypes types) {
		this.inner.putAll(types);
	}

	@Override
	public Class<?> getType(NameSpaceIdentifier key) {
		if (name.equals(key.getNamespace()))
			return value.getType(key.getVarName());
		if (more != null) {
			CalcTypes t = more.get(key.getNamespace());
			if (t != null)
				return t.getType(key.getVarName());
		}
		return null;
	}

	@Override
	public Class<?> getTypeAt(int n) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPosition(String key) {
		return -1;
	}

	@Override
	public int getPosition(NameSpaceIdentifier key) {
		return -1;
	}
}
