package com.f1.utils.structs.table.derived;

import com.f1.base.Caster;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorCast implements DerivedCellCalculator {

	final private Class<?> type;
	final private Caster<?> caster;
	final private int position;
	final private DerivedCellCalculator right;

	public DerivedCellCalculatorCast(int position, Class<?> type, DerivedCellCalculator right, Caster caster) {
		this.position = position;
		this.type = type;
		this.caster = caster;
		if (right == null)
			throw new ExpressionParserException(position, "Cast contents are null");
		this.right = right;
	}
	@Override
	public Object get(CalcFrameStack values) {
		Object r = right.get(values);
		if (r instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) r, this, values, 0, null);
		return eval(r);
	}
	private Object eval(Object r) {
		if (caster == Caster_String.INSTANCE)
			return DerivedHelper.toString(r);
		return this.caster.cast(r, false, false);
	}

	@Override
	public Class<?> getReturnType() {
		return this.type;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("((").append(type.getName()).append(")");
		return right.toString(sink).append(")");
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorCast.class)
			return false;
		DerivedCellCalculatorCast o = (DerivedCellCalculatorCast) other;
		return OH.eq(type, o.type) && OH.eq(right, o.right);
	}

	@Override
	public int hashCode() {
		return OH.hashCode(type, right);
	}
	@Override
	public int getPosition() {
		return position;
	}
	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorCast(position, type, right.copy(), caster);
	}
	@Override
	public boolean isConst() {
		return this.right.isConst();
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	public boolean isReadOnly() {
		return false;
	}

	public DerivedCellCalculator getRight() {
		return this.right;
	}

	@Override
	public Object resume(PauseStack paused) {
		Object r = paused.getNext().resume();
		if (r instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) r, this, paused.getLcvs(), 0, null);
		return eval(r);
	}
	@Override
	public int getInnerCalcsCount() {
		return 1;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.right;
	}
	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorCast o = (DerivedCellCalculatorCast) other;
		return OH.eq(this.type, o.type) && right.isSame(o.right);
	}
	public Caster getCaster() {
		return this.caster;
	}
}
