package com.f1.utils.structs.table.derived;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.utils.OH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorArrayDeref implements DerivedCellCalculator {

	private int position;
	private DerivedCellCalculator array;
	private DerivedCellCalculator offset;

	public DerivedCellCalculatorArrayDeref(int position, DerivedCellCalculator array, DerivedCellCalculator offset) {
		this.position = position;
		this.array = array;
		this.offset = offset;
	}

	@Override
	public Object get(CalcFrameStack key) {
		Object object = array.get(key);
		if (object instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) object, this, key, 0, null);
		return eval1(object, key);
	}

	private Object eval1(Object value, CalcFrameStack key) {
		Object object = this.offset.get(key);
		if (object instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) object, this, key, 1, object);
		return eval2(value, object);
	}

	private Object eval2(Object ds, Object object) {
		if (object == null || ds == null)
			return null;
		if (ds instanceof List) {
			if (object instanceof Number) {
				List list = (List) ds;
				int pos = ((Number) object).intValue();
				int size = list.size();
				if (pos < 0)
					pos += size;
				return pos < size && pos >= 0 ? list.get(pos) : null;
			}
		} else if (ds instanceof Map) {
			Map map = (Map) ds;
			return map.get(object);
		} else if (ds instanceof Set) {
			Set set = (Set) ds;
			return set.contains(object);
		}
		return null;
	}

	@Override
	public Class<?> getReturnType() {
		return Object.class;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		array.toString(sink).append('[');
		offset.toString(sink).append(']');
		return sink;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorArrayDeref.class)
			return false;
		DerivedCellCalculatorArrayDeref o = (DerivedCellCalculatorArrayDeref) other;
		return OH.eq(array, o.array) && OH.eq(offset, o.offset);
	}

	@Override
	public int hashCode() {
		return OH.hashCode(array, offset);
	}
	@Override
	public int getPosition() {
		return position;
	}
	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorArrayDeref(position, array, offset);
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
		return true;
	}

	@Override
	public int getInnerCalcsCount() {
		return 2;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return n == 0 ? this.array : this.offset;
	}
	@Override
	public Object resume(PauseStack paused) {
		Object array, offset;
		if (paused.getState() == 0) {
			array = paused.getNext().resume();
			if (array instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) array, this, paused.getLcvs(), 0, null);
			return eval1(array, paused.getLcvs());
		} else { //state==1
			array = paused.getAttachment();
			offset = paused.getNext().resume();
			if (offset instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) array, this, paused.getLcvs(), 1, array);
			return eval2(array, offset);
		}
	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorArrayDeref o = (DerivedCellCalculatorArrayDeref) other;
		return DerivedHelper.areSame(array, o.array) && DerivedHelper.areSame(offset, o.offset);
	}

}
