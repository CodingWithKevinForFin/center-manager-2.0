package com.f1.utils.structs.table.derived;

import java.util.Set;

import com.f1.base.Caster;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorAssignment implements DerivedCellCalculatorWithDependencies {

	private final Class<?> type;
	private final Caster<?> caster;
	private String variableName;
	private int position;
	private DerivedCellCalculator right;
	private boolean isDeclaration;

	public DerivedCellCalculatorAssignment(int position, String leftId, Class<?> type, DerivedCellCalculator right, boolean isDeclaration) {
		this.position = position;
		this.isDeclaration = isDeclaration;
		this.type = type;
		this.caster = OH.getCaster(type);
		if (this.type != String.class && !OH.isCoercable(this.type, right.getReturnType()))
			throw new ExpressionParserException(position,
					"Illegal Assignment for '" + leftId + "', can not cast " + right.getReturnType().getSimpleName() + " to " + this.type.getSimpleName());
		this.variableName = leftId;
		this.right = right;

	}
	@Override
	public Object get(CalcFrameStack values) {
		Object r = right.get(values);
		return run(values, r);
	}

	private Object run(CalcFrameStack values, Object val) {
		if (val instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) val, this, values, 0, null);
		if (val != null)
			val = this.caster == Caster_String.INSTANCE ? DerivedHelper.toString(val) : this.caster.cast(val);
		try {
			DerivedHelper.putValue(values, variableName, val);
		} catch (UnsupportedOperationException e) {
			throw new ExpressionParserException(position, "Can not mutate readonly variable: " + variableName, e);
		}
		return val;
	}

	@Override
	public Class<?> getReturnType() {
		return this.type;
	}

	@Override
	public Set<Object> getDependencyIds(Set<Object> sink) {
		sink.add(variableName);
		return sink;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (isDeclaration)
			sink.append(this.type.getSimpleName()).append(' ');
		else
			sink.append(variableName).append(" = ");
		return right.toString(sink);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorAssignment.class)
			return false;
		DerivedCellCalculatorAssignment o = (DerivedCellCalculatorAssignment) other;
		return OH.eq(variableName, o.variableName) && OH.eq(type, o.type) && OH.eq(right, o.right) && isDeclaration == o.isDeclaration;
	}

	@Override
	public int hashCode() {
		return OH.hashCode(variableName, type);
	}
	@Override
	public int getPosition() {
		return position;
	}
	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorAssignment(position, variableName, type, right.copy(), isDeclaration);
	}
	@Override
	public boolean isConst() {
		return false;
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

	public String getVariableName() {
		return variableName;
	}
	public boolean getIsDeclaration() {
		return isDeclaration;
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
	public Object resume(PauseStack paused) {
		return run(paused.getLcvs(), paused.getNext().resume());
	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorAssignment o = (DerivedCellCalculatorAssignment) other;
		return OH.eq(this.variableName, o.variableName) && OH.eq(this.type, o.type) && right.isSame(o.right) && this.isDeclaration == o.isDeclaration;
	}
}
