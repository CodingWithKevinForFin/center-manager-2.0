package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTriggerAgg_Avg extends AmiTriggerAgg {

	private Class<?> returnType;
	private boolean isFloat;
	private PrimitiveMath math;

	public static class AggregateHelper {
		private double doubleValue = 0;
		private int count = 0;
	}

	public AmiTriggerAgg_Avg(int position, DerivedCellCalculator inner) {
		super(position, inner);
		this.math = getPrimitiveMathManager(inner);
		this.returnType = math.getReturnType();
		this.isFloat = returnType == Float.class || returnType == Double.class || returnType == float.class || returnType == double.class;
	}

	@Override
	public Class<?> getReturnType() {
		return double.class;
	}
	@Override
	public String getMethodName() {
		return "avg";
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiTriggerAgg_Avg(getPosition(), getInner().copy());
	}

	@Override
	public Object calculateInsert(Object nuw, Object current, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (nuw instanceof Number)
			ah.count++;
		else
			return ah.count != 0 ? ah.doubleValue / ah.count : null;
		double nuwN;
		if (isFloat) {
			nuwN = toDouble(nuw);
		} else {
			nuwN = toLong(nuw);
		}
		ah.doubleValue = nuwN + ah.doubleValue;
		return ah.count != 0 ? ah.doubleValue / ah.count : null;
	}
	@Override
	public Object calculateUpdate(Object old, Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper,
			CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		double oldN;
		double nuwN;
		if (!(old instanceof Number)) {
			ah.count++;
			oldN = 0;
		} else {
			if (isFloat) {
				oldN = toDouble(old);
			} else {
				oldN = toLong(old);
			}
		}
		if (!(nuw instanceof Number)) {
			ah.count--;
			nuwN = 0;
		} else {
			if (isFloat) {
				nuwN = toDouble(nuw);
			} else {
				nuwN = toLong(nuw);
			}
		}
		ah.doubleValue = (nuwN - oldN) + ah.doubleValue;
		return ah.count != 0 ? ah.doubleValue / ah.count : null;
	}
	@Override
	public Object calculateDelete(Object old, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (old instanceof Number)
			ah.count--;
		else
			return ah.count != 0 ? ah.doubleValue / ah.count : null;
		double oldN;
		if (isFloat) {
			oldN = toDouble(old);
		} else {
			oldN = toLong(old);
		}
		ah.doubleValue = ah.doubleValue - oldN;
		return ah.count != 0 ? ah.doubleValue / ah.count : null;
	}
	static private double toDouble(Object old) {
		if (old instanceof Number)
			return ((Number) old).doubleValue();
		else
			return 0;
	}
	static private long toLong(Object old) {
		if (old instanceof Number)
			return ((Number) old).longValue();
		else
			return 0L;
	}

	@Override
	public boolean needsHelper() {
		return true;
	}
	public Object initHelper() {
		return new AggregateHelper();
	};
}
