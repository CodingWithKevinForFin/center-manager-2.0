package com.f1.utils.structs.table.derived;

import com.f1.base.Row;
import com.f1.utils.structs.IntKeyMap;

public class AggregateRow extends DerivedRow {

	public static final Object NOT_AGGEGATED = new Object() {
	};
	private IntKeyMap<Row> constituents = new IntKeyMap<Row>();
	private AggregateTable atable;
	private Object[] groupingKey;

	public AggregateRow(AggregateTable table, int uid, Object[] values, boolean needsCheck) {
		super(table, uid, values, needsCheck);
		this.atable = table;
	}

	public IntKeyMap<Row> getConstituents() {
		return constituents;
	}

	protected void addConstituent(Row row) {
		constituents.put(row.getUid(), row);
	}

	public void removeConstituent(Row row) {
		constituents.remove(row.getUid());
	}

	public void updateValue(int cell, Object oldValue, Object newValue, AggregateColumn column) {
		Object val = values[cell];
		if (newValue == NOT_CACHED)
			newValue = super.getAt(cell);
		Object value = column.calculate(val, oldValue, newValue);
		if (value == NOT_AGGEGATED)
			value = column.recalc(getConstituents().values());//TODO: be lazy on this!
		super.putAt(cell, value);
	}
	// DON'T MODIFY CONTENTS!
	public Object[] getValues() {
		Object[] r = super.getValues();
		for (AggregateColumn col : atable.getAggregateColumns())
			if (r[col.getLocation()] == NOT_AGGEGATED)
				r[col.getLocation()] = col.recalc(getConstituents().values());
		return r;
	}
	public Object getAt(int i) {
		if (values[i] == NOT_AGGEGATED) {
			AggregateColumn col = (AggregateColumn) atable.getColumnAt(i);
			return values[i] = col.recalc(getConstituents().values());
		} else
			return super.getAt(i);
	}

	@Override
	public Object putAt(int i, Object value) {
		if (value == NOT_AGGEGATED) {
			Object r = values[i];
			values[i] = value;
			removeCache(i);
			return r;
		} else
			return super.putAt(i, value);
	}

	public Object[] getGroupingKey() {
		return groupingKey;
	}

	public void setKey(Object[] key) {
		this.groupingKey = key;
	}

}
