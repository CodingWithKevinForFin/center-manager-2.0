package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

abstract public class StdevAggCalculatorAbstract extends AbstractAggCalculator {
	final private boolean isFloat;
	private Class returnType;
	private final VarAggCalculatorAbstract vAC;
	private double doubleValue;

	public StdevAggCalculatorAbstract(int position, DerivedCellCalculator inner, boolean population) {
		super(position, inner);
		PrimitiveMath manager = getPrimitiveMathManager(inner);
		this.returnType = manager.getReturnType();
		isFloat = (returnType == Float.class || returnType == Double.class || returnType == float.class || returnType == double.class);
		this.vAC = population ? new VarAggCalculator(position, inner) : new VarAggCalculatorSample(position, inner);
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		Object variance = vAC.get(lcvs);
		if (variance == null) {
			return null;
		} else {
			doubleValue = (Double) variance;
			return Math.sqrt(doubleValue);
		}
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		vAC.visit(sf, values);
	}
	@Override
	public Class<?> getReturnType() {
		return Double.class;
	}

	@Override
	public void setValue(Object object) {
		if (object instanceof Number)
			vAC.setValue(Math.pow(((Number) object).doubleValue(), 2));
		else
			vAC.setValue(null); //??? needs review. @Rob

	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		return super.isSame(other) && ((StdevAggCalculatorAbstract) other).vAC.isSame(this.vAC);
	}

	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(0d);
	}
}
