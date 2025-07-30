package com.f1.utils.structs.table.derived;

import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorNot implements DerivedCellCalculator {

	private final DerivedCellCalculator inner;
	private final int position;

	public DerivedCellCalculatorNot(int position, DerivedCellCalculator inner) {
		if (inner.getReturnType() != Object.class && inner.getReturnType() != Boolean.class)
			throw new ExpressionParserException(position, "NOT Expecting boolean expression");
		this.inner = inner;
		this.position = position;
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		Object object = inner.get(lcvs);
		if (object instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) object, this, lcvs, 0, null);
		return eval(object);
	}
	private Object eval(Object object) {
		return object instanceof Boolean ? !((Boolean) object).booleanValue() : null;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("!");
		return inner.toString(sink);
	}

	@Override
	public Class<?> getReturnType() {
		return Boolean.class;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorNot(position, inner.copy());
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
		return this.inner;
	}
	public DerivedCellCalculator getInner() {
		return inner;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
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
		return other.getClass() == this.getClass() && DerivedHelper.childrenAreSame(this, other);
	}

}
