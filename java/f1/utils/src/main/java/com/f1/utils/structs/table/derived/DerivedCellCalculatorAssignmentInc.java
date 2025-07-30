package com.f1.utils.structs.table.derived;

import java.util.Set;

import com.f1.utils.OH;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorAssignmentInc implements DerivedCellCalculatorWithDependencies {

	final private Class<?> type;
	final private String variableName;
	final private int position;
	final private boolean isInc;
	final private boolean isReturnAfterChange;
	final private PrimitiveMath math;

	public DerivedCellCalculatorAssignmentInc(int position, String variableName, Class<?> type, boolean isInc, boolean returnAfterChange) {
		this.position = position;
		this.isInc = isInc;
		this.isReturnAfterChange = returnAfterChange;
		this.type = type;
		this.variableName = variableName;
		this.math = PrimitiveMathManager.INSTANCE.getNoThrow(type);
		if (math == null && !type.isAssignableFrom(Number.class))
			throw new ExpressionParserException(position, "Operator '" + (isInc ? "++ " : "--") + "' not supported for: " + type.getSimpleName());
	}
	@Override
	public Object get(CalcFrameStack values) {
		Object r = DerivedHelper.getValue(values, variableName);
		if (r == null)
			return null;
		if (math == null) {
			PrimitiveMath m = PrimitiveMathManager.INSTANCE.getNoThrow(r.getClass());
			if (m == null)
				return null;
			Number r2 = m.add((Number) r, isInc ? 1 : -1);
			DerivedHelper.putValue(values, variableName, r2);
			return isReturnAfterChange ? r2 : r;
		}
		Number r2 = math.add((Number) r, isInc ? 1 : -1);
		DerivedHelper.putValue(values, variableName, r2);
		return isReturnAfterChange ? r2 : r;
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
		if (isReturnAfterChange)
			sink.append(isInc ? "++" : "--").append(variableName);
		else
			sink.append(variableName).append(isInc ? "++" : "--");
		return sink;
	}
	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorAssignmentInc.class)
			return false;
		DerivedCellCalculatorAssignmentInc o = (DerivedCellCalculatorAssignmentInc) other;
		return OH.eq(variableName, o.variableName) && OH.eq(type, o.type) && isInc == o.isInc && isReturnAfterChange == o.isReturnAfterChange;
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
		return new DerivedCellCalculatorAssignmentInc(position, variableName, type, isInc, isReturnAfterChange);
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

	public String getVariableName() {
		return variableName;
	}
	public boolean getIsInc() {
		return isInc;
	}
	public boolean getIsReturnAfterChange() {
		return isReturnAfterChange;
	}
	@Override
	public int getInnerCalcsCount() {
		return 0;
	}
	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
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
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorAssignmentInc o = (DerivedCellCalculatorAssignmentInc) other;
		return OH.eq(this.variableName, o.variableName) && OH.eq(this.type, o.type) && isInc == o.isInc && isReturnAfterChange == o.isReturnAfterChange;
	}
}
