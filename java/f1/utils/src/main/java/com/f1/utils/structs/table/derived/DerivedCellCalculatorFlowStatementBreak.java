package com.f1.utils.structs.table.derived;

import com.f1.utils.OH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorFlowStatementBreak implements DerivedCellCalculatorFlowStatement {

	final private String name;
	final private int position;
	final private FlowControlBreak breakValue;

	public DerivedCellCalculatorFlowStatementBreak(int position, DerivedCellCalculator right) {
		this.position = position;
		if (right == null) {
			this.name = null;
		} else if (right.isConst() && right.getReturnType() == String.class) {
			this.name = (String) right.get(null);
		} else
			throw new ExpressionParserException(right.getPosition(), "Paramenter must be string constant: " + right);
		this.breakValue = new FlowControlBreak(this, this.name);
	}
	public DerivedCellCalculatorFlowStatementBreak(int position, String name) {
		this.position = position;
		this.name = name;
		this.breakValue = new FlowControlBreak(this, this.name);
	}

	@Override
	public Object getFlowControl(CalcFrameStack lcvs) {
		return breakValue;
	}

	@Override
	public Object resumeFlowControl(PauseStack paused) {
		return breakValue;
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return breakValue;
	}

	@Override
	public Object resume(PauseStack paused) {
		return breakValue;
	}

	@Override
	public Class<?> getReturnType() {
		return null;
	}

	@Override
	public int getPosition() {
		return this.position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorFlowStatementBreak(position, name);
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("break");
		if (name != null)
			sink.append(' ').append(name);
		return sink;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public int getInnerCalcsCount() {
		return 0;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		throw new IndexOutOfBoundsException();
	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean getFlowControlAlwaysCompletes() {
		return false;
	}
	@Override
	public boolean hasReturn() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		return other.getClass() == this.getClass() && OH.eq(((DerivedCellCalculatorFlowStatementBreak) other).name, name);
	}

}
