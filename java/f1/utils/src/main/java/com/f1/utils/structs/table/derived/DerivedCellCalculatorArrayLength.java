package com.f1.utils.structs.table.derived;

import java.lang.reflect.Array;

import com.f1.utils.OH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorArrayLength implements DerivedCellCalculator {

	private int position;
	DerivedCellCalculator array;

	public DerivedCellCalculatorArrayLength(int position, DerivedCellCalculator array) {
		this.position = position;
		if (!array.getReturnType().isArray())
			throw new ExpressionParserException(position, ".length must be used with arrays");
		this.array = array;
	}

	@Override
	public Object get(CalcFrameStack key) {
		Object object = array.get(key);
		if (object instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) object, this, key, 0, null);
		return eval(object);
	}

	private Object eval(Object value) {
		return Array.getLength(value);
	}

	@Override
	public Class<?> getReturnType() {
		return Integer.class;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return array.toString(sink).append(".length");
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorArrayLength.class)
			return false;
		DerivedCellCalculatorArrayLength o = (DerivedCellCalculatorArrayLength) other;
		return OH.eq(array, o.array);
	}

	@Override
	public int hashCode() {
		return OH.hashCode(array);
	}
	@Override
	public int getPosition() {
		return position;
	}
	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorArrayLength(position, array);
	}
	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String toString() {
		return OH.toString(this);
	}
	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public int getInnerCalcsCount() {
		return 1;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.array;
	}
	@Override
	public Object resume(PauseStack paused) {
		Object r = paused.getNext().resume();
		if (r instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) r, this, paused.getLcvs(), 0, null);
		return eval(r);
	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorArrayLength o = (DerivedCellCalculatorArrayLength) other;
		return DerivedHelper.areSame(array, o.array);
	}
}
