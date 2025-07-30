package com.f1.utils.sql;

import com.f1.base.Table;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorSqlTableAs implements DerivedCellCalculator {

	final private DerivedCellCalculator inner;
	final private int position;
	final private String asName;

	public DerivedCellCalculatorSqlTableAs(int position, DerivedCellCalculator inner, String asName) {
		this.position = position;
		this.inner = inner;
		this.asName = asName;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {

		sink.append(inner);
		if (OH.ne(inner, asName))
			sink.append(" AS ").append(asName);
		return sink;

	}

	@Override
	public Object get(CalcFrameStack sf) {
		Table t = (Table) inner.get(sf);
		if (t != null)
			t.setTitle(asName);
		return t;
	}

	@Override
	public Class<?> getReturnType() {
		return Table.class;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorSqlTableAs(position, inner, asName);
	}

	@Override
	public boolean isConst() {
		return inner.isConst();
	}

	@Override
	public boolean isReadOnly() {
		return inner.isReadOnly();
	}

	@Override
	public int getInnerCalcsCount() {
		return 1;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return inner;
	}

	@Override
	public Object resume(PauseStack paused) {
		throw new IllegalStateException();
	}

	@Override
	public boolean isPausable() {
		return false;
	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other == this)
			return true;
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorSqlTableAs o = (DerivedCellCalculatorSqlTableAs) other;
		return o.inner.isSame(inner) && OH.eq(o.asName, asName);
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

}
