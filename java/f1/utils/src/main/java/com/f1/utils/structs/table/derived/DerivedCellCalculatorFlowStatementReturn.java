package com.f1.utils.structs.table.derived;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorFlowStatementReturn implements DerivedCellCalculatorFlowStatement {
	//	public static final FlowControl RETURN_DEFAULT = new FlowControlReturn(null, null);

	final private int position;
	final private DerivedCellCalculator right;

	public DerivedCellCalculatorFlowStatementReturn(int position, DerivedCellCalculator right) {
		this.position = position;
		this.right = right;
	}

	@Override
	public Object getFlowControl(CalcFrameStack lcvs) {
		if (right == null)
			return VOID;
		Object object = right.get(lcvs);
		if (object instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) object, this, lcvs, 0, null);
		//		Object r = run(object);
		return object == null ? NULL : object;
	}

	@Override
	public Object resumeFlowControl(PauseStack paused) {
		Object object = paused.getNext().resume();
		if (object instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) object, this, paused.getLcvs(), 0, null);
		return object == null ? NULL : object;
	}

	@Override
	public Class<?> getReturnType() {
		return right == null ? null : right.getReturnType();
	}

	@Override
	public int getPosition() {
		return this.position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorFlowStatementReturn(position, right);
	}

	@Override
	public boolean isConst() {
		return false;//this.right == null || this.right.isConst();
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("return");
		if (right != null) {
			sink.append(' ');
			right.toString(sink);
		}
		return sink;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public int getInnerCalcsCount() {
		return right == null ? 0 : 1;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return right;
	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean getFlowControlAlwaysCompletes() {
		return true;
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		return DerivedHelper.getForFlowControl(this.getFlowControl(lcvs));
	}
	@Override
	public Object resume(PauseStack lcvs) {
		return DerivedHelper.getForFlowControl(this.resumeFlowControl(lcvs));
	}
	@Override
	public boolean hasReturn() {
		return true;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		return other.getClass() == this.getClass() && DerivedHelper.areSame(((DerivedCellCalculatorFlowStatementReturn) other).right, right);
	}
}
