package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.MH;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTriggerAgg_Sum extends AmiTriggerAgg {

	private Class<?> returnType;
	private boolean isFloat;
	private PrimitiveMath math;

	public AmiTriggerAgg_Sum(int position, DerivedCellCalculator inner) {
		super(position, inner);
		this.math = getPrimitiveMathManager(inner);
		this.returnType = math.getReturnType();
		this.isFloat = returnType == Float.class || returnType == Double.class || returnType == float.class || returnType == double.class;
	}

	@Override
	public Class<?> getReturnType() {
		return isFloat ? double.class : long.class;
	}

	@Override
	public String getMethodName() {
		return "sum";
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiTriggerAgg_Sum(getPosition(), getInner().copy());
	}

	@Override
	protected Object calculateInsert(Object nuw, Object current, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		if (isFloat) {
			double curN = toDouble(current);
			double nuwN = toDouble(nuw);
			return nuwN + curN;
		} else {
			long curN = toLong(current);
			long nuwN = toLong(nuw);
			return nuwN + curN;
		}
	}
	@Override
	protected Object calculateUpdate(Object old, Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper,
			CalcFrameStack sf) {
		if (isFloat) {
			double curN = toDouble(current);
			double nuwN = toDouble(nuw);
			double oldN = toDouble(old);
			return (nuwN - oldN) + curN;
		} else {
			long curN = toLong(current);
			long nuwN = toLong(nuw);
			long oldN = toLong(old);
			return (nuwN - oldN) + curN;
		}
	}
	@Override
	protected Object calculateDelete(Object old, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		if (isFloat) {
			double curN = toDouble(current);
			double oldN = toDouble(old);
			return curN - oldN;
		} else {
			long curN = toLong(current);
			long oldN = toLong(old);
			return curN - oldN;
		}
	}
	static private double toDouble(Object old) {
		if (old == null)
			return 0;
		double r = ((Number) old).doubleValue();
		return MH.isntNumber(r) ? 0 : r;
	}
	static private long toLong(Object old) {
		if (old == null)
			return 0L;
		return ((Number) old).longValue();
	}

}
