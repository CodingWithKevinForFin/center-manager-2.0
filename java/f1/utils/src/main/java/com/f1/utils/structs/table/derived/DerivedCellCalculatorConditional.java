package com.f1.utils.structs.table.derived;

import com.f1.base.Caster;
import com.f1.utils.OH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorConditional implements DerivedCellCalculator {

	final private DerivedCellCalculator condition;
	final private DerivedCellCalculator trueExpression;
	final private DerivedCellCalculator falseExpression;
	final private boolean falseNeedsCast;
	final private boolean trueNeedsCast;
	final private Class<?> returnType;
	final private Caster<?> returnTypeCaster;

	public DerivedCellCalculatorConditional(DerivedCellCalculator condition, DerivedCellCalculator trueExpression, DerivedCellCalculator falseExpression) {
		if (condition.getReturnType() != Boolean.class)
			throw new ExpressionParserException(condition.getPosition(), "conditional must return boolean, not: " + condition.getReturnType().getSimpleName());
		this.condition = condition;
		this.trueExpression = trueExpression;
		this.falseExpression = falseExpression;
		if (DerivedHelper.isNull(trueExpression))
			this.returnType = falseExpression.getReturnType();
		else if (DerivedHelper.isNull(falseExpression))
			this.returnType = trueExpression.getReturnType();
		else
			this.returnType = OH.getWidest(trueExpression.getReturnType(), falseExpression.getReturnType());
		this.returnTypeCaster = OH.getCaster(this.returnType);
		this.falseNeedsCast = falseExpression.getReturnType() != returnType;
		this.trueNeedsCast = trueExpression.getReturnType() != returnType;
	}

	private DerivedCellCalculatorConditional(DerivedCellCalculatorConditional i) {
		this.condition = i.condition.copy();
		this.trueExpression = i.trueExpression.copy();
		this.falseExpression = i.falseExpression.copy();
		this.falseNeedsCast = i.falseNeedsCast;
		this.trueNeedsCast = i.trueNeedsCast;
		this.returnType = i.returnType;
		this.returnTypeCaster = OH.getCaster(this.returnType);
	}

	@Override
	public Object get(CalcFrameStack key) {
		Object object = condition.get(key);
		return eval(key, object);
	}

	private Object eval(CalcFrameStack key, Object object) {
		if (object instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) object, this, key, 0, null);
		if (Boolean.TRUE.equals((Boolean) object)) {
			Object o = trueExpression.get(key);
			if (o instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) o, this, key, 1, null);
			return evalTrue(o);
		} else {
			Object o = falseExpression.get(key);
			if (o instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) o, this, key, 2, null);
			return evalFalse(o);
		}
	}

	private Object evalFalse(Object o) {
		return falseNeedsCast ? this.returnTypeCaster.cast(o, false) : o;
	}

	private Object evalTrue(Object o) {
		return trueNeedsCast ? this.returnTypeCaster.cast(o, false) : o;
	}

	@Override
	public Class<?> getReturnType() {
		return returnType;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('(');
		condition.toString(sink);
		sink.append(" ? ");
		trueExpression.toString(sink);
		sink.append(" : ");
		falseExpression.toString(sink);
		sink.append(')');
		return sink;
	}

	public DerivedCellCalculator getCondition() {
		return this.condition;
	}
	public DerivedCellCalculator getTrue() {
		return this.trueExpression;
	}
	public DerivedCellCalculator getFalse() {
		return this.falseExpression;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorConditional.class)
			return false;
		DerivedCellCalculatorConditional o = (DerivedCellCalculatorConditional) other;
		return OH.eq(returnType, o.returnType) && OH.eq(condition, o.condition) && OH.eq(trueExpression, o.trueExpression) && OH.eq(falseExpression, o.falseExpression);
	}

	@Override
	public int hashCode() {
		return OH.hashCode(returnType, condition, trueExpression, falseExpression);
	}

	@Override
	public int getPosition() {
		return condition.getPosition();
	}
	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public DerivedCellCalculatorConditional copy() {
		return new DerivedCellCalculatorConditional(this);
	}

	@Override
	public boolean isConst() {
		return this.condition.isConst() && this.trueExpression.isConst() && this.falseExpression.isConst();
	}
	@Override
	public boolean isReadOnly() {
		return this.condition.isReadOnly() && this.trueExpression.isReadOnly() && this.falseExpression.isReadOnly();
	}

	@Override
	public int getInnerCalcsCount() {
		return 3;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		switch (n) {
			case 0:
				return condition;
			case 1:
				return trueExpression;
			case 2:
				return falseExpression;
			default:
				throw new IndexOutOfBoundsException("" + n);
		}
	}

	@Override
	public Object resume(PauseStack paused) {
		if (paused.getState() == 0) {//in condition
			Object val = paused.getNext().resume();
			if (val instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) val, this, paused.getLcvs(), 0, null);
			return eval(paused.getLcvs(), val);
		} else if (paused.getState() == 1) {//in false condition
			Object val = paused.getNext().resume();
			if (val instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) val, this, paused.getLcvs(), 1, null);
			return evalFalse(val);
		} else { // in true condition
			Object val = paused.getNext().resume();
			if (val instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) val, this, paused.getLcvs(), 2, null);
			return evalTrue(val);
		}
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
