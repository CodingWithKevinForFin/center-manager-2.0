package com.f1.utils.structs.table.derived;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AbstractMethodDerivedCellCalculator0 implements MethodDerivedCellCalculator {

	final private int position;

	public AbstractMethodDerivedCellCalculator0(int position) {
		this.position = position;
		getDefinition().verify(this, false);
	}

	@Override
	final public Object get(CalcFrameStack sf) {
		return eval();
	}

	@Override
	public Class<?> getReturnType() {
		return getDefinition().getReturnType();
	}

	abstract public Object eval();
	@Override
	abstract public DerivedCellCalculator copy();

	@Override
	final public StringBuilder toString(StringBuilder sink) {
		sink.append(getMethodName());
		return sink.append("()");
	}

	@Override
	final public int getPosition() {
		return this.position;
	}

	final public String toString() {
		return this.toString(new StringBuilder()).toString();
	}

	abstract public ParamsDefinition getDefinition();

	@Override
	public String getMethodName() {
		return this.getDefinition().getMethodName();
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	final public int getInnerCalcsCount() {
		return 0;
	}

	@Override
	final public DerivedCellCalculator getInnerCalcAt(int n) {
		throw new IndexOutOfBoundsException("" + n);
	}

	@Override
	final public int getParamsCount() {
		return 0;
	}

	@Override
	final public DerivedCellCalculator getParamAt(int n) {
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
		return other != null && other.getClass() == getClass();
	}
}
