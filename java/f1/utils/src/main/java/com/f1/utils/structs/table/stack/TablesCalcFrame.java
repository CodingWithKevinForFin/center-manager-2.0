package com.f1.utils.structs.table.stack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.base.Column;
import com.f1.base.NameSpaceCalcFrame;
import com.f1.base.NameSpaceCalcTypes;
import com.f1.base.NameSpaceIdentifier;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.OH;

public class TablesCalcFrame implements NameSpaceCalcFrame, NameSpaceCalcTypes {

	private Map<Object, Integer> namesToPositions = new HashMap<Object, Integer>();//16 bits for table, 16 bits for col
	final public Row[] currentRows;
	final private Table[] tables;
	final private String[] tableNames;

	public TablesCalcFrame(String tableNames[], Table[] tables) {
		this.currentRows = new Row[tables.length];
		this.tableNames = tableNames;
		this.tables = tables;
	}

	public Row[] getRows() {
		return this.currentRows;
	}

	@Override
	public Object getValue(NameSpaceIdentifier key) {
		int pos = getPosition(key);
		if (pos != -1)
			return getAt(pos);
		return null;
	}

	@Override
	public Object getValue(String key) {
		int pos = getPosition(key);
		if (pos != -1)
			return getAt(pos);
		return null;
	}

	@Override
	public boolean isVarsEmpty() {
		return false;
	}

	@Override
	public Class<?> getType(String key) {
		int pos = getPosition(key);
		if (pos != -1)
			return getTypeAt(pos);
		return null;
	}

	@Override
	public Object putValue(String key, Object value) {
		throw new UnsupportedOperationException();
	}

	private Set<String> allVars;

	@Override
	public Set<String> getVarKeys() {
		if (allVars == null) {
			allVars = new HashSet<String>();
			for (Table i : tables) {
				for (String j : i.getColumnIds()) {
					//					allVars.add(i.getTitle() + "." + j);
					allVars.add(j);
				}
			}
		}
		return allVars;
	}

	@Override
	public int getVarsCount() {
		return getVarKeys().size();
	}

	@Override
	public Class<?> getType(NameSpaceIdentifier key) {
		int pos = getPosition(key);
		if (pos != -1)
			return getTypeAt(pos);
		return null;
	}

	@Override
	public int getPosition(String key) {
		Integer r = namesToPositions.get(key);
		if (r != null)
			return r;
		r = -1;
		for (int n = 0; n < tables.length; n++) {
			Column col = tables[n].getColumnsMap().get(key);
			if (col != null) {
				r = (n << 16) | col.getLocation();
				break;
			}
		}
		namesToPositions.put(key, r);
		return r;
	}

	@Override
	public int getPosition(NameSpaceIdentifier key) {
		Integer r = namesToPositions.get(key);
		if (r != null)
			return r;
		r = -1;
		for (int n = 0; n < tables.length; n++) {
			Table table = tables[n];
			if (OH.eq(tableNames[n], key.getNamespace())) {
				Column col = table.getColumnsMap().get(key.getVarName());
				if (col != null) {
					r = (n << 16) | col.getLocation();
					break;
				}
			}
		}
		namesToPositions.put(key, r);
		return r;
	}

	@Override
	public Object getAt(int value) {
		int rowOffset = value >> 16;
		int cellOffset = value & 65535;
		return getAt(rowOffset, cellOffset);
	}
	@Override
	public Class getTypeAt(int value) {
		int rowOffset = value >> 16;
		int cellOffset = value & 65535;
		return getTypeAt(rowOffset, cellOffset);
	}

	private Object getAt(int rowOffset, int cellOffset) {
		Row row = currentRows[rowOffset];
		return row == null ? null : row.getAt(cellOffset);
	}
	private Class<?> getTypeAt(int rowOffset, int cellOffset) {
		Table table = tables[rowOffset];
		return table.getColumnAt(cellOffset).getType();
	}
}
