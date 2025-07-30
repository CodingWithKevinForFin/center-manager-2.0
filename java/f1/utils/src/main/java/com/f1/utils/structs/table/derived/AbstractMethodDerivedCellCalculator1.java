package com.f1.utils.structs.table.derived;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AbstractMethodDerivedCellCalculator1 implements MethodDerivedCellCalculator {

	final protected DerivedCellCalculator param0;
	final private int position;

	public AbstractMethodDerivedCellCalculator1(int position, DerivedCellCalculator param) {
		this.param0 = DerivedHelper.reduceConst(param);
		this.position = position;
		getDefinition().verify(this, false);
	}

	@Override
	public Object get(CalcFrameStack key) {
		Object p1 = param0.get(key);
		if (p1 instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) p1, this, key, 0, null);
		return eval(p1);
	}

	@Override
	public Class<?> getReturnType() {
		return getDefinition().getReturnType();
	}
	abstract public Object eval(Object p1);
	abstract public DerivedCellCalculator copy(DerivedCellCalculator params);

	@Override
	final public StringBuilder toString(StringBuilder sink) {
		sink.append(getMethodName());
		sink.append('(');
		this.param0.toString(sink);
		return sink.append(')');
	}

	@Override
	final public int getPosition() {
		return this.position;
	}

	@Override
	final public DerivedCellCalculator copy() {
		return copy(param0.copy());
	}

	@Override
	public boolean isConst() {
		return param0.isConst();
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
		return param0.isReadOnly();
	}

	@Override
	final public int getInnerCalcsCount() {
		return 1;
	}

	@Override
	final public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.param0;
	}
	@Override
	final public int getParamsCount() {
		return 1;
	}

	@Override
	final public DerivedCellCalculator getParamAt(int n) {
		return this.param0;
	}

	@Override
	final public Object resume(PauseStack paused) {
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
		if (other == null || other.getClass() != getClass())
			return false;
		AbstractMethodDerivedCellCalculator1 o = (AbstractMethodDerivedCellCalculator1) other;
		return param0.isSame(o.param0);
	}
}
