package com.f1.utils.sql;

import java.util.Collections;
import java.util.List;

import com.f1.base.Table;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControl;

public class TableReturn implements FlowControl {

	public static final TableReturn EMPTY = new TableReturn(0);
	final private List<Object> generatedKeys;
	final private List<Table> tables;
	final private long rowsEffected;
	final private Class returnType;
	final private Object returnValue;

	public TableReturn(Table table) {
		this(Collections.singletonList(table), 0, null, null);
	}
	public TableReturn(List<Table> table) {
		this(table, 0, null, null);
	}

	public TableReturn(long rowsEffected, List<Object> generatedKeys) {
		this.tables = Collections.EMPTY_LIST;
		this.generatedKeys = generatedKeys;
		this.rowsEffected = rowsEffected;
		this.returnType = null;
		this.returnValue = null;
	}
	public TableReturn(long rowsEffected) {
		this.tables = Collections.EMPTY_LIST;
		this.generatedKeys = Collections.EMPTY_LIST;
		this.rowsEffected = rowsEffected;
		this.returnType = null;
		this.returnValue = null;
	}
	public TableReturn(List<Table> tables, long rowsEffected, Class returnType, Object returnValue) {
		this.tables = tables == null ? Collections.EMPTY_LIST : tables;
		this.generatedKeys = Collections.EMPTY_LIST;
		this.rowsEffected = rowsEffected;
		this.returnType = returnType;
		this.returnValue = returnValue;
	}
	public TableReturn(List<Table> tables, List<Object> generatedKeys, long rowsEffected, Class returnType, Object returnValue) {
		this.tables = tables == null ? Collections.EMPTY_LIST : tables;
		this.generatedKeys = generatedKeys == null ? Collections.EMPTY_LIST : generatedKeys;
		this.rowsEffected = rowsEffected;
		this.returnType = returnType;
		this.returnValue = returnValue;
	}
	public TableReturn() {
		this.tables = Collections.EMPTY_LIST;
		this.generatedKeys = Collections.EMPTY_LIST;
		this.rowsEffected = 0;
		this.returnType = null;
		this.returnValue = null;
	}

	public List<Table> getTables() {
		return tables;
	}

	public long getRowsEffected() {
		return rowsEffected;
	}
	public Class getReturnType() {
		return returnType;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public List<Object> getGenerateKeys() {
		return generatedKeys;
	}

	@Override
	public byte getType() {
		return STATEMENT_SQL;
	}

	@Override
	public DerivedCellCalculator getPosition() {
		return null;
	}

}
