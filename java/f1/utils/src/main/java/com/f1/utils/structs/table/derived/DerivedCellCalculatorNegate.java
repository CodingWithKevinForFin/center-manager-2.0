package com.f1.utils.structs.table.derived;

import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorNegate implements DerivedCellCalculator {

	private final DerivedCellCalculator inner;
	private final int position;
	private final PrimitiveMath math;

	public DerivedCellCalculatorNegate(int position, DerivedCellCalculator inner) {
		this.inner = inner;
		this.math = PrimitiveMathManager.INSTANCE.getNoThrow((Class<? extends Number>) inner.getReturnType());
		if (math == null && !inner.getReturnType().isAssignableFrom(Number.class))
			throw new ExpressionParserException(position, "Operator '-' not supported for: " + inner.getReturnType().getSimpleName());
		this.position = position;
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		Object val = inner.get(lcvs);
		if (val instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) val, this, lcvs, 0, null);
		return eval(val);
	}

	private Object eval(Object val) {
		if (val == null)
			return null;
		if (math != null)
			math.negate((Number) val);
		PrimitiveMath m = PrimitiveMathManager.INSTANCE.getNoThrow(val.getClass());
		return m == null ? null : m.negate((Number) val);
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("!");
		return inner.toString(sink);
	}

	@Override
	public Class<?> getReturnType() {
		return this.math == null ? Number.class : this.math.getReturnType();
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorNegate(position, inner.copy());
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
	public boolean isPausable() {
		return false;
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
	public boolean isSame(DerivedCellCalculator other) {
		return other.getClass() == this.getClass() && DerivedHelper.childrenAreSame(this, other);
	}
}
