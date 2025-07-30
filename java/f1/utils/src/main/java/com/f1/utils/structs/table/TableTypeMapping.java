package com.f1.utils.structs.table;

import java.util.ArrayList;

import com.f1.base.Column;
import com.f1.base.NameSpaceCalcTypes;
import com.f1.base.NameSpaceIdentifier;
import com.f1.base.Table;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class TableTypeMapping implements NameSpaceCalcTypes {

	private Table table;
	ArrayList<Tuple2<String, Class<?>>> r = null;

	public TableTypeMapping(Table table) {
		this.table = table;
	}

	@Override
	public String toString() {
		return SH.join(", ", getVarKeys(), new StringBuilder("{")).append("}").toString();
	}

	@Override
	public boolean isVarsEmpty() {
		return false;
	}

	@Override
	public Iterable<String> getVarKeys() {
		return table.getColumnsMap().keySet();
	}

	@Override
	public Class<?> getType(String key) {
		Column col = table.getColumnsMap().get(key);
		return col == null ? null : col.getType();
	}

	@Override
	public int getVarsCount() {
		return table.getColumnsCount();
	}

	@Override
	public Class<?> getType(NameSpaceIdentifier key) {
		if (OH.eq(table.getTitle(), key.getNamespace()))
			return getType(key.getVarName());
		return null;
	}

	@Override
	public int getPosition(String key) {
		Column col = table.getColumnsMap().get(key);
		return col == null ? -1 : col.getLocation();
	}

	@Override
	public int getPosition(NameSpaceIdentifier key) {
		if (OH.eq(table.getTitle(), key.getNamespace()))
			return getPosition(key.getVarName());
		return -1;
	}

	@Override
	public Class<?> getTypeAt(int n) {
		return table.getColumnAt(n).getType();
	}

}
