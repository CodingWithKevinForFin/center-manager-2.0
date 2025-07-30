package com.f1.utils.structs.table.derived;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.utils.OH;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorAssignmentIncWithDeref implements DerivedCellCalculatorWithDependencies {

	final private int position;
	final private boolean isInc;
	final private boolean isReturnAfterChange;
	final private DerivedCellCalculator deref;
	final private DerivedCellCalculator offset;

	public DerivedCellCalculatorAssignmentIncWithDeref(int position, DerivedCellCalculator deref, DerivedCellCalculator offset, boolean isInc, boolean returnAfterChange) {
		this.position = position;
		this.isInc = isInc;
		this.isReturnAfterChange = returnAfterChange;
		this.deref = deref;
		this.offset = offset;
	}
	@Override
	public Object get(CalcFrameStack values) {
		Object ds = deref.get(values);
		if (ds == null)
			return null;
		Object object = offset.get(values);
		if (ds instanceof List) {
			if (object instanceof Number) {
				List list = (List) ds;
				int pos = ((Number) object).intValue();
				if (pos >= 0 && pos < list.size()) {
					Object r = list.get(pos);
					if (r != null) {
						PrimitiveMath math = PrimitiveMathManager.INSTANCE.getNoThrow(r.getClass());
						if (math != null) {
							Number r2 = math.add((Number) r, isInc ? 1 : -1);
							list.set(pos, r2);
							return isReturnAfterChange ? r2 : r;
						}
					}
				}
			}
		} else if (ds instanceof Map) {
			Map map = (Map) ds;
			Object r = map.get(object);
			if (r != null) {
				PrimitiveMath math = PrimitiveMathManager.INSTANCE.getNoThrow(r.getClass());
				if (math != null) {
					Number r2 = math.add((Number) r, isInc ? 1 : -1);
					map.put(object, r2);
					return isReturnAfterChange ? r2 : r;
				}
			}
		}
		return null;
	}

	@Override
	public Class<?> getReturnType() {
		return Number.class;
	}

	@Override
	public Set<Object> getDependencyIds(Set<Object> sink) {
		return sink;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (isReturnAfterChange) {
			sink.append(isInc ? "++" : "--");
			deref.toString(sink);
			sink.append('[');
			offset.toString(sink);
			sink.append(']');
		} else {
			deref.toString(sink);
			sink.append('[');
			offset.toString(sink);
			sink.append(']');
			sink.append(isInc ? "++" : "--");
		}
		return sink;
	}
	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorAssignmentIncWithDeref.class)
			return false;
		DerivedCellCalculatorAssignmentIncWithDeref o = (DerivedCellCalculatorAssignmentIncWithDeref) other;
		return OH.eq(this.deref, o.deref) && OH.eq(this.offset, o.offset) && isInc == o.isInc && isReturnAfterChange == o.isReturnAfterChange;
	}

	@Override
	public int hashCode() {
		return OH.hashCode(isReturnAfterChange ? 0 : 1, isInc ? 0 : 1, this.deref.hashCode(), this.offset.hashCode());
	}
	@Override
	public int getPosition() {
		return position;
	}
	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorAssignmentIncWithDeref(position, this.deref, this.offset, isInc, isReturnAfterChange);
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

	public boolean getIsInc() {
		return isInc;
	}
	public boolean getIsReturnAfterChange() {
		return isReturnAfterChange;
	}
	@Override
	public int getInnerCalcsCount() {
		return 0;
	}
	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		throw new IndexOutOfBoundsException("" + n);
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
		DerivedCellCalculatorAssignmentIncWithDeref o = (DerivedCellCalculatorAssignmentIncWithDeref) other;
		return OH.eq(this.deref, o.deref) && OH.eq(this.offset, o.offset) && isInc == o.isInc && isReturnAfterChange == o.isReturnAfterChange;
	}
}
