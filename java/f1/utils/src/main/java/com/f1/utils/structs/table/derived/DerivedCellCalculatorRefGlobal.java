package com.f1.utils.structs.table.derived;

import java.util.Set;

import com.f1.utils.OH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorRefGlobal implements DerivedCellCalculatorWithDependencies {
	private static final Object NO_VALUE = new Object();
	private Class<?> type;
	private String id;
	private int position;
	private Object constValue = NO_VALUE;

	public DerivedCellCalculatorRefGlobal(int position, Class<?> type, String id) {
		this.position = position;
		this.type = OH.getBoxed(type);
		this.id = id;
	}

	@Override
	public Object get(CalcFrameStack key) {
		if (constValue != NO_VALUE)
			return constValue;
		return key.getGlobal().getValue(id);
	}

	@Override
	public Class<?> getReturnType() {
		return this.type;
	}

	@Override
	public Set<Object> getDependencyIds(Set<Object> sink) {
		if (!isConst())
			sink.add(id);
		return sink;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(id);
	}

	public Object getId() {
		return id;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorRefGlobal.class)
			return false;
		DerivedCellCalculatorRefGlobal o = (DerivedCellCalculatorRefGlobal) other;
		return OH.eq(id, o.id) && OH.eq(type, o.type);
	}

	@Override
	public int hashCode() {
		return OH.hashCode(id, type);
	}
	@Override
	public int getPosition() {
		return position;
	}
	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorRefGlobal(position, type, id);
	}
	@Override
	public boolean isConst() {
		return constValue != NO_VALUE;
	}

	@Override
	public String toString() {
		return OH.toString(id);
	}
	@Override
	public boolean isReadOnly() {
		return true;
	}
	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		throw new IndexOutOfBoundsException("" + n);
	}

	@Override
	public int getInnerCalcsCount() {
		return 0;
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
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorRefGlobal o = (DerivedCellCalculatorRefGlobal) other;
		if (OH.ne(type, o.type))
			return false;
		if (OH.ne(id, o.id))
			return false;
		return true;
	}

	public void setConst(Object value) {
		this.constValue = value;
	}

}
