package com.f1.utils.structs.table.derived;

import com.f1.utils.structs.table.BasicColumn;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedColumn extends BasicColumn {

	private DerivedCellCalculator getter;

	public DerivedColumn(DerivedTable table, int uid, int location, String id, DerivedCellCalculator calc) {
		super(table, uid, location, calc.getReturnType(), id);
		this.getter = calc;
	}

	public Object getValue(CalcFrameStack row) {
		return getter.get(row);
	}

	public DerivedCellCalculator getCalculator() {
		return getter;
	}
	@Override
	public String toString() {
		return "DeriveColumn [id=" + getId() + ", location=" + getLocation() + ", type=" + getType() + ", calc=" + this.getter + "]";
	}

}
