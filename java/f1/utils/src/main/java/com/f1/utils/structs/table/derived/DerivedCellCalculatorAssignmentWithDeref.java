package com.f1.utils.structs.table.derived;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.utils.OH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorAssignmentWithDeref implements DerivedCellCalculatorWithDependencies {

	final private int position;
	final private DerivedCellCalculator right;
	final private DerivedCellCalculator deref;
	final private DerivedCellCalculator offset;

	public DerivedCellCalculatorAssignmentWithDeref(int position, DerivedCellCalculator deref, DerivedCellCalculator offset, DerivedCellCalculator right) {
		this.position = position;
		this.deref = deref;
		this.offset = offset;
		this.right = right;

	}
	@Override
	public Object get(CalcFrameStack values) {
		Object r = right.get(values);
		return run(values, r);
	}

	private Object run(CalcFrameStack values, Object val) {
		if (val instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) val, this, values, 0, null);
		Object ds = deref.get(values);
		if (ds == null)
			return null;
		Object object = offset.get(values);
		if (ds instanceof List) {
			if (object instanceof Number) {
				List list = (List) ds;
				int pos = ((Number) object).intValue();
				if (pos >= 0) {
					while (list.size() <= pos)
						list.add(null);
					list.set(pos, val);
					return val;
				}
			}
		} else if (ds instanceof Map) {
			Map map = (Map) ds;
			map.put(object, val);
			return val;
		}
		return val;
	}

	@Override
	public Class<?> getReturnType() {
		return Object.class;
	}

	@Override
	public Set<Object> getDependencyIds(Set<Object> sink) {
		return sink;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(deref).append('[').append(offset).append(']').append(" = ");
		return right.toString(sink);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorAssignmentWithDeref.class)
			return false;
		DerivedCellCalculatorAssignmentWithDeref o = (DerivedCellCalculatorAssignmentWithDeref) other;
		return OH.eq(deref, o.deref) && OH.eq(offset, o.offset) && OH.eq(right, o.right);
	}

	@Override
	public int hashCode() {
		return OH.hashCode(deref, offset, right);
	}
	@Override
	public int getPosition() {
		return position;
	}
	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorAssignmentWithDeref(position, deref.copy(), offset.copy(), right.copy());
	}
	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	public boolean isReadOnly() {
		return false;
	}

	public DerivedCellCalculator getRight() {
		return this.right;
	}

	@Override
	public int getInnerCalcsCount() {
		return 3;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		switch (n) {
			case 0:
				return deref;
			case 1:
				return offset;
			case 2:
				return right;
		}
		throw new RuntimeException();
	}
	@Override
	public Object resume(PauseStack paused) {
		return run(paused.getLcvs(), paused.getNext().resume());
	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorAssignmentWithDeref o = (DerivedCellCalculatorAssignmentWithDeref) other;
		return this.deref.isSame(o.deref) && OH.eq(this.offset, o.offset) && right.isSame(o.right);
	}
}
