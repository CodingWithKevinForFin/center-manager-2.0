package com.f1.utils.structs.table.derived;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.structs.table.BasicColumn;

public abstract class AggregateColumn extends BasicColumn {

	private int innerLocation;
	final private String innerId;

	public AggregateColumn(Table table, int uid, int location, Class<?> type, String id, String innerId) {
		super(table, uid, location, type, id);
		this.innerId = innerId;
		this.innerLocation = -1;
	}

	public abstract Object calculate(Object val, Object oldValue, Object newValue);

	public int getInnerColumnLocation() {
		return innerLocation;
	}
	public void setInnerColumnLocation(int innerLocation) {
		this.innerLocation = innerLocation;
	}

	public String getInnerColumnId() {
		return innerId;
	}

	public Object recalc(Iterable<Row> innerRows) {
		Object r = null;
		for (Row row : innerRows)
			r = calculate(r, null, row.getAt(innerLocation));
		return r;
	}

	abstract public String getMethodName();

	public StringBuilder toCalcString(StringBuilder sb) {
		return sb.append(getMethodName()).append('(').append(innerId).append(')');
	}

}
