package com.f1.utils.structs.table.derived;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorWrapper implements DerivedCellCalculator {

	DerivedCellCalculator inner;

	public Object get(CalcFrameStack lcvs) {
		return inner.get(lcvs);
	}

	public StringBuilder toString(StringBuilder sink) {
		return inner.toString(sink);
	}

	public Class<?> getReturnType() {
		return inner.getReturnType();
	}

	public int getPosition() {
		return inner.getPosition();
	}

	public DerivedCellCalculator copy() {
		return inner.copy();
	}

	public boolean isConst() {
		return inner.isConst();
	}

	public boolean isReadOnly() {
		return inner.isReadOnly();
	}

	public DerivedCellCalculatorWrapper(DerivedCellCalculator inner) {
		super();
		this.inner = inner;
	}

	public DerivedCellCalculator getInner() {
		return inner;
	}
	@Override
	public int getInnerCalcsCount() {
		return 1;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.inner;
	}

	public void setInner(DerivedCellCalculator inner) {
		this.inner = inner;
	}

	@Override
	public Object resume(PauseStack paused) {
		return inner.resume(paused);
	}
	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorWrapper o = (DerivedCellCalculatorWrapper) other;
		return DerivedHelper.areSame(inner, o.inner);
	}

}
