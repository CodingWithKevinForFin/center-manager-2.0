package com.f1.utils.structs.table.derived;

import com.f1.utils.OH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorFlowStatementThrow implements DerivedCellCalculatorFlowStatement {

	private int position;
	private DerivedCellCalculator param;

	public DerivedCellCalculatorFlowStatementThrow(int position, DerivedCellCalculator param) {
		this.position = position;
		if (param.isConst() && param.get(null) == null)
			throw new ExpressionParserException(position, "throw value can not be null");
		this.param = param;
	}

	@Override
	public Object getFlowControl(CalcFrameStack lcvs) {
		Object object = this.param.get(lcvs);
		if (object instanceof FlowControlThrow)
			throw (FlowControlThrow) object;
		if (object instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) object, this, lcvs, 0, null);
		throw eval(object);
	}

	private FlowControlThrow eval(Object returnValue) {
		return new FlowControlThrow(this, returnValue);
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("throw ");
		this.param.toString(sink);
		return sink;
	}
	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public Class<?> getReturnType() {
		return null;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorFlowStatementThrow(position, param);
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorFlowStatementThrow.class)
			return false;
		DerivedCellCalculatorFlowStatementThrow o = (DerivedCellCalculatorFlowStatementThrow) other;
		return OH.eq(param, o.param);
	}
	@Override
	public int hashCode() {
		return OH.hashCode(position, this.param);
	}
	@Override
	public int getInnerCalcsCount() {
		return 1;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.param;
	}
	@Override
	public Object resumeFlowControl(PauseStack paused) {
		Object r = paused.getNext().resume();
		if (r instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) r, this, paused.getLcvs(), 0, null);
		throw eval(r);
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
		return false;
	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorFlowStatementThrow o = (DerivedCellCalculatorFlowStatementThrow) other;
		return DerivedHelper.areSame(this.param, o.param);
	}
}
