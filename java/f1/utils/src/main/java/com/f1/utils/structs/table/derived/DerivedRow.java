package com.f1.utils.structs.table.derived;

import com.f1.base.Row;
import com.f1.utils.OH;
import com.f1.utils.structs.table.BasicRow;

public class DerivedRow extends BasicRow {

	public static final Object NOT_CACHED = new Object();

	private DerivedTable dtable;

	public DerivedRow(DerivedTable table, int uid, Object[] values, boolean needsCheck) {
		super(table, uid, values);
		this.dtable = table;
		if (needsCheck) {
			for (int loc : dtable.getDerivedColumns()) {
				if (values[loc] != null)
					throw new RuntimeException("values for derived column '" + dtable.getDerivedColumn(loc).getId() + "' must be null: " + values[loc]);
				values[loc] = NOT_CACHED;
			}
		}
	}

	public DerivedRow(DerivedTable table, int uid, Row row) {
		super(table, uid, row);
		this.dtable = table;
		for (int loc : dtable.getDerivedColumns())
			values[loc] = NOT_CACHED;
	}

	// DON'T MODIFY CONTENTS!
	public Object[] getValues() {
		Object[] r = super.getValues();
		dtable.getStackFrame().reset(this);
		for (int loc : dtable.getDerivedColumns())
			if (r[loc] == NOT_CACHED)
				r[loc] = dtable.getDerivedColumn(loc).getValue(dtable.getStackFrame());
		return r;
	}
	public Object[] getNonDerivedValues() {
		Object[] vals = super.getValues();
		Object[] r = new Object[vals.length - dtable.getDerivedColumns().length];
		for (int i = 0, j = 0; i < r.length; j++) {
			if (dtable.getDerivedColumn(j) == null)
				r[i++] = vals[j];
		}
		return r;
	}
	public Object getAt(int i) {
		Object r = values[i];
		if (r == NOT_CACHED)
			values[i] = r = dtable.getDerivedColumn(i).getValue(dtable.getStackFrame().reset(this));
		return r;
	}
	@Override
	public Object putAt(int i, Object value) {
		Object old = super.putAt(i, value);
		if (!OH.eq(old, value))
			removeCache(i);
		return old;
	}

	public void removeCache(int i) {
		int cols[] = dtable.getDependentColumns(i);
		if (cols != null) {
			for (int col : cols) {
				if (values[col] != NOT_CACHED) {
					Object old = values[col];
					values[col] = NOT_CACHED;
					if (dtable.hasListeners)
						dtable.fireCellChanged(this, col, old, NOT_CACHED);
					removeCache(col);
				}
			}
		}
	}
	public void setValues(Object[] values) {
		this.values = values;
	}
}
